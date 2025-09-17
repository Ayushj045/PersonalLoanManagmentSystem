package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanApplyRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode;
import com.nextGenZeta.LoanApplicationSystem.exception.LoanApplyException;
import com.nextGenZeta.LoanApplicationSystem.repository.CustomerProfileRepository;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.repository.dao.LoanApplyDao;
import com.nextGenZeta.LoanApplicationSystem.service.EmiService;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class LoanApplyServiceImpl implements LoanApplyService {

    private static final Logger logger = LoggerFactory.getLogger(LoanApplyServiceImpl.class);

    @Value("${loan.autorejection.enabled:true}")
    private boolean autoRejectionEnabled;
    private final LoanApplyDao loanDao;
    private final CustomerProfileRepository customerProfileRepository;
    private final EmiService emiService;

    @Autowired
    private Executor securityExecutor;

    @Override
    public LoanApplyResponse applyLoan(LoanApplyRequest request) {
        logger.info("applyLoan called with request={}", request);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);

        Boolean duplicateExists = loanDao.checkDuplicateWithin24Hours(request.getUserId(), twentyFourHoursAgo);
        if (autoRejectionEnabled && Boolean.TRUE.equals(duplicateExists)) {
            logger.error("Duplicate loan request within 24 hours for userId={}", request.getUserId());
            throw new LoanApplyException(ErrorCode.DUPLICATE_LOAN_REQUEST, "Duplicate loan request within 24 hours");
        }

        CustomerProfile customerProfile = customerProfileRepository.findByBaseUserId(request.getUserId()).orElse(null);
        if (customerProfile == null) {
            logger.error("Customer profile not found for userId={}", request.getUserId());
            throw new LoanApplyException(ErrorCode.RESOURCE_NOT_FOUND, "Customer profile not found for user ID: " + request.getUserId());
        }

        LoanApplication loanApp = LoanApplication.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .tenureMonths(request.getTenureMonths())
                .income(request.getIncome())
                .creditScore(request.getCreditScore())
                .purpose(request.getPurpose())
                .applicationDateTime(now)
                .status(LoanStatus.NEW)
                .build();

        if (autoRejectionEnabled) {
            String rejectionReason = validateLoan(request);
            if (rejectionReason != null) {
                logger.error("Loan auto-rejected for userId={}, reason={}", request.getUserId(), rejectionReason);
                loanApp.setStatus(LoanStatus.REJECTED);
                loanDao.save(loanApp);
                return buildResponse(loanApp.getId(), request, LoanStatus.REJECTED,
                        now, rejectionReason);
            }
        }

        LoanApplication saved = loanDao.save(loanApp);
        return buildResponse(saved.getId(), request, saved.getStatus(),
                now, "Loan application submitted successfully");
    }

    @Override
    public List<LoanApplication> getLoansByUser(Long userId) {
        logger.info("getLoansByUser called with userId={}", userId);
        return loanDao.getLoansByUser(userId);
    }

    @Override
    public LoanApplication getLoanById(Long loanId) {
        logger.info("getLoanById called with loanId={}", loanId);
        Optional<LoanApplication> optionalLoan = loanDao.getByLoanId(loanId);
        return optionalLoan.orElseThrow(() -> {
            logger.error("Loan not found with ID: {}", loanId);
            return new LoanApplyException(ErrorCode.RESOURCE_NOT_FOUND, "Loan not found with ID: " + loanId);
        });
    }

    @Override
    public LoanApplyPaginatedResponse getAllLoans(Integer page, Integer size) {
        logger.info("getAllLoans called with page={}, size={}", page, size);
        Pageable pageable;
        if (page == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(page, size);
        }
        Page<LoanApplication> pageResult = loanDao.findAll(pageable);
        return LoanApplyPaginatedResponse.builder()
                .loanApplications(pageResult.getContent())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .size(pageResult.getSize())
                .page(pageResult.getNumber())
                .build();
    }

    @Override
    public LoanApplyResponse updateLoanStatus(Long loanId, String status,
                                              Long reviewedBy, String reviewRemarks) {
        logger.info("updateLoanStatus called with loanId={}, status={}, reviewedBy={}, reviewRemarks={}", loanId, status, reviewedBy, reviewRemarks);
        Optional<LoanApplication> optionalLoan = loanDao.getByLoanId(loanId);
        if (optionalLoan.isEmpty()) {
            logger.error("Loan not found with ID: {}", loanId);
            throw new LoanApplyException(ErrorCode.RESOURCE_NOT_FOUND, "Loan not found with ID: " + loanId);
        }

        LoanApplication loan = optionalLoan.get();
        LoanStatus newStatus;
        try {
            newStatus = LoanStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid loan status: {}", status);
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, "Invalid loan status: " + status);
        }

        loan.setStatus(newStatus);
        loan.setReviewedBy(reviewedBy);
        loan.setReviewedAt(LocalDateTime.now());
        loan.setReviewRemarks(reviewRemarks);
        LoanApplication updatedLoan = loanDao.save(loan);
        if (updatedLoan.getStatus() == LoanStatus.APPROVED) {
            emiService.createRepaymentScheduleForLoan(updatedLoan.getId());
        }
        return LoanApplyResponse.builder()
                .id(updatedLoan.getId())
                .userId(updatedLoan.getUserId())
                .amount(updatedLoan.getAmount())
                .tenureMonths(updatedLoan.getTenureMonths())
                .income(updatedLoan.getIncome())
                .creditScore(updatedLoan.getCreditScore())
                .status(updatedLoan.getStatus())
                .applicationDateTime(updatedLoan.getApplicationDateTime())
                .purpose(updatedLoan.getPurpose())
                .reviewedBy(updatedLoan.getReviewedBy())
                .reviewedAt(updatedLoan.getReviewedAt())
                .reviewRemarks(updatedLoan.getReviewRemarks())
                .build();
    }

    @Override
    public Page<LoanApplication> getLoansByStatus(Long userId, String status, int page, int size) {
        logger.info("getLoansByStatus called with userId={}, status={}, page={}, size={}", userId, status, page, size);
        LoanStatus loanStatus;
        try {
            loanStatus = LoanStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid loan status: {}", status);
            throw new LoanApplyException(ErrorCode.VALIDATION_ERROR, "Invalid loan status: " + status);
        }
        return loanDao.getLoansByUserAndStatus(userId, loanStatus, page, size);
    }

    private String validateLoan(LoanApplyRequest request) {
        if (request.getIncome() < 25000) return "Income below minimum requirement";
        if (request.getCreditScore() < 300 || request.getCreditScore() > 900) return "Invalid credit score";
        if (request.getAmount() <= 0) return "Invalid loan amount";
        if (request.getAmount() > request.getIncome() * 20) return "Loan amount too high compared to income";
        if (request.getTenureMonths() < 6 || request.getTenureMonths() > 240)
            return "Loan tenure out of allowed range";
        if (request.getAmount() > 1000000 && (request.getPurpose() == null || request.getPurpose().isBlank()))
            return "Purpose required for large loan amount";
        return null;
    }

    private LoanApplyResponse buildResponse(Long id, LoanApplyRequest req, LoanStatus status,
                                            LocalDateTime applicationDateTime, String message) {
        return new LoanApplyResponse(
                id,
                req.getUserId(),
                req.getAmount(),
                req.getTenureMonths(),
                req.getIncome(),
                req.getCreditScore(),
                status,
                message,
                applicationDateTime,
                req.getPurpose(),
                null,
                null,
                null
        );
    }
}