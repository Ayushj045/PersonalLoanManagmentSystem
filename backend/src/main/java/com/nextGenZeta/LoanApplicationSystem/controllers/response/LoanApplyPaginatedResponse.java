package com.nextGenZeta.LoanApplicationSystem.controllers.response;

import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplyPaginatedResponse {

    private List<LoanApplication> loanApplications;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer page;

}
