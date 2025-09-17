// src/main/java/com/nextGenZeta/LoanApplicationSystem/repository/RepaymentScheduleRepository.java
package com.nextGenZeta.LoanApplicationSystem.repository;

import com.nextGenZeta.LoanApplicationSystem.model.entities.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    List<RepaymentSchedule> findByLoanIdOrderByMonthNumber(Long loanId);

    @Query("SELECT r FROM RepaymentSchedule r WHERE r.loanId = :loanId AND (r.paymentStatus = com.nextGenZeta.LoanApplicationSystem.model.enums.PaymentStatus.PENDING OR r.paymentStatus = com.nextGenZeta.LoanApplicationSystem.model.enums.PaymentStatus.PARTIALLY_PAID) ORDER BY r.monthNumber ASC")
    List<RepaymentSchedule> findNextToPayByLoanId(Long loanId);

    @Query("SELECT COALESCE(SUM(t.amountPaid), 0) FROM RepaymentTransaction t WHERE t.repaymentScheduleId = :repaymentScheduleId")
    Double getTotalPaidForSchedule(Long repaymentScheduleId);
}