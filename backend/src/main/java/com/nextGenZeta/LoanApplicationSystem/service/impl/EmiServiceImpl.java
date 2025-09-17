package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.commons.EmiConstants;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.EmiCalculationRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.PaymentUpdateRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.RepaymentScheduleDto;
import com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode;
import com.nextGenZeta.LoanApplicationSystem.exception.LoanApplyException;
import com.nextGenZeta.LoanApplicationSystem.mapper.EmiMapper;
import com.nextGenZeta.LoanApplicationSystem.model.entities.EmiCalculationResult;
import com.nextGenZeta.LoanApplicationSystem.model.entities.EmiScheduleItem;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.entities.RepaymentSchedule;
import com.nextGenZeta.LoanApplicationSystem.model.entities.RepaymentTransaction;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.model.enums.PaymentStatus;
import com.nextGenZeta.LoanApplicationSystem.repository.LoanApplyRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.RepaymentScheduleRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.RepaymentTransactionRepository;
import com.nextGenZeta.LoanApplicationSystem.service.EmiService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmiServiceImpl implements EmiService {

    private static final Logger logger = LoggerFactory.getLogger(EmiServiceImpl.class);

    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final RepaymentTransactionRepository repaymentTransactionRepository;
    private final EmiMapper emiMapper;

    @Autowired
    private LoanApplyRepository loanApplyRepository;

    @Autowired
    public EmiServiceImpl(
            RepaymentScheduleRepository repaymentScheduleRepository,
            RepaymentTransactionRepository repaymentTransactionRepository,
            EmiMapper emiMapper,
            TaskExecutor taskExecutor
    ) {
        this.repaymentScheduleRepository = repaymentScheduleRepository;
        this.repaymentTransactionRepository = repaymentTransactionRepository;
        this.emiMapper = emiMapper;
    }

    @Override
    public EmiCalculationResult calculateEmi(EmiCalculationRequest request) {
        logger.info("calculateEmi called with request={}", request);
        validateEmiRequest(request);

        double principal = request.getLoanAmount();
        double annualRate = request.getInterestRate();
        int tenureMonths = request.getTenureMonths();

        double monthlyRate = getMonthlyRate(annualRate);
        double emi = calculateMonthlyEmi(principal, monthlyRate, tenureMonths);

        double totalAmount = emi * tenureMonths;
        double totalInterest = totalAmount - principal;

        List<EmiScheduleItem> schedule = generateAmortizationSchedule(principal, monthlyRate, tenureMonths, emi);

        return EmiCalculationResult.builder()
                .loanAmount(principal)
                .interestRate(annualRate)
                .tenureMonths(tenureMonths)
                .monthlyEmi(emi)
                .totalInterest(totalInterest)
                .totalRepayment(totalAmount)
                .repaymentSchedule(schedule)
                .build();
    }

    @Override
    public EmiCalculationResult calculateEmiForLoan(Long loanId) {
        logger.info("calculateEmiForLoan called with loanId={}", loanId);
        LoanApplication loan = loanApplyRepository.findById(loanId)
                .orElseThrow(() -> new LoanApplyException(ErrorCode.LOAN_NOT_FOUND, "Loan not found for id: " + loanId));
        EmiCalculationRequest request = EmiCalculationRequest.builder()
                .loanAmount(loan.getAmount())
                .interestRate(EmiConstants.INTEREST_RATE)
                .tenureMonths(loan.getTenureMonths())
                .build();
        return calculateEmi(request);
    }

    @Override
    @Transactional
    public void createRepaymentScheduleForLoan(Long loanId) {
        logger.info("createRepaymentScheduleForLoan called with loanId={}", loanId);
        LoanApplication loan = loanApplyRepository.findById(loanId)
                .orElseThrow(() -> new LoanApplyException(ErrorCode.LOAN_NOT_FOUND, "Loan not found for id: " + loanId));

        double principal = loan.getAmount();
        double annualRate = EmiConstants.INTEREST_RATE;
        int tenureMonths = loan.getTenureMonths();
        double monthlyRate = getMonthlyRate(annualRate);
        double emi = calculateMonthlyEmi(principal, monthlyRate, tenureMonths);

        List<RepaymentSchedule> scheduleEntities = buildRepaymentSchedules(loanId, principal, monthlyRate, tenureMonths, emi);
        repaymentScheduleRepository.saveAll(scheduleEntities);
    }

    @Override
    public List<RepaymentScheduleDto> getRepaymentSchedule(Long loanId) {
        logger.info("getRepaymentSchedule called with loanId={}", loanId);
        List<RepaymentSchedule> schedules = repaymentScheduleRepository.findByLoanIdOrderByMonthNumber(loanId);
        if (schedules.isEmpty()) {
            logger.error("No repayment schedule found for loanId={}", loanId);
            throw new LoanApplyException(ErrorCode.LOAN_NOT_FOUND, EmiConstants.ERR_NO_SCHEDULE_FOUND + loanId);
        }
        return schedules.stream()
                .map(schedule -> {
                    RepaymentScheduleDto dto = emiMapper.toRepaymentScheduleDto(schedule);
                    Double totalPaid = repaymentTransactionRepository.getTotalPaidForSchedule(schedule.getId());
                    dto.setEmiAmount(schedule.getEmiAmount());
                    dto.setRemainingAmount(schedule.getEmiAmount() - totalPaid);
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    public RepaymentScheduleDto updatePaymentForLoan(Long loanId, PaymentUpdateRequest req) {
        logger.info("updatePaymentForLoan called with loanId={}, req={}", loanId, req);
        RepaymentSchedule repayment = repaymentScheduleRepository.findById(req.getRepaymentId())
                .orElseThrow(() -> new LoanApplyException(ErrorCode.LOAN_NOT_FOUND, EmiConstants.ERR_REPAYMENT_NOT_FOUND));

        validateRepaymentForLoan(loanId, req, repayment);

        List<RepaymentSchedule> nextToPayList = repaymentScheduleRepository.findNextToPayByLoanId(loanId);
        RepaymentSchedule nextToPay = nextToPayList.isEmpty() ? null : nextToPayList.get(0);

        if (nextToPay == null || !nextToPay.getId().equals(req.getRepaymentId())) {
            logger.error("You can only pay the next due EMI. loanId={}, repaymentId={}", loanId, req.getRepaymentId());
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, "You can only pay the next due EMI. Complete previous payments first.");
        }

        Double paidSoFar = repaymentTransactionRepository.getTotalPaidForSchedule(repayment.getId());
        double newTotalPaid = paidSoFar + req.getAmountPaid();

        if (newTotalPaid > repayment.getEmiAmount()) {
            logger.error("Total payment exceeds EMI amount. loanId={}, repaymentId={}, paidSoFar={}, newTotalPaid={}, emiAmount={}", loanId, req.getRepaymentId(), paidSoFar, newTotalPaid, repayment.getEmiAmount());
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, "Total payment exceeds EMI amount. Please pay only the remaining EMI.");
        }

        RepaymentTransaction transaction = RepaymentTransaction.builder()
                .repaymentScheduleId(repayment.getId())
                .amountPaid(req.getAmountPaid())
                .paymentDate(req.getPaymentDate())
                .build();
        repaymentTransactionRepository.save(transaction);

        repayment.setPaymentDate(req.getPaymentDate());
        repayment.setPaymentStatus(newTotalPaid < repayment.getEmiAmount() ? PaymentStatus.PARTIALLY_PAID : PaymentStatus.PAID);
        repaymentScheduleRepository.save(repayment);

        updateLoanStatusIfAllPaid(loanId);

        RepaymentScheduleDto dto = emiMapper.toRepaymentScheduleDto(repayment);
        Double totalPaidNow = repaymentTransactionRepository.getTotalPaidForSchedule(repayment.getId());
        dto.setEmiAmount(repayment.getEmiAmount());
        dto.setRemainingAmount(repayment.getEmiAmount() - totalPaidNow);
        return dto;
    }

    // --- Helper Methods ---

    private void validateEmiRequest(EmiCalculationRequest request) {
        if (request.getLoanAmount() == null || request.getLoanAmount() <= 0) {
            logger.error("Loan amount must be positive, got {}", request.getLoanAmount());
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, EmiConstants.ERR_LOAN_AMOUNT_POSITIVE);
        }
        if (request.getInterestRate() == null || request.getInterestRate() <= 0) {
            logger.error("Interest rate must be positive, got {}", request.getInterestRate());
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, EmiConstants.ERR_INTEREST_RATE_POSITIVE);
        }
        if (request.getTenureMonths() == null || request.getTenureMonths() < 1) {
            logger.error("Tenure months must be at least 1, got {}", request.getTenureMonths());
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, EmiConstants.ERR_TENURE_MIN);
        }
    }

    private double getMonthlyRate(double annualRate) {
        return (annualRate / 12) / EmiConstants.HUNDRED;
    }

    private double calculateMonthlyEmi(double principal, double monthlyRate, int tenureMonths) {
        double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths)) /
                (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return Math.round(emi * EmiConstants.HUNDRED) / EmiConstants.HUNDRED;
    }

    private List<EmiScheduleItem> generateAmortizationSchedule(double principal, double monthlyRate,
                                                               int tenureMonths, double emi) {
        List<EmiScheduleItem> schedule = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        double balance = principal;

        for (int month = 1; month <= tenureMonths; month++) {
            double interest = balance * monthlyRate;
            double monthlyPrincipal = emi - interest;
            balance -= monthlyPrincipal;
            if (balance < 0) balance = 0;
            LocalDate dueDate = startDate.plusMonths(month);

            EmiScheduleItem item = EmiScheduleItem.builder()
                    .monthNumber(month)
                    .dueDate(dueDate)
                    .principalAmount(Math.round(monthlyPrincipal * EmiConstants.HUNDRED) / EmiConstants.HUNDRED)
                    .interestAmount(Math.round(interest * EmiConstants.HUNDRED) / EmiConstants.HUNDRED)
                    .emiAmount(Math.round(emi * EmiConstants.HUNDRED) / EmiConstants.HUNDRED)
                    .balanceRemaining(Math.round(balance * EmiConstants.HUNDRED) / EmiConstants.HUNDRED)
                    .build();

            schedule.add(item);
        }
        return schedule;
    }

    private List<RepaymentSchedule> buildRepaymentSchedules(Long loanId, double principal, double monthlyRate,
                                                            int tenureMonths, double emi) {
        List<RepaymentSchedule> scheduleEntities = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        double balance = principal;

        for (int month = 1; month <= tenureMonths; month++) {
            double interest = balance * monthlyRate;
            double monthlyPrincipal = emi - interest;
            balance -= monthlyPrincipal;
            if (balance < 0) balance = 0;
            LocalDate dueDate = startDate.plusMonths(month);

            RepaymentSchedule entity = RepaymentSchedule.builder()
                    .loanId(loanId)
                    .monthNumber(month)
                    .dueDate(dueDate)
                    .principalAmount(Math.round(monthlyPrincipal * EmiConstants.HUNDRED) / EmiConstants.HUNDRED)
                    .interestAmount(Math.round(interest * EmiConstants.HUNDRED) / EmiConstants.HUNDRED)
                    .emiAmount(emi)
                    .balanceRemaining(Math.round(balance * EmiConstants.HUNDRED) / EmiConstants.HUNDRED)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();

            scheduleEntities.add(entity);
        }
        return scheduleEntities;
    }

    private void validateRepaymentForLoan(Long loanId, PaymentUpdateRequest req, RepaymentSchedule repayment) {
        if (!repayment.getLoanId().equals(loanId)) {
            logger.error("Repayment does not belong to the specified loan. loanId={}, repaymentId={}", loanId, req.getRepaymentId());
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, "Repayment does not belong to the specified loan.");
        }
    }

    private void updateLoanStatusIfAllPaid(Long loanId) {
        List<RepaymentSchedule> schedules = repaymentScheduleRepository.findByLoanIdOrderByMonthNumber(loanId);
        boolean allPaid = schedules.stream().allMatch(s -> s.getPaymentStatus() == PaymentStatus.PAID);
        if (allPaid) {
            LoanApplication loan = loanApplyRepository.findById(loanId).orElse(null);
            if (loan != null) {
                loan.setStatus(LoanStatus.PAID);
                loanApplyRepository.save(loan);
            }
        }
    }
}