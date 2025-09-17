package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanScoreResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.model.enums.RiskFactor;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanScoringEngineImplTest {

    @Mock
    private LoanApplyService loanApplyService;

    private LoanScoringEngineImpl scoringEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scoringEngine = new LoanScoringEngineImpl(loanApplyService);
    }

    @Test
    void calculateScore_highRisk() {
        LoanApplication loan = LoanApplication.builder()
                .id(1L)
                .userId(2L)
                .amount(100000.0)
                .income(10000.0)
                .creditScore(400)
                .status(LoanStatus.NEW)
                .build();
        when(loanApplyService.getLoanById(1L)).thenReturn(loan);
        when(loanApplyService.getLoansByUser(2L)).thenReturn(List.of(
                LoanApplication.builder().amount(90000.0).status(LoanStatus.APPROVED).build()
        ));

        LoanScoreResponse resp = scoringEngine.calculateScore(1L);
        assertEquals(RiskFactor.HIGH, resp.getRiskFactor());
        assertTrue(resp.getScore() <= 30);
    }

    @Test
    void calculateScore_mediumRisk() {
        LoanApplication loan = LoanApplication.builder()
                .id(2L)
                .userId(3L)
                .amount(30000.0)
                .income(50000.0)
                .creditScore(650)
                .status(LoanStatus.NEW)
                .build();
        // DTI low, credit score medium, loan-to-income medium
        when(loanApplyService.getLoanById(2L)).thenReturn(loan);
        when(loanApplyService.getLoansByUser(3L)).thenReturn(List.of(
                LoanApplication.builder().amount(20000.0).status(LoanStatus.APPROVED).build()
        ));

        LoanScoreResponse resp = scoringEngine.calculateScore(2L);
        assertEquals(RiskFactor.LOW, resp.getRiskFactor());
        assertTrue(resp.getScore() > 60);
    }

    @Test
    void calculateScore_lowRisk() {
        LoanApplication loan = LoanApplication.builder()
                .id(3L)
                .userId(4L)
                .amount(10000.0)
                .income(100000.0)
                .creditScore(800)
                .status(LoanStatus.NEW)
                .build();
        when(loanApplyService.getLoanById(3L)).thenReturn(loan);
        when(loanApplyService.getLoansByUser(4L)).thenReturn(List.of(
                LoanApplication.builder().amount(10000.0).status(LoanStatus.APPROVED).build()
        ));

        LoanScoreResponse resp = scoringEngine.calculateScore(3L);
        assertEquals(RiskFactor.LOW, resp.getRiskFactor());
        assertTrue(resp.getScore() > 60);
    }

    @Test
    void calculateScore_handlesZeroIncome() {
        LoanApplication loan = LoanApplication.builder()
                .id(4L)
                .userId(5L)
                .amount(5000.0)
                .income(0.0)
                .creditScore(700)
                .status(LoanStatus.NEW)
                .build();
        when(loanApplyService.getLoanById(4L)).thenReturn(loan);
        when(loanApplyService.getLoansByUser(5L)).thenReturn(List.of());

        LoanScoreResponse resp = scoringEngine.calculateScore(4L);
        assertEquals(RiskFactor.MEDIUM, resp.getRiskFactor());
        assertTrue(resp.getScore() > 30 && resp.getScore() <= 60);
    }

    @Test
    void calculateScore_handlesNoApprovedLoans() {
        LoanApplication loan = LoanApplication.builder()
                .id(5L)
                .userId(6L)
                .amount(10000.0)
                .income(50000.0)
                .creditScore(750)
                .status(LoanStatus.NEW)
                .build();
        when(loanApplyService.getLoanById(5L)).thenReturn(loan);
        when(loanApplyService.getLoansByUser(6L)).thenReturn(List.of(
                LoanApplication.builder().amount(10000.0).status(LoanStatus.REJECTED).build()
        ));

        LoanScoreResponse resp = scoringEngine.calculateScore(5L);
        assertEquals(RiskFactor.LOW, resp.getRiskFactor());
        assertTrue(resp.getScore() > 60);
    }
}