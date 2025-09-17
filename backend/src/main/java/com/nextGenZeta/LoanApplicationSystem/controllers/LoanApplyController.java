package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoanApplyRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyPaginatedResponse;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.LoanApplyResponse;
import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.service.LoanApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/v1/loans")
@RequiredArgsConstructor
public class LoanApplyController {

    private static final Logger logger = LoggerFactory.getLogger(LoanApplyController.class);

    private final LoanApplyService loanService;

    @PostMapping("/apply")
    public LoanApplyResponse applyForLoan(@RequestBody LoanApplyRequest loanApplyRequest) {
        logger.info("applyForLoan called with loanApplyRequest={}", loanApplyRequest);
        return loanService.applyLoan(loanApplyRequest);
    }

    @GetMapping("/user/{userId}")
    public List<LoanApplication> getLoansByUser(@PathVariable Long userId) {
        logger.info("getLoansByUser called with userId={}", userId);
        return loanService.getLoansByUser(userId);
    }

    @GetMapping("/{loanId}")
    public LoanApplication getLoanById(@PathVariable Long loanId) {
        logger.info("getLoanById called with loanId={}", loanId);
        return loanService.getLoanById(loanId);
    }

    @GetMapping("/all")
    public LoanApplyPaginatedResponse getAllLoans(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        logger.info("getAllLoans called with page={}, size={}", page, size);
        return loanService.getAllLoans(page, size);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public Page<LoanApplication> getLoansByStatus(@PathVariable Long userId, @PathVariable String status, @RequestParam(required = false, defaultValue = "0") Integer page,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        logger.info("getLoansByStatus called with userId={}, status={}, page={}, size={}", userId, status, page, size);
        return loanService.getLoansByStatus(userId, status, page, size);
    }
}
