package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanApplyRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanApplyControllerTest {

    @Mock
    private LoanApplyService loanService;

    private LoanApplyController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new LoanApplyController(loanService);
    }

    @Test
    void applyForLoan_returnsResponse() {
        LoanApplyRequest req = new LoanApplyRequest();
        LoanApplyResponse resp = LoanApplyResponse.builder().id(1L).build();
        when(loanService.applyLoan(req)).thenReturn(resp);
        assertSame(resp, controller.applyForLoan(req));
    }

    @Test
    void getLoansByUser_returnsList() {
        Long userId = 2L;
        LoanApplication loan = LoanApplication.builder().id(3L).build();
        List<LoanApplication> loans = List.of(loan);
        when(loanService.getLoansByUser(userId)).thenReturn(loans);
        assertSame(loans, controller.getLoansByUser(userId));
    }

    @Test
    void getLoanById_returnsLoan() {
        Long loanId = 4L;
        LoanApplication loan = LoanApplication.builder().id(loanId).build();
        when(loanService.getLoanById(loanId)).thenReturn(loan);
        assertSame(loan, controller.getLoanById(loanId));
    }

    @Test
    void getAllLoans_returnsPaginatedResponse() {
        LoanApplyPaginatedResponse resp = LoanApplyPaginatedResponse.builder().page(0).size(10).build();
        when(loanService.getAllLoans(0, 10)).thenReturn(resp);
        assertSame(resp, controller.getAllLoans(0, 10));
    }

    @Test
    void getLoansByStatus_returnsPage() {
        Long userId = 5L;
        String status = "APPROVED";
        int page = 1;
        int size = 5;
        Page<LoanApplication> pageObj = mock(Page.class);
        when(loanService.getLoansByStatus(userId, status, page, size)).thenReturn(pageObj);
        assertSame(pageObj, controller.getLoansByStatus(userId, status, page, size));
    }
}