package com.nextGenZeta.LoanApplicationSystem.controllers.request;

import lombok.Data;

@Data
public class LoanDecisionDTO {
    private String status;   // APPROVED or REJECTED
    private String remarks;  // optional
    private Long adminId;    // reviewer
}

