package com.nextGenZeta.LoanApplicationSystem.model.entities;

import com.nextGenZeta.LoanApplicationSystem.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "repayment_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "month_number")
    private Integer monthNumber;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "principal_amount")
    private Double principalAmount;

    @Column(name = "interest_amount")
    private Double interestAmount;

    @Column(name = "emi_amount")
    private Double emiAmount;

    @Column(name = "balance_remaining")
    private Double balanceRemaining;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}