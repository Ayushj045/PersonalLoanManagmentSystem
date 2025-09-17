package com.nextGenZeta.LoanApplicationSystem.repository;

import com.nextGenZeta.LoanApplicationSystem.model.entities.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser, Long> {

   Optional<BaseUser> findByUsername(String username);

}
