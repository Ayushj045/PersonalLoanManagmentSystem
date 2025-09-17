package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.SupportTicketRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.SupportTicketResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.entities.SupportTicket;
import com.nextGenZeta.LoanApplicationSystem.repository.SupportTicketRepository;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupportTicketServiceImplTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;
    @Mock
    private LoanApplyService loanApplyService;

    private SupportTicketServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new SupportTicketServiceImpl(supportTicketRepository, loanApplyService);
    }

    @Test
    void createSupportTicket_success() {
        Long userId = 1L;
        Long loanId = 2L;
        SupportTicketRequest req = new SupportTicketRequest();
        req.setLoanId(loanId);
        req.setSubject("Subject");
        req.setDescription("Description");
        LoanApplication loan = LoanApplication.builder().id(loanId).build();
        when(loanApplyService.getLoanById(loanId)).thenReturn(loan);

        SupportTicket ticket = new SupportTicket();
        ticket.setId(10L);
        ticket.setUserId(userId);
        ticket.setLoanId(loanId);
        ticket.setSubject("Subject");
        ticket.setDescription("Description");
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        when(supportTicketRepository.save(any())).thenReturn(ticket);

        SupportTicketResponse resp = service.createSupportTicket(userId, req);
        assertEquals(10L, resp.getId());
        assertEquals("Support ticket created successfully", resp.getMessage());
        assertEquals(SupportTicket.TicketStatus.OPEN, resp.getStatus());
    }

    @Test
    void createSupportTicket_invalidLoanId_returnsError() {
        Long userId = 1L;
        SupportTicketRequest req = new SupportTicketRequest();
        req.setLoanId(99L);
        req.setSubject("Subject");
        req.setDescription("Description");
        when(loanApplyService.getLoanById(99L)).thenReturn(null);

        SupportTicketResponse resp = service.createSupportTicket(userId, req);
        assertNull(resp.getId());
        assertEquals("Invalid request: Loan ID does not exist", resp.getMessage());
    }

    @Test
    void createSupportTicket_invalidRequest_returnsError() {
        Long userId = 1L;
        Long loanId = 2L;
        SupportTicketRequest req = new SupportTicketRequest();
        req.setLoanId(loanId);
        req.setSubject("");
        req.setDescription("");
        LoanApplication loan = LoanApplication.builder().id(loanId).build();
        when(loanApplyService.getLoanById(loanId)).thenReturn(loan);

        SupportTicketResponse resp = service.createSupportTicket(userId, req);
        assertNull(resp.getId());
        assertEquals("Invalid request: userId, subject, and description are required", resp.getMessage());
    }

    @Test
    void getTicketsByUser_returnsList() {
        Long userId = 1L;
        SupportTicket ticket = new SupportTicket();
        ticket.setId(11L);
        ticket.setUserId(userId);
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        when(supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(ticket));

        List<SupportTicketResponse> respList = service.getTicketsByUser(userId);
        assertEquals(1, respList.size());
        assertEquals(11L, respList.get(0).getId());
        assertEquals("Ticket Under Review", respList.get(0).getMessage());
    }

    @Test
    void getTicketsByStatus_returnsList() {
        SupportTicket.TicketStatus status = SupportTicket.TicketStatus.OPEN;
        SupportTicket ticket = new SupportTicket();
        ticket.setId(12L);
        ticket.setStatus(status);
        ticket.setCreatedAt(LocalDateTime.now());
        when(supportTicketRepository.findByStatus(status)).thenReturn(List.of(ticket));

        List<SupportTicketResponse> respList = service.getTicketsByStatus("OPEN");
        assertEquals(1, respList.size());
        assertEquals(12L, respList.get(0).getId());
        assertEquals("Ticket Under Review", respList.get(0).getMessage());
    }

    @Test
    void getTicketById_found_returnsResponse() {
        Long ticketId = 13L;
        SupportTicket ticket = new SupportTicket();
        ticket.setId(ticketId);
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        SupportTicketResponse resp = service.getTicketById(ticketId);
        assertEquals(ticketId, resp.getId());
        assertEquals("Ticket Under Review", resp.getMessage());
    }

    @Test
    void getTicketById_notFound_returnsError() {
        Long ticketId = 14L;
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.empty());

        SupportTicketResponse resp = service.getTicketById(ticketId);
        assertNull(resp.getId());
        assertEquals("Ticket not found", resp.getMessage());
    }

    @Test
    void updateTicketResponse_found_updatesResponse() {
        Long ticketId = 15L;
        String responseText = "Resolved";
        SupportTicket ticket = new SupportTicket();
        ticket.setId(ticketId);
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(supportTicketRepository.save(any())).thenReturn(ticket);

        SupportTicketResponse resp = service.updateTicketResponse(ticketId, responseText);
        assertEquals(ticketId, resp.getId());
        assertEquals("Ticket response updated successfully", resp.getMessage());
    }

    @Test
    void updateTicketResponse_notFound_returnsError() {
        Long ticketId = 16L;
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.empty());

        SupportTicketResponse resp = service.updateTicketResponse(ticketId, "Response");
        assertNull(resp.getId());
        assertEquals("Ticket not found", resp.getMessage());
    }

    @Test
    void updateTicketStatus_found_updatesStatus() {
        Long ticketId = 17L;
        SupportTicket.TicketStatus status = SupportTicket.TicketStatus.RESOLVED;
        SupportTicket ticket = new SupportTicket();
        ticket.setId(ticketId);
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(supportTicketRepository.save(any())).thenReturn(ticket);

        SupportTicketResponse resp = service.updateTicketStatus(ticketId, status);
        assertEquals(ticketId, resp.getId());
        assertEquals("Ticket status updated successfully", resp.getMessage());
    }

    @Test
    void updateTicketStatus_notFound_returnsError() {
        Long ticketId = 18L;
        when(supportTicketRepository.findById(ticketId)).thenReturn(Optional.empty());

        SupportTicketResponse resp = service.updateTicketStatus(ticketId, SupportTicket.TicketStatus.OPEN);
        assertNull(resp.getId());
        assertEquals("Ticket not found", resp.getMessage());
    }
}