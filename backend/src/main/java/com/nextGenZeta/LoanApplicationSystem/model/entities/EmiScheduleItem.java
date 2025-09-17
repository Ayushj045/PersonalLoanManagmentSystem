package com.nextGenZeta.LoanApplicationSystem.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmiScheduleItem {
    private Integer monthNumber;
    private LocalDate dueDate;
    private Double principalAmount;
    private Double interestAmount;
    private Double emiAmount;
    private Double balanceRemaining;
}