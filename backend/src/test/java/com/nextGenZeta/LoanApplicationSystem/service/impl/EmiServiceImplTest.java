package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.EmiCalculationRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.PaymentUpdateRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.RepaymentScheduleDto;
import com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode;
import com.nextGenZeta.LoanApplicationSystem.exception.LoanApplyException;
import com.nextGenZeta.LoanApplicationSystem.mapper.EmiMapper;
import com.nextGenZeta.LoanApplicationSystem.model.entities.EmiCalculationResult;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.entities.RepaymentSchedule;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.model.enums.PaymentStatus;
import com.nextGenZeta.LoanApplicationSystem.repository.LoanApplyRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.RepaymentScheduleRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.RepaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmiServiceImplTest {

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;
    @Mock
    private RepaymentTransactionRepository repaymentTransactionRepository;
    @Mock
    private EmiMapper emiMapper;
    @Mock
    private LoanApplyRepository loanApplyRepository;

    private EmiServiceImpl emiService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        emiService = new EmiServiceImpl(repaymentScheduleRepository, repaymentTransactionRepository, emiMapper, null);
        Field field = EmiServiceImpl.class.getDeclaredField("loanApplyRepository");
        field.setAccessible(true);
        field.set(emiService, loanApplyRepository);
    }

    @Test
    void calculateEmi_validRequest() {
        EmiCalculationRequest req = EmiCalculationRequest.builder()
                .loanAmount(10000.0)
                .interestRate(10.0)
                .tenureMonths(12)
                .build();
        EmiCalculationResult result = emiService.calculateEmi(req);
        assertNotNull(result);
        assertEquals(10000.0, result.getLoanAmount());
        assertEquals(12, result.getTenureMonths());
        assertTrue(result.getMonthlyEmi() > 0);
    }

    @Test
    void calculateEmi_invalidLoanAmount() {
        EmiCalculationRequest req = EmiCalculationRequest.builder()
                .loanAmount(-1.0)
                .interestRate(10.0)
                .tenureMonths(12)
                .build();
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.calculateEmi(req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    void calculateEmi_invalidInterestRate() {
        EmiCalculationRequest req = EmiCalculationRequest.builder()
                .loanAmount(10000.0)
                .interestRate(0.0)
                .tenureMonths(12)
                .build();
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.calculateEmi(req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    void calculateEmi_invalidTenure() {
        EmiCalculationRequest req = EmiCalculationRequest.builder()
                .loanAmount(10000.0)
                .interestRate(10.0)
                .tenureMonths(0)
                .build();
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.calculateEmi(req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    void calculateEmiForLoan_validLoan() {
        LoanApplication loan = LoanApplication.builder()
                .id(1L)
                .amount(5000.0)
                .tenureMonths(6)
                .build();
        when(loanApplyRepository.findById(1L)).thenReturn(Optional.of(loan));
        EmiCalculationResult result = emiService.calculateEmiForLoan(1L);
        assertNotNull(result);
        assertEquals(5000.0, result.getLoanAmount());
        assertEquals(6, result.getTenureMonths());
    }

    @Test
    void calculateEmiForLoan_loanNotFound() {
        when(loanApplyRepository.findById(2L)).thenReturn(Optional.empty());
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.calculateEmiForLoan(2L));
        assertEquals(ErrorCode.LOAN_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void createRepaymentScheduleForLoan_validLoan() {
        LoanApplication loan = LoanApplication.builder()
                .id(1L)
                .amount(1000.0)
                .tenureMonths(2)
                .build();
        when(loanApplyRepository.findById(1L)).thenReturn(Optional.of(loan));
        emiService.createRepaymentScheduleForLoan(1L);
        verify(repaymentScheduleRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createRepaymentScheduleForLoan_loanNotFound() {
        when(loanApplyRepository.findById(2L)).thenReturn(Optional.empty());
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.createRepaymentScheduleForLoan(2L));
        assertEquals(ErrorCode.LOAN_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getRepaymentSchedule_validLoan() {
        RepaymentSchedule schedule = RepaymentSchedule.builder()
                .id(1L)
                .loanId(1L)
                .emiAmount(100.0)
                .build();
        List<RepaymentSchedule> schedules = List.of(schedule);
        when(repaymentScheduleRepository.findByLoanIdOrderByMonthNumber(1L)).thenReturn(schedules);
        when(emiMapper.toRepaymentScheduleDto(schedule)).thenReturn(new RepaymentScheduleDto());
        when(repaymentTransactionRepository.getTotalPaidForSchedule(1L)).thenReturn(50.0);

        List<RepaymentScheduleDto> dtos = emiService.getRepaymentSchedule(1L);
        assertEquals(1, dtos.size());
    }

    @Test
    void getRepaymentSchedule_noSchedules() {
        when(repaymentScheduleRepository.findByLoanIdOrderByMonthNumber(2L)).thenReturn(new ArrayList<>());
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.getRepaymentSchedule(2L));
        assertEquals(ErrorCode.LOAN_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updatePaymentForLoan_validPayment() {
        RepaymentSchedule repayment = RepaymentSchedule.builder()
                .id(1L)
                .loanId(1L)
                .emiAmount(100.0)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        PaymentUpdateRequest req = PaymentUpdateRequest.builder()
                .repaymentId(1L)
                .amountPaid(100.0)
                .paymentDate(LocalDate.now())
                .build();
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repayment));
        when(repaymentScheduleRepository.findNextToPayByLoanId(1L)).thenReturn(List.of(repayment));
        when(repaymentTransactionRepository.getTotalPaidForSchedule(1L)).thenReturn(0.0, 100.0);
        when(repaymentScheduleRepository.findByLoanIdOrderByMonthNumber(1L)).thenReturn(List.of(repayment));
        when(emiMapper.toRepaymentScheduleDto(repayment)).thenReturn(new RepaymentScheduleDto());
        when(loanApplyRepository.findById(1L)).thenReturn(Optional.of(LoanApplication.builder().id(1L).status(LoanStatus.APPROVED).build()));

        RepaymentScheduleDto dto = emiService.updatePaymentForLoan(1L, req);
        assertNotNull(dto);
        verify(repaymentTransactionRepository, times(1)).save(any());
        verify(repaymentScheduleRepository, times(1)).save(repayment);
    }

    @Test
    void updatePaymentForLoan_wrongLoan() {
        RepaymentSchedule repayment = RepaymentSchedule.builder()
                .id(1L)
                .loanId(2L)
                .emiAmount(100.0)
                .build();
        PaymentUpdateRequest req = PaymentUpdateRequest.builder()
                .repaymentId(1L)
                .amountPaid(100.0)
                .paymentDate(LocalDate.now())
                .build();
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repayment));
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.updatePaymentForLoan(1L, req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    void updatePaymentForLoan_notNextToPay() {
        RepaymentSchedule repayment = RepaymentSchedule.builder()
                .id(1L)
                .loanId(1L)
                .emiAmount(100.0)
                .build();
        PaymentUpdateRequest req = PaymentUpdateRequest.builder()
                .repaymentId(1L)
                .amountPaid(100.0)
                .paymentDate(LocalDate.now())
                .build();
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repayment));
        when(repaymentScheduleRepository.findNextToPayByLoanId(1L)).thenReturn(new ArrayList<>());
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.updatePaymentForLoan(1L, req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    void updatePaymentForLoan_overPayment() {
        RepaymentSchedule repayment = RepaymentSchedule.builder()
                .id(1L)
                .loanId(1L)
                .emiAmount(100.0)
                .build();
        PaymentUpdateRequest req = PaymentUpdateRequest.builder()
                .repaymentId(1L)
                .amountPaid(150.0)
                .paymentDate(LocalDate.now())
                .build();
        when(repaymentScheduleRepository.findById(1L)).thenReturn(Optional.of(repayment));
        when(repaymentScheduleRepository.findNextToPayByLoanId(1L)).thenReturn(List.of(repayment));
        when(repaymentTransactionRepository.getTotalPaidForSchedule(1L)).thenReturn(60.0);
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> emiService.updatePaymentForLoan(1L, req));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }
}