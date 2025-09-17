package com.nextGenZeta.LoanApplicationSystem.controllers.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiCalculationRequest {

    @NotNull
    @Positive
    private Double loanAmount;

    @NotNull
    @Positive
    private Double interestRate;

    @NotNull
    @Min(value = 1)
    private Integer tenureMonths;
}