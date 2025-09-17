package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.SupportTicketRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.SupportTicketResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.SupportTicket;
import com.nextGenZeta.LoanApplicationSystem.repository.SupportTicketRepository;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import com.nextGenZeta.LoanApplicationSystem.service.SupportTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketServiceImpl.class);

    private final SupportTicketRepository supportTicketRepository;

    private final LoanApplyService loanApplyService;


    @Autowired
    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository, LoanApplyService loanApplyService) {
        this.supportTicketRepository = supportTicketRepository;
        this.loanApplyService = loanApplyService;
    }

    @Override
    public SupportTicketResponse createSupportTicket(Long userId, SupportTicketRequest request) {
        logger.info("createSupportTicket called with userId={}, request={}", userId, request);

        if(loanApplyService.getLoanById(request.getLoanId())!=null) {
            if (userId == null || request.getSubject() == null ||
                    request.getSubject().trim().isEmpty() || request.getDescription() == null ||
                    request.getDescription().trim().isEmpty()) {
                logger.error("Invalid request: userId, subject, and description are required for userId={}", userId);
                return buildErrorResponse("Invalid request: userId, subject, and description are required");
            }

            SupportTicket ticket = new SupportTicket();
            ticket.setUserId(userId);
            ticket.setLoanId(request.getLoanId());
            ticket.setSubject(request.getSubject().trim());
            ticket.setDescription(request.getDescription().trim());
            ticket.setStatus(SupportTicket.TicketStatus.OPEN);
            ticket.setCreatedAt(LocalDateTime.now());

            SupportTicket savedTicket = supportTicketRepository.save(ticket);

            return buildSuccessResponse(savedTicket, "Support ticket created successfully");
        }
        logger.error("Invalid request: Loan ID does not exist for userId={}, loanId={}", userId, request.getLoanId());
        return buildErrorResponse("Invalid request: Loan ID does not exist");
    }

    @Override
    public List<SupportTicketResponse> getTicketsByUser(Long userId) {
        logger.info("getTicketsByUser called with userId={}", userId);
        return supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ticket -> buildSuccessResponse(ticket, "Ticket Under Review"))
                .toList();
    }

    @Override
    public List<SupportTicketResponse> getTicketsByStatus(String status) {
        logger.info("getTicketsByStatus called with status={}", status);
        SupportTicket.TicketStatus ticketStatus = SupportTicket.TicketStatus.valueOf(status.toUpperCase());
        return supportTicketRepository.findByStatus(ticketStatus).stream()
                .map(ticket -> buildSuccessResponse(ticket, "Ticket Under Review"))
                .toList();
    }

    @Override
    public SupportTicketResponse getTicketById(Long ticketId) {
        logger.info("getTicketById called with ticketId={}", ticketId);
        SupportTicket supportTicket = supportTicketRepository.findById(ticketId).orElse(null);
        if (supportTicket != null) {
            return buildSuccessResponse(supportTicket, "Ticket Under Review");
        } else {
            logger.error("Ticket not found for ticketId={}", ticketId);
            return buildErrorResponse("Ticket not found");
        }
    }

    @Override
    public SupportTicketResponse updateTicketResponse(Long ticketId, String response) {
        logger.info("updateTicketResponse called with ticketId={}, response={}", ticketId, response);
        return supportTicketRepository.findById(ticketId)
                .map(ticket -> {
                    ticket.setResponse(response);
                    SupportTicket updatedTicket = supportTicketRepository.save(ticket);
                    return buildSuccessResponse(updatedTicket, "Ticket response updated successfully");
                })
                .orElseGet(() -> {
                    logger.error("Ticket not found for updateTicketResponse, ticketId={}", ticketId);
                    return buildErrorResponse("Ticket not found");
                });
    }

    @Override
    public SupportTicketResponse updateTicketStatus(Long ticketId, SupportTicket.TicketStatus status) {
        logger.info("updateTicketStatus called with ticketId={}, status={}", ticketId, status);
        return supportTicketRepository.findById(ticketId)
                .map(ticket -> {
                    ticket.setStatus(status);
                    SupportTicket updatedTicket = supportTicketRepository.save(ticket);
                    return buildSuccessResponse(updatedTicket, "Ticket status updated successfully");
                })
                .orElseGet(() -> {
                    logger.error("Ticket not found for updateTicketStatus, ticketId={}", ticketId);
                    return buildErrorResponse("Ticket not found");
                });
    }

    private SupportTicketResponse buildSuccessResponse(SupportTicket ticket, String message) {
        return SupportTicketResponse.builder()
                        .id(ticket.getId())
                        .userId(ticket.getUserId())
                        .loanId(ticket.getLoanId())
                        .subject(ticket.getSubject())
                        .description(ticket.getDescription())
                        .status(ticket.getStatus())
                        .createdAt(ticket.getCreatedAt())
                        .response(ticket.getResponse())
                        .message(message)
                        .build();
    }

    private SupportTicketResponse buildErrorResponse(String message) {
        return SupportTicketResponse.builder()
                .message(message)
                .build();
    }
}
