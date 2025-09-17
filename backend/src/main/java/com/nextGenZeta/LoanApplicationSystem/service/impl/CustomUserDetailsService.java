package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.model.entities.BaseUser;
import com.nextGenZeta.LoanApplicationSystem.model.entities.UserPrincipal;
import com.nextGenZeta.LoanApplicationSystem.repository.BaseUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final BaseUserRepository userRepository;

    public CustomUserDetailsService(BaseUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("loadUserByUsername called with username={}", username);
        BaseUser user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            logger.error("User not found with username: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new UserPrincipal(user);
    }
}
