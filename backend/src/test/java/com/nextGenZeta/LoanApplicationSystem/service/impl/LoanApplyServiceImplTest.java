package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanApplyRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode;
import com.nextGenZeta.LoanApplicationSystem.exception.LoanApplyException;
import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.repository.CustomerProfileRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.dao.LoanApplyDao;
import com.nextGenZeta.LoanApplicationSystem.service.EmiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanApplyServiceImplTest {

    @Mock
    private LoanApplyDao loanDao;
    @Mock
    private CustomerProfileRepository customerProfileRepository;
    @Mock
    private EmiService emiService;
    @Mock
    private Executor securityExecutor;

    private LoanApplyServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        service = new LoanApplyServiceImpl(loanDao, customerProfileRepository, emiService);
        Field field = LoanApplyServiceImpl.class.getDeclaredField("securityExecutor");
        field.setAccessible(true);
        field.set(service, securityExecutor);
        // Set autoRejectionEnabled to true for tests
        Field autoRejectionField = LoanApplyServiceImpl.class.getDeclaredField("autoRejectionEnabled");
        autoRejectionField.setAccessible(true);
        autoRejectionField.set(service, true);
    }

    @Test
    void applyLoan_successful() {
        LoanApplyRequest req = LoanApplyRequest.builder()
                .userId(1L)
                .amount(10000.0)
                .tenureMonths(12)
                .income(50000.0)
                .creditScore(700)
                .purpose("Test")
                .build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));
        LoanApplication saved = LoanApplication.builder()
                .id(2L)
                .userId(1L)
                .amount(10000.0)
                .tenureMonths(12)
                .income(50000.0)
                .creditScore(700)
                .purpose("Test")
                .status(LoanStatus.NEW)
                .applicationDateTime(LocalDateTime.now())
                .build();
        when(loanDao.save(any())).thenReturn(saved);

        LoanApplyResponse resp = service.applyLoan(req);
        assertEquals(2L, resp.getId());
        assertEquals(LoanStatus.NEW, resp.getStatus());
        assertEquals("Loan application submitted successfully", resp.getMessage());
    }

    @Test
    void applyLoan_duplicateWithin24Hours_throwsException() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(10000.0).tenureMonths(12).income(50000.0).creditScore(700).purpose("Test").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(true);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));

        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.applyLoan(req));
        assertEquals(ErrorCode.DUPLICATE_LOAN_REQUEST, ex.getErrorCode());
    }

    @Test
    void applyLoan_customerProfileNotFound_throwsException() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(10000.0).tenureMonths(12).income(50000.0).creditScore(700).purpose("Test").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.empty());

        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.applyLoan(req));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void applyLoan_autoReject_invalidIncome() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(10000.0).tenureMonths(12).income(20000.0).creditScore(700).purpose("Test").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));
        LoanApplication saved = LoanApplication.builder().id(2L).status(LoanStatus.REJECTED).build();
        when(loanDao.save(any())).thenReturn(saved);

        LoanApplyResponse resp = service.applyLoan(req);
        assertEquals(LoanStatus.REJECTED, resp.getStatus());
        assertEquals("Income below minimum requirement", resp.getMessage());
    }

    @Test
    void applyLoan_autoReject_invalidCreditScore() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(10000.0).tenureMonths(12).income(50000.0).creditScore(200).purpose("Test").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));
        LoanApplication saved = LoanApplication.builder().id(2L).status(LoanStatus.REJECTED).build();
        when(loanDao.save(any())).thenReturn(saved);

        LoanApplyResponse resp = service.applyLoan(req);
        assertEquals(LoanStatus.REJECTED, resp.getStatus());
        assertEquals("Invalid credit score", resp.getMessage());
    }

    @Test
    void applyLoan_autoReject_invalidAmount() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(0.0).tenureMonths(12).income(50000.0).creditScore(700).purpose("Test").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));
        LoanApplication saved = LoanApplication.builder().id(2L).status(LoanStatus.REJECTED).build();
        when(loanDao.save(any())).thenReturn(saved);

        LoanApplyResponse resp = service.applyLoan(req);
        assertEquals(LoanStatus.REJECTED, resp.getStatus());
        assertEquals("Invalid loan amount", resp.getMessage());
    }

    @Test
    void applyLoan_autoReject_amountTooHigh() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(2000000.0).tenureMonths(12).income(50000.0).creditScore(700).purpose("Test").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));
        LoanApplication saved = LoanApplication.builder().id(2L).status(LoanStatus.REJECTED).build();
        when(loanDao.save(any())).thenReturn(saved);

        LoanApplyResponse resp = service.applyLoan(req);
        assertEquals(LoanStatus.REJECTED, resp.getStatus());
        assertEquals("Loan amount too high compared to income", resp.getMessage());
    }

    @Test
    void applyLoan_autoReject_tenureOutOfRange() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(10000.0).tenureMonths(3).income(50000.0).creditScore(700).purpose("Test").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));
        LoanApplication saved = LoanApplication.builder().id(2L).status(LoanStatus.REJECTED).build();
        when(loanDao.save(any())).thenReturn(saved);

        LoanApplyResponse resp = service.applyLoan(req);
        assertEquals(LoanStatus.REJECTED, resp.getStatus());
        assertEquals("Loan tenure out of allowed range", resp.getMessage());
    }

    @Test
    void applyLoan_autoReject_largeAmountNoPurpose() {
        LoanApplyRequest req = LoanApplyRequest.builder().userId(1L).amount(2000000.0).tenureMonths(12).income(100000.0).creditScore(700).purpose("").build();
        when(loanDao.checkDuplicateWithin24Hours(eq(1L), any())).thenReturn(false);
        when(customerProfileRepository.findByBaseUserId(1L)).thenReturn(Optional.of(CustomerProfile.builder().build()));
        LoanApplication saved = LoanApplication.builder().id(2L).status(LoanStatus.REJECTED).build();
        when(loanDao.save(any())).thenReturn(saved);

        LoanApplyResponse resp = service.applyLoan(req);
        assertEquals(LoanStatus.REJECTED, resp.getStatus());
        assertEquals("Purpose required for large loan amount", resp.getMessage());
    }

    @Test
    void getLoansByUser_returnsList() {
        LoanApplication loan = LoanApplication.builder().id(1L).build();
        when(loanDao.getLoansByUser(1L)).thenReturn(List.of(loan));
        List<LoanApplication> result = service.getLoansByUser(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getLoanById_found() {
        LoanApplication loan = LoanApplication.builder().id(2L).build();
        when(loanDao.getByLoanId(2L)).thenReturn(Optional.of(loan));
        LoanApplication result = service.getLoanById(2L);
        assertEquals(2L, result.getId());
    }

    @Test
    void getLoanById_notFound_throwsException() {
        when(loanDao.getByLoanId(3L)).thenReturn(Optional.empty());
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.getLoanById(3L));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAllLoans_unpaged() {
        LoanApplication loan = LoanApplication.builder().id(1L).build();
        Page<LoanApplication> page = new PageImpl<>(List.of(loan));
        when(loanDao.findAll(any(Pageable.class))).thenReturn(page);

        LoanApplyPaginatedResponse resp = service.getAllLoans(null, null);
        assertEquals(1, resp.getLoanApplications().size());
    }

    @Test
    void updateLoanStatus_foundAndApproved_triggersEmiSchedule() {
        LoanApplication loan = LoanApplication.builder().id(1L).status(LoanStatus.NEW).build();
        when(loanDao.getByLoanId(1L)).thenReturn(Optional.of(loan));
        LoanApplication updated = LoanApplication.builder().id(1L).status(LoanStatus.APPROVED).build();
        when(loanDao.save(any())).thenReturn(updated);

        LoanApplyResponse resp = service.updateLoanStatus(1L, "APPROVED", 2L, "Approved");
        assertEquals(LoanStatus.APPROVED, resp.getStatus());
        verify(emiService, times(1)).createRepaymentScheduleForLoan(1L);
    }

    @Test
    void updateLoanStatus_foundAndRejected_doesNotTriggerEmiSchedule() {
        LoanApplication loan = LoanApplication.builder().id(1L).status(LoanStatus.NEW).build();
        when(loanDao.getByLoanId(1L)).thenReturn(Optional.of(loan));
        LoanApplication updated = LoanApplication.builder().id(1L).status(LoanStatus.REJECTED).build();
        when(loanDao.save(any())).thenReturn(updated);

        LoanApplyResponse resp = service.updateLoanStatus(1L, "REJECTED", 2L, "Rejected");
        assertEquals(LoanStatus.REJECTED, resp.getStatus());
        verify(emiService, never()).createRepaymentScheduleForLoan(anyLong());
    }

    @Test
    void updateLoanStatus_notFound_throwsException() {
        when(loanDao.getByLoanId(2L)).thenReturn(Optional.empty());
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.updateLoanStatus(2L, "APPROVED", 3L, "Approved"));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateLoanStatus_invalidStatus_throwsException() {
        LoanApplication loan = LoanApplication.builder().id(1L).status(LoanStatus.NEW).build();
        when(loanDao.getByLoanId(1L)).thenReturn(Optional.of(loan));
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.updateLoanStatus(1L, "INVALID", 2L, "Invalid"));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }

    @Test
    void getLoansByStatus_validStatus() {
        LoanApplication loan = LoanApplication.builder().id(1L).status(LoanStatus.APPROVED).build();
        Page<LoanApplication> page = new PageImpl<>(List.of(loan));
        when(loanDao.getLoansByUserAndStatus(1L, LoanStatus.APPROVED, 0, 10)).thenReturn(page);

        Page<LoanApplication> result = service.getLoansByStatus(1L, "APPROVED", 0, 10);
        assertEquals(1, result.getContent().size());
        assertEquals(LoanStatus.APPROVED, result.getContent().get(0).getStatus());
    }

    @Test
    void getLoansByStatus_invalidStatus_throwsException() {
        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.getLoansByStatus(1L, "INVALID", 0, 10));
        assertEquals(ErrorCode.VALIDATION_ERROR, ex.getErrorCode());
    }
}