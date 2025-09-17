package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.commons.EmiConstants;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.EmiCalculationRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.PaymentUpdateRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.RepaymentScheduleDto;
import com.nextGenZeta.LoanApplicationSystem.model.entities.EmiCalculationResult;
import com.nextGenZeta.LoanApplicationSystem.service.EmiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(EmiConstants.EMI_BASE)
public class EmiController {

    private static final Logger logger = LoggerFactory.getLogger(EmiController.class);

    private final EmiService emiService;

    public EmiController(EmiService emiService) {
        this.emiService = emiService;
    }

    @PostMapping(EmiConstants.EMI_PREVIEW)
    public ResponseEntity<EmiCalculationResult> previewEmi(@Valid @RequestBody EmiCalculationRequest request) {
        logger.info("previewEmi called with request={}", request);
        return ResponseEntity.ok(emiService.calculateEmi(request));
    }

    @GetMapping(EmiConstants.EMI_GET_SCHEDULE)
    public ResponseEntity<List<RepaymentScheduleDto>> getRepaymentSchedule(@PathVariable Long loanId) {
        logger.info("getRepaymentSchedule called with loanId={}", loanId);
        return ResponseEntity.ok(emiService.getRepaymentSchedule(loanId));
    }

    @PutMapping(EmiConstants.EMI_UPDATE_PAYMENT)
    public ResponseEntity<RepaymentScheduleDto> updatePayment(
            @PathVariable Long loanId,
            @Valid @RequestBody PaymentUpdateRequest paymentUpdateRequest) {
        logger.info("updatePayment called with loanId={}, paymentUpdateRequest={}", loanId, paymentUpdateRequest);
        return ResponseEntity.ok(emiService.updatePaymentForLoan(loanId, paymentUpdateRequest));
    }


}