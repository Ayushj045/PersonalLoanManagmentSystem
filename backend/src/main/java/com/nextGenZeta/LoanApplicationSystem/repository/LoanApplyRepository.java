package com.nextGenZeta.LoanApplicationSystem.repository;

import com.nextGenZeta.LoanApplicationSystem.model.entities.LoanApplication;
import com.nextGenZeta.LoanApplicationSystem.model.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanApplyRepository extends JpaRepository<LoanApplication, Long> {

    boolean existsByUserIdAndApplicationDateTimeAfter(Long userId, LocalDateTime dateTime);

    List<LoanApplication> findByUserId(Long userId);
    Page<LoanApplication> findByStatus(LoanStatus status, Pageable pageable);
    Page<LoanApplication> findByUserIdAndStatus(Long userId, LoanStatus status, Pageable pageable);
}
