package com.nextGenZeta.LoanApplicationSystem.controllers.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUpdateRequest {
    @NotNull
    private Long repaymentId;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    private Double amountPaid;
}