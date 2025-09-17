package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanDecisionDTO;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.repository.CustomerProfileRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.dao.LoanApplyDao;
import com.nextGenZeta.LoanApplicationSystem.service.AdminLoanService;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class AdminLoanServiceImpl implements AdminLoanService {

    private static final Logger logger = LoggerFactory.getLogger(AdminLoanServiceImpl.class);

    private final LoanApplyService loanApplyService;
    private final LoanApplyDao loanApplyDao;
    private final CustomerProfileRepository customerProfileRepository;

    public AdminLoanServiceImpl(LoanApplyDao loanApplyDao, LoanApplyService loanApplyService, CustomerProfileRepository customerProfileRepository) {
        this.loanApplyDao = loanApplyDao;
        this.loanApplyService = loanApplyService;
        this.customerProfileRepository = customerProfileRepository;
    }

    @Override
    public LoanApplyAdminPaginatedResponse getLoansByStatus(String status, Integer page, Integer size) {
        logger.info("getLoansByStatus called with status={}, page={}, size={}", status, page, size);
        LoanStatus loanStatus = LoanStatus.valueOf(status);
        Page<LoanApplication> pageResult = loanApplyDao.getLoansByStatus(loanStatus, page, size);
        return LoanApplyAdminPaginatedResponse.builder()
                        .loanApplications(pageResult.getContent().stream()
                                .map(this::loanApplyResponseBuilder)
                                .toList())
                        .totalPages(pageResult.getTotalPages())
                        .size(pageResult.getSize())
                        .page(pageResult.getNumber())
                        .build();
    }

    @Override
    public LoanApplyAdminResponse getLoanByLoanId(Long loanId) {
        logger.info("getLoanByLoanId called with loanId={}", loanId);
        LoanApplication loan = loanApplyService.getLoanById(loanId);
        return loanApplyResponseBuilder(loan);
    }

    @Override
    public LoanApplyAdminPaginatedResponse getAllLoans(Integer page, Integer size) {
        logger.info("getAllLoans called with page={}, size={}", page, size);
        Page<LoanApplication> pageResult = loanApplyDao.getAllLoans(page, size);
        return LoanApplyAdminPaginatedResponse.builder()
                        .loanApplications(pageResult.getContent().stream()
                                .map(this::loanApplyResponseBuilder)
                                .toList())
                        .totalPages(pageResult.getTotalPages())
                        .size(pageResult.getSize())
                        .page(pageResult.getNumber())
                        .build();
    }

    @Override
    @Transactional
    public LoanApplyResponse reviewLoan(Long loanId, LoanDecisionDTO decision) {
        logger.info("reviewLoan called with loanId={}, decision={}", loanId, decision);
        return loanApplyService.updateLoanStatus(loanId, decision.getStatus(), decision.getAdminId(), decision.getRemarks());
    }

    private LoanApplyAdminResponse loanApplyResponseBuilder(LoanApplication loan) {
        logger.info("loanApplyResponseBuilder called for loanId={}", loan.getId());
        CustomerProfile customerProfile = customerProfileRepository.findByBaseUserId(loan.getUserId()).orElse(null);
        return LoanApplyAdminResponse.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .userName(customerProfile != null ? customerProfile.getFullName() : "Unknown User")
                .amount(loan.getAmount())
                .tenureMonths(loan.getTenureMonths())
                .income(loan.getIncome())
                .creditScore(loan.getCreditScore())
                .purpose(loan.getPurpose())
                .applicationDateTime(loan.getApplicationDateTime())
                .status(loan.getStatus())
                .reviewedBy(loan.getReviewedBy())
                .reviewRemarks(loan.getReviewRemarks())
                .build();
    }
}
