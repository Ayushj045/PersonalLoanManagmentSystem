package com.nextGenZeta.LoanApplicationSystem.repository;

import com.nextGenZeta.LoanApplicationSystem.model.entities.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByUserId(Long userId);

    List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<SupportTicket> findByStatus(SupportTicket.TicketStatus status);

    List<SupportTicket> findByLoanId(Long loanId);
}
