package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanDecisionDTO;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.repository.CustomerProfileRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.dao.LoanApplyDao;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLoanServiceImplTest {

    @Mock
    private LoanApplyDao loanApplyDao;
    @Mock
    private LoanApplyService loanApplyService;
    @Mock
    private CustomerProfileRepository customerProfileRepository;

    private AdminLoanServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AdminLoanServiceImpl(loanApplyDao, loanApplyService, customerProfileRepository);
    }

    @Test
    void getLoansByStatus_returnsPaginatedResponse() {
        LoanApplication loan = LoanApplication.builder()
                .id(1L)
                .userId(2L)
                .amount(10000.0)
                .tenureMonths(12)
                .income(50000.0)
                .creditScore(700)
                .purpose("Test")
                .applicationDateTime(LocalDateTime.now())
                .status(LoanStatus.NEW)
                .build();
        Page<LoanApplication> pageMock = mock(Page.class);
        when(pageMock.getContent()).thenReturn(List.of(loan));
        when(pageMock.getTotalPages()).thenReturn(1);
        when(pageMock.getSize()).thenReturn(1);
        when(pageMock.getNumber()).thenReturn(0);
        when(loanApplyDao.getLoansByStatus(LoanStatus.NEW, 0, 1)).thenReturn(pageMock);

        CustomerProfile profile = CustomerProfile.builder().fullName("User Name").build();
        when(customerProfileRepository.findByBaseUserId(loan.getUserId())).thenReturn(java.util.Optional.of(profile));

        LoanApplyAdminPaginatedResponse resp = service.getLoansByStatus("NEW", 0, 1);
        assertEquals(1, resp.getLoanApplications().size());
        assertEquals("User Name", resp.getLoanApplications().get(0).getUserName());
    }

    @Test
    void getLoansByStatus_handlesUnknownUser() {
        LoanApplication loan = LoanApplication.builder()
                .id(2L)
                .userId(3L)
                .amount(20000.0)
                .tenureMonths(24)
                .income(60000.0)
                .creditScore(650)
                .purpose("Test2")
                .applicationDateTime(LocalDateTime.now())
                .status(LoanStatus.NEW)
                .build();
        Page<LoanApplication> pageMock = mock(Page.class);
        when(pageMock.getContent()).thenReturn(List.of(loan));
        when(pageMock.getTotalPages()).thenReturn(1);
        when(pageMock.getSize()).thenReturn(1);
        when(pageMock.getNumber()).thenReturn(0);
        when(loanApplyDao.getLoansByStatus(LoanStatus.NEW, 0, 1)).thenReturn(pageMock);

        when(customerProfileRepository.findByBaseUserId(loan.getUserId())).thenReturn(java.util.Optional.empty());

        LoanApplyAdminPaginatedResponse resp = service.getLoansByStatus("NEW", 0, 1);
        assertEquals("Unknown User", resp.getLoanApplications().get(0).getUserName());
    }

    @Test
    void getLoanByLoanId_returnsAdminResponse() {
        LoanApplication loan = LoanApplication.builder()
                .id(5L)
                .userId(6L)
                .amount(30000.0)
                .tenureMonths(36)
                .income(70000.0)
                .creditScore(800)
                .purpose("Test3")
                .applicationDateTime(LocalDateTime.now())
                .status(LoanStatus.APPROVED)
                .build();
        when(loanApplyService.getLoanById(5L)).thenReturn(loan);

        CustomerProfile profile = CustomerProfile.builder().fullName("Admin User").build();
        when(customerProfileRepository.findByBaseUserId(loan.getUserId())).thenReturn(java.util.Optional.of(profile));

        LoanApplyAdminResponse resp = service.getLoanByLoanId(5L);
        assertEquals("Admin User", resp.getUserName());
        assertEquals(5L, resp.getId());
    }

    @Test
    void getLoanByLoanId_handlesUnknownUser() {
        LoanApplication loan = LoanApplication.builder()
                .id(7L)
                .userId(8L)
                .amount(40000.0)
                .tenureMonths(48)
                .income(80000.0)
                .creditScore(750)
                .purpose("Test4")
                .applicationDateTime(LocalDateTime.now())
                .status(LoanStatus.REJECTED)
                .build();
        when(loanApplyService.getLoanById(7L)).thenReturn(loan);

        when(customerProfileRepository.findByBaseUserId(loan.getUserId())).thenReturn(java.util.Optional.empty());

        LoanApplyAdminResponse resp = service.getLoanByLoanId(7L);
        assertEquals("Unknown User", resp.getUserName());
        assertEquals(7L, resp.getId());
    }

    @Test
    void getAllLoans_returnsPaginatedResponse() {
        LoanApplication loan = LoanApplication.builder()
                .id(9L)
                .userId(10L)
                .amount(50000.0)
                .tenureMonths(60)
                .income(90000.0)
                .creditScore(780)
                .purpose("Test5")
                .applicationDateTime(LocalDateTime.now())
                .status(LoanStatus.NEW)
                .build();
        Page<LoanApplication> pageMock = mock(Page.class);
        when(pageMock.getContent()).thenReturn(List.of(loan));
        when(pageMock.getTotalPages()).thenReturn(2);
        when(pageMock.getSize()).thenReturn(1);
        when(pageMock.getNumber()).thenReturn(1);
        when(loanApplyDao.getAllLoans(1, 1)).thenReturn(pageMock);

        CustomerProfile profile = CustomerProfile.builder().fullName("All User").build();
        when(customerProfileRepository.findByBaseUserId(loan.getUserId())).thenReturn(java.util.Optional.of(profile));

        LoanApplyAdminPaginatedResponse resp = service.getAllLoans(1, 1);
        assertEquals(1, resp.getLoanApplications().size());
        assertEquals("All User", resp.getLoanApplications().get(0).getUserName());
        assertEquals(2, resp.getTotalPages());
        assertEquals(1, resp.getSize());
        assertEquals(1, resp.getPage());
    }

}