package com.nextGenZeta.LoanApplicationSystem.controllers.response;

import com.nextGenZeta.LoanApplicationSystem.model.entities.SupportTicket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketResponse {

    private Long id;
    private Long userId;
    private Long loanId;
    private String subject;
    private String description;
    private SupportTicket.TicketStatus status;
    private LocalDateTime createdAt;
    private String response;
    private String message;
}
