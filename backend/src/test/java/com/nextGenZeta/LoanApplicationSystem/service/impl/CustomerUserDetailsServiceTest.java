package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.model.entities.BaseUser;
import com.nextGenZeta.LoanApplicationSystem.model.entities.UserPrincipal;
import com.nextGenZeta.LoanApplicationSystem.repository.BaseUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerUserDetailsServiceTest {

    @Mock
    private BaseUserRepository userRepository;

    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_userFound_returnsUserPrincipal() {
        String username = "testuser";
        BaseUser user = BaseUser.builder().username(username).build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Object result = service.loadUserByUsername(username);

        assertTrue(result instanceof UserPrincipal);
        assertEquals(username, ((UserPrincipal) result).getUsername());
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        String username = "nouser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(username));
    }
}