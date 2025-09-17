package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanScoreResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.model.enums.RiskFactor;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import com.nextGenZeta.LoanApplicationSystem.service.LoanScoringEngine;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@Service
public class LoanScoringEngineImpl implements LoanScoringEngine {

    private static final Logger logger = LoggerFactory.getLogger(LoanScoringEngineImpl.class);

    private final LoanApplyService loanApplyService;

    public LoanScoringEngineImpl(LoanApplyService loanApplyService) {
        this.loanApplyService = loanApplyService;
    }

    @Override
    public LoanScoreResponse calculateScore(Long id) {
        logger.info("calculateScore called with id={}", id);
        LoanApplication loanApplication = loanApplyService.getLoanById(id);
        int score = calculateScore(loanApplication);
        return convertToLoanScoreResponse(score);
    }

    private LoanScoreResponse convertToLoanScoreResponse(Integer score) {
        logger.info("convertToLoanScoreResponse called with score={}", score);
        RiskFactor riskFactor = RiskFactor.LOW;
        if (score <= 30) {
            riskFactor = RiskFactor.HIGH;
        } else if (score <= 60) {
            riskFactor = RiskFactor.MEDIUM;
        }
        return LoanScoreResponse.builder()
                .score(score)
                .riskFactor(riskFactor)
                .build();
    }

    private int calculateScore(LoanApplication loanApplication) {
        logger.info("calculateScore called for loanId={}", loanApplication.getId());
        int score = getScoreBasedOnDTI(loanApplication);
        score = getScoreBasedOnCreditScore(loanApplication, score);
        score = getScoreBasedOnLoanToIncomeRatio(loanApplication, loanApplication.getIncome(), score);
        return score;
    }

    private int getScoreBasedOnLoanToIncomeRatio(LoanApplication loanApplication, double income, int score) {
        logger.info("getScoreBasedOnLoanToIncomeRatio called for loanId={}, income={}, score={}", loanApplication.getId(), income, score);
        double loanAmount = loanApplication.getAmount();
        double loanToIncome = (income > 0) ? (loanAmount / income) : Double.MAX_VALUE;

        if (loanToIncome < 5) {
            score += 20;
        } else if (loanToIncome <= 10) {
            score += 10;
        }
        return score;
    }

    private int getScoreBasedOnDTI(LoanApplication loanApplication) {
        logger.info("getScoreBasedOnDTI called for loanId={}", loanApplication.getId());
        List<LoanApplication> loansList = loanApplyService.getLoansByUser(loanApplication.getUserId());
        Optional<Double> totalDebt = loansList.stream()
                .filter(loan -> LoanStatus.APPROVED.equals(loan.getStatus()))
                .map(LoanApplication::getAmount)
                .reduce(Double::sum);
        double income = loanApplication.getIncome();
        double amount = totalDebt.orElse(0.0);
        double dti = (income > 0) ? (amount / income) * 100 : 100;
        if (dti < 30) {
            return 30;
        } else if (dti <= 50) {
            return 15;
        } else {
            return 5;
        }
    }

    private int getScoreBasedOnCreditScore(LoanApplication loanApplication, int score) {
        logger.info("getScoreBasedOnCreditScore called for loanId={}, creditScore={}, score={}", loanApplication.getId(), loanApplication.getCreditScore(), score);
        int creditScore = loanApplication.getCreditScore();
        if (creditScore < 500) {
            score += 10;
        } else if (creditScore < 700) {
            score += 30;
        } else {
            score += 50;
        }
        return score;
    }
}
