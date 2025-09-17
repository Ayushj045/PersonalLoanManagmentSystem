package com.nextGenZeta.LoanApplicationSystem.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long repaymentScheduleId;

    private Double amountPaid;

    private LocalDate paymentDate;
}