package com.nextGenZeta.LoanApplicationSystem.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketRequest {
    private Long loanId;
    private String subject;
    private String description;
}
