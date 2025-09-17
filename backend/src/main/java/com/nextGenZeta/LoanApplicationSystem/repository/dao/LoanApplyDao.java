package com.nextGenZeta.LoanApplicationSystem.repository.dao;

import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import com.nextGenZeta.LoanApplicationSystem.repository.LoanApplyRepository;
import com.nextGenZeta.LoanApplicationSystem.config.CustomThreadPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Repository
public interface LoanApplyDao extends LoanApplyRepository {

    default Optional<LoanApplication> getByLoanId(Long loanId) {
        return findById(loanId);
    }

    default List<LoanApplication> getLoansByUser(Long userId) {
        return findByUserId(userId);
    }

    default Boolean checkDuplicateWithin24Hours(Long userId, LocalDateTime fromTime) {
        return existsByUserIdAndApplicationDateTimeAfter(userId, fromTime);
    }

    default Page<LoanApplication> getAllLoans(int page, int size) {
        return findAll(PageRequest.of(page, size));
    }

    default Page<LoanApplication> getLoansByStatus(LoanStatus status, int page, int size) {
        return findByStatus(status, PageRequest.of(page, size));
    }

    default Page<LoanApplication> getLoansByUserAndStatus(Long userId, LoanStatus status, int page, int size) {
        return findByUserIdAndStatus(userId, status, PageRequest.of(page, size));
    }


}
