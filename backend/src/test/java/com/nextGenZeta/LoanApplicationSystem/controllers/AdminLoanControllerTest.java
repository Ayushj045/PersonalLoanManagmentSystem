package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanDecisionDTO;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanScoreResponse;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.service.AdminLoanService;
import com.nextGenZeta.LoanApplicationSystem.service.LoanScoringEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLoanControllerTest {

    @Mock
    private AdminLoanService adminLoanService;
    @Mock
    private LoanScoringEngine loanScoringEngine;

    private AdminLoanController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AdminLoanController(adminLoanService, loanScoringEngine);
    }

    @Test
    void getPendingLoans_returnsResponse() {
        LoanApplyAdminPaginatedResponse resp = LoanApplyAdminPaginatedResponse.builder().build();
        when(adminLoanService.getLoansByStatus(LoanStatus.NEW.name(), 0, 10)).thenReturn(resp);
        assertSame(resp, controller.getPendingLoans(0, 10));
    }

    @Test
    void getAllLoans_returnsResponse() {
        LoanApplyAdminPaginatedResponse resp = LoanApplyAdminPaginatedResponse.builder().build();
        when(adminLoanService.getAllLoans(1, 5)).thenReturn(resp);
        assertSame(resp, controller.getAllLoans(1, 5));
    }

    @Test
    void getLoansByStatus_returnsResponse() {
        LoanApplyAdminPaginatedResponse resp = LoanApplyAdminPaginatedResponse.builder().build();
        when(adminLoanService.getLoansByStatus("APPROVED", 2, 3)).thenReturn(resp);
        assertSame(resp, controller.getLoansByStatus("APPROVED", 2, 3));
    }

    @Test
    void getLoanById_returnsResponse() {
        LoanApplyAdminResponse resp = LoanApplyAdminResponse.builder().id(99L).build();
        when(adminLoanService.getLoanByLoanId(99L)).thenReturn(resp);
        assertSame(resp, controller.getLoanById(99L));
    }

    @Test
    void reviewLoan_returnsResponse() {
        LoanDecisionDTO dto = new LoanDecisionDTO();
        LoanApplyResponse resp = LoanApplyResponse.builder().id(88L).build();
        when(adminLoanService.reviewLoan(88L, dto)).thenReturn(resp);
        assertSame(resp, controller.reviewLoan(88L, dto));
    }

    @Test
    void getLoanScore_returnsResponse() {
        LoanScoreResponse resp = LoanScoreResponse.builder().score(100).build();
        when(loanScoringEngine.calculateScore(77L)).thenReturn(resp);
        assertSame(resp, controller.getLoanScore(77L));
    }
}