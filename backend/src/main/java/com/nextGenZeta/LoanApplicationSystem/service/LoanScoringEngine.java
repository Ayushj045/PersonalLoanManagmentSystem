package com.nextGenZeta.LoanApplicationSystem.service;


import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanScoreResponse;

public interface LoanScoringEngine {
    LoanScoreResponse calculateScore(Long id);
}
