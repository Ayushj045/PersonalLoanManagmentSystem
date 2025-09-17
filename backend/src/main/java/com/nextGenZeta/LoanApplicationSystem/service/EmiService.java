package com.nextGenZeta.LoanApplicationSystem.service;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.EmiCalculationRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.PaymentUpdateRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.RepaymentScheduleDto;
import com.nextGenZeta.LoanApplicationSystem.model.entities.EmiCalculationResult;

import java.util.List;

public interface EmiService {
    EmiCalculationResult calculateEmi(EmiCalculationRequest request);

    EmiCalculationResult calculateEmiForLoan(Long loanId);

    void createRepaymentScheduleForLoan(Long loanId);

    List<RepaymentScheduleDto> getRepaymentSchedule(Long loanId);

    RepaymentScheduleDto updatePaymentForLoan(Long loanId, PaymentUpdateRequest paymentUpdateRequest);


}