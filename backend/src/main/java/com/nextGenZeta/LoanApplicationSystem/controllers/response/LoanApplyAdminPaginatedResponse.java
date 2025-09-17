package com.nextGenZeta.LoanApplicationSystem.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplyAdminPaginatedResponse {
    private List<LoanApplyAdminResponse> loanApplications;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer page;
}
