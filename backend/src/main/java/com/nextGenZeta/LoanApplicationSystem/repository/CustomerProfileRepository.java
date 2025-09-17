package com.nextGenZeta.LoanApplicationSystem.repository;

import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    @Query("SELECT e FROM CustomerProfile e WHERE e.baseUser.id = :userId")
    Optional<CustomerProfile> findByBaseUserId(@Param("userId") Long userId);
}
