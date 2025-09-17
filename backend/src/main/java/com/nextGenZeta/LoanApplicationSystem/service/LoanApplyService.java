package com.nextGenZeta.LoanApplicationSystem.service;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanApplyRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface LoanApplyService {

    LoanApplyResponse applyLoan(LoanApplyRequest request);

    List<LoanApplication> getLoansByUser(Long userId);

    LoanApplication getLoanById(Long loanId);

    LoanApplyPaginatedResponse getAllLoans(Integer page, Integer size);

    LoanApplyResponse updateLoanStatus(Long loanId, String status,
                                       Long reviewedBy, String reviewRemarks);

    Page<LoanApplication> getLoansByStatus(Long userid, String status, int page, int size);


}
