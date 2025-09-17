package com.nextGenZeta.LoanApplicationSystem.controllers;


import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanDecisionDTO;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.*;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.service.AdminLoanService;
import com.nextGenZeta.LoanApplicationSystem.service.LoanScoringEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/loans")
public class AdminLoanController {

    private static final Logger logger = LoggerFactory.getLogger(AdminLoanController.class);

    private final AdminLoanService adminLoanService;
    private final LoanScoringEngine loanScoringEngine;

    public AdminLoanController(AdminLoanService adminLoanService, LoanScoringEngine loanScoringEngine) {
        this.adminLoanService = adminLoanService;
        this.loanScoringEngine = loanScoringEngine;
    }

    @GetMapping("/pending")
    public LoanApplyAdminPaginatedResponse getPendingLoans(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        logger.info("getPendingLoans called with page={}, size={}", page, size);
        return adminLoanService.getLoansByStatus(LoanStatus.NEW.name(), page, size);
    }

    @GetMapping("/all")
    public LoanApplyAdminPaginatedResponse getAllLoans(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        logger.info("getAllLoans called with page={}, size={}", page, size);
        return adminLoanService.getAllLoans(page, size);
    }

    @GetMapping("/status/{status}")
    public LoanApplyAdminPaginatedResponse getLoansByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        logger.info("getLoansByStatus called with status={}, page={}, size={}", status, page, size);
        return adminLoanService.getLoansByStatus(status, page, size);
    }

    @GetMapping("/{loanId}")
    public LoanApplyAdminResponse getLoanById(@PathVariable Long loanId) {
        logger.info("getLoanById called with loanId={}", loanId);
        return adminLoanService.getLoanByLoanId(loanId);
    }

    @PutMapping("/{loanId}/status")
    public LoanApplyResponse reviewLoan(
            @PathVariable Long loanId,
            @RequestBody LoanDecisionDTO decision
    ) {
        logger.info("reviewLoan called with loanId={}, decision={}", loanId, decision);
        return adminLoanService.reviewLoan(loanId, decision);
    }

    @GetMapping("/{loanId}/score")
    public LoanScoreResponse getLoanScore(@PathVariable Long loanId) {
        logger.info("getLoanScore called with loanId={}", loanId);
        return loanScoringEngine.calculateScore(loanId);
    }



}
