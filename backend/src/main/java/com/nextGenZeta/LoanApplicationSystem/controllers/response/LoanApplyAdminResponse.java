package com.nextGenZeta.LoanApplicationSystem.controllers.response;


import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplyAdminResponse {
    private Long id;
    private Long userId;
    private String userName;
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
