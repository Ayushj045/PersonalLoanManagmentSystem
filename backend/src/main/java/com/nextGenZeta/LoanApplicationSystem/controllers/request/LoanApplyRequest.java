package com.nextGenZeta.LoanApplicationSystem.controllers.request;


import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplyRequest {

    private Long userId;

    @Min(value = 1, message = "Loan amount must be greater than 0")
    private Double amount;

    private Integer tenureMonths;
    private Double income;
    private Integer creditScore;
    private String purpose;
}
