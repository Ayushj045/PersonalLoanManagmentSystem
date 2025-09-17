package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.SupportTicketRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.SupportTicketResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.SupportTicket;
import com.nextGenZeta.LoanApplicationSystem.service.SupportTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupportTicketControllerTest {

    @Mock
    private SupportTicketService supportTicketService;

    private SupportTicketController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SupportTicketController(supportTicketService);
    }

    @Test
    void createSupportTicket_returnsResponse() {
        Long userId = 1L;
        SupportTicketRequest req = new SupportTicketRequest();
        SupportTicketResponse resp = SupportTicketResponse.builder().id(10L).build();
        when(supportTicketService.createSupportTicket(userId, req)).thenReturn(resp);
        assertSame(resp, controller.createSupportTicket(userId, req));
    }

    @Test
    void getTicketsByUser_returnsList() {
        Long userId = 2L;
        SupportTicketResponse resp = SupportTicketResponse.builder().id(3L).build();
        List<SupportTicketResponse> list = List.of(resp);
        when(supportTicketService.getTicketsByUser(userId)).thenReturn(list);
        assertSame(list, controller.getTicketsByUser(userId));
    }

    @Test
    void getTicketById_returnsResponse() {
        Long ticketId = 4L;
        SupportTicketResponse resp = SupportTicketResponse.builder().id(ticketId).build();
        when(supportTicketService.getTicketById(ticketId)).thenReturn(resp);
        assertSame(resp, controller.getTicketById(ticketId));
    }

    @Test
    void updateTicketResponse_returnsResponse() {
        Long ticketId = 5L;
        String responseText = "Resolved";
        SupportTicketResponse resp = SupportTicketResponse.builder().id(ticketId).build();
        when(supportTicketService.updateTicketResponse(ticketId, responseText)).thenReturn(resp);
        assertSame(resp, controller.updateTicketResponse(ticketId, responseText));
    }

    @Test
    void getTicketsByStatus_returnsList() {
        String status = "OPEN";
        SupportTicketResponse resp = SupportTicketResponse.builder().id(6L).build();
        List<SupportTicketResponse> list = List.of(resp);
        when(supportTicketService.getTicketsByStatus(status)).thenReturn(list);
        assertSame(list, controller.getTicketsByStatus(status));
    }

    @Test
    void updateTicketStatus_returnsResponse() {
        Long ticketId = 7L;
        SupportTicket.TicketStatus status = SupportTicket.TicketStatus.RESOLVED;
        SupportTicketResponse resp = SupportTicketResponse.builder().id(ticketId).build();
        when(supportTicketService.updateTicketStatus(ticketId, status)).thenReturn(resp);
        assertSame(resp, controller.updateTicketStatus(ticketId, status));
    }
}