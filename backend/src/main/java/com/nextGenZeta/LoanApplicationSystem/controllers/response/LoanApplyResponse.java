package com.nextGenZeta.LoanApplicationSystem.controllers.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanApplyResponse {

    private Long id;
    private Long userId;
    private Double amount;
    private Integer tenureMonths;
    private Double income;
    private Integer creditScore;
    private LoanStatus status;
    private String message;
    private LocalDateTime applicationDateTime;
    private String purpose;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewRemarks;
}
