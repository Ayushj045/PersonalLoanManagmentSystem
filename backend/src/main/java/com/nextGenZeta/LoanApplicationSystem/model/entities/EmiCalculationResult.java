package com.nextGenZeta.LoanApplicationSystem.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiCalculationResult {
    private Double loanAmount;
    private Double interestRate;
    private Integer tenureMonths;
    private Double monthlyEmi;
    private Double totalInterest;
    private Double totalRepayment;
    private List<EmiScheduleItem> repaymentSchedule;
}