package com.nextGenZeta.LoanApplicationSystem.controllers.response;

import com.nextGenZeta.LoanApplicationSystem.model.enums.RiskFactor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanScoreResponse {
    private int score;

    private RiskFactor riskFactor;

}
