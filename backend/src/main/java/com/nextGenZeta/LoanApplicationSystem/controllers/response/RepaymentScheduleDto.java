package com.nextGenZeta.LoanApplicationSystem.controllers.response;

import com.nextGenZeta.LoanApplicationSystem.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentScheduleDto {
    private Long id;
    private Long loanId;
    private Integer monthNumber;
    private LocalDate dueDate;
    private Double principalAmount;
    private Double interestAmount;
    private Double emiAmount;
    private Double balanceRemaining;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate;
    private Double remainingAmount;
}