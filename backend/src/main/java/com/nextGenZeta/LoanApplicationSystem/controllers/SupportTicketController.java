package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.SupportTicketRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.SupportTicketResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.SupportTicket;
import com.nextGenZeta.LoanApplicationSystem.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class SupportTicketController {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketController.class);

    private final SupportTicketService supportTicketService;

    @PostMapping("/user/{userId}")
    public SupportTicketResponse createSupportTicket(@PathVariable Long userId, @RequestBody SupportTicketRequest request) {
        logger.info("createSupportTicket called with userId={}, request={}", userId, request);
        return supportTicketService.createSupportTicket(userId, request);
    }

    @GetMapping("/user/{userId}")
    public List<SupportTicketResponse> getTicketsByUser(@PathVariable Long userId) {
        logger.info("getTicketsByUser called with userId={}", userId);
        return supportTicketService.getTicketsByUser(userId);
    }

    @GetMapping("/{ticketId}")
    public SupportTicketResponse getTicketById(@PathVariable Long ticketId) {
        logger.info("getTicketById called with ticketId={}", ticketId);
        return supportTicketService.getTicketById(ticketId);
    }

    @PutMapping("/admin/{ticketId}/response")
    public SupportTicketResponse updateTicketResponse(
            @PathVariable Long ticketId,
            @RequestBody String response) {
        logger.info("updateTicketResponse called with ticketId={}, response={}", ticketId, response);
        return supportTicketService.updateTicketResponse(ticketId, response);
    }

    @GetMapping("/admin/status/{status}")
    public List<SupportTicketResponse> getTicketsByStatus(@PathVariable String status) {
        logger.info("getTicketsByStatus called with status={}", status);
        return supportTicketService.getTicketsByStatus(status);
    }

    @PutMapping("/admin/{ticketId}/status")
    public SupportTicketResponse updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam SupportTicket.TicketStatus status) {
        logger.info("updateTicketStatus called with ticketId={}, status={}", ticketId, status);
        return supportTicketService.updateTicketStatus(ticketId, status);
    }
}
