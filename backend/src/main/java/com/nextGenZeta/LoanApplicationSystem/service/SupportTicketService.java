package com.nextGenZeta.LoanApplicationSystem.service;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.SupportTicketRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.SupportTicketResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.SupportTicket;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SupportTicketService {

    SupportTicketResponse createSupportTicket(Long userId, SupportTicketRequest request);

    List<SupportTicketResponse> getTicketsByUser(Long userId);

    List<SupportTicketResponse> getTicketsByStatus(String status);

    SupportTicketResponse getTicketById(Long ticketId);

    SupportTicketResponse updateTicketResponse(Long ticketId, String response);

    SupportTicketResponse updateTicketStatus(Long ticketId, SupportTicket.TicketStatus status);
}
