package com.nextGenZeta.LoanApplicationSystem.repository;

import com.nextGenZeta.LoanApplicationSystem.model.entities.RepaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepaymentTransactionRepository extends JpaRepository<RepaymentTransaction, Long> {
    @Query("SELECT COALESCE(SUM(t.amountPaid), 0) FROM RepaymentTransaction t WHERE t.repaymentScheduleId = :repaymentScheduleId")
    Double getTotalPaidForSchedule(Long repaymentScheduleId);
}