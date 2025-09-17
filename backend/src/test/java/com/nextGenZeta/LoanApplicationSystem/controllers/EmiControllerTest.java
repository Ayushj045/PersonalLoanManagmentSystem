package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.commons.EmiConstants;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.EmiCalculationRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.PaymentUpdateRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.RepaymentScheduleDto;
import com.nextGenZeta.LoanApplicationSystem.model.entities.EmiCalculationResult;
import com.nextGenZeta.LoanApplicationSystem.service.EmiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmiControllerTest {

    @Mock
    private EmiService emiService;

    private EmiController emiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emiController = new EmiController(emiService);
    }

    @Test
    void previewEmi_returnsResult() {
        EmiCalculationRequest request = new EmiCalculationRequest();
        EmiCalculationResult result = EmiCalculationResult.builder().monthlyEmi(123.45).build();
        when(emiService.calculateEmi(request)).thenReturn(result);

        ResponseEntity<EmiCalculationResult> response = emiController.previewEmi(request);
        assertEquals(result, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getRepaymentSchedule_returnsList() {
        Long loanId = 1L;
        RepaymentScheduleDto dto = new RepaymentScheduleDto();
        List<RepaymentScheduleDto> list = List.of(dto);
        when(emiService.getRepaymentSchedule(loanId)).thenReturn(list);

        ResponseEntity<List<RepaymentScheduleDto>> response = emiController.getRepaymentSchedule(loanId);
        assertEquals(list, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updatePayment_returnsDto() {
        Long loanId = 2L;
        PaymentUpdateRequest req = new PaymentUpdateRequest();
        RepaymentScheduleDto dto = new RepaymentScheduleDto();
        when(emiService.updatePaymentForLoan(loanId, req)).thenReturn(dto);

        ResponseEntity<RepaymentScheduleDto> response = emiController.updatePayment(loanId, req);
        assertEquals(dto, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }
}