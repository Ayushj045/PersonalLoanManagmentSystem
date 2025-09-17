package com.nextGenZeta.LoanApplicationSystem.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "loan_application", schema = "public")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false)
    private Double income;

    @Column(name = "credit_score", nullable = false)
    private Integer creditScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(name = "application_date", nullable = false)
    private LocalDateTime applicationDateTime;

    @Column
    private String purpose;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_remarks")
    private String reviewRemarks;



}