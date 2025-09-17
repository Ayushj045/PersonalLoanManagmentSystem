package com.nextGenZeta.LoanApplicationSystem.service;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanDecisionDTO;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyAdminResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;


public interface AdminLoanService {
    LoanApplyResponse reviewLoan(Long loanId, LoanDecisionDTO decision);
    LoanApplyAdminPaginatedResponse getLoansByStatus(String status, Integer page, Integer size);
    LoanApplyAdminResponse getLoanByLoanId(Long loanId);

    LoanApplyAdminPaginatedResponse getAllLoans(Integer page, Integer size);
}

