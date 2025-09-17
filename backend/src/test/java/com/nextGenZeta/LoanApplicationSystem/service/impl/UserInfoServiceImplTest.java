package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.response.UserResponseDTO;
import com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode;
import com.nextGenZeta.LoanApplicationSystem.exception.LoanApplyException;
import com.nextGenZeta.LoanApplicationSystem.model.entities.BaseUser;
import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import com.nextGenZeta.LoanApplicationSystem.model.enums.Gender;
import com.nextGenZeta.LoanApplicationSystem.repository.BaseUserRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.CustomerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserInfoServiceImplTest {

    @Mock
    private CustomerProfileRepository customerProfileRepository;
    @Mock
    private BaseUserRepository baseUserRepository;

    private UserInfoServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new UserInfoServiceImpl(customerProfileRepository, baseUserRepository);
    }


    @Test
    void getUserInfo_userNotFound_throwsException() {
        Long userId = 2L;
        when(baseUserRepository.findById(userId)).thenReturn(Optional.empty());
        when(customerProfileRepository.findByBaseUserId(userId)).thenReturn(Optional.of(CustomerProfile.builder().build()));

        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.getUserInfo(userId));
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("User or Customer Profile not found"));
    }

    @Test
    void getUserInfo_profileNotFound_throwsException() {
        Long userId = 3L;
        BaseUser user = BaseUser.builder().id(userId).username("user3@example.com").build();
        when(baseUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(customerProfileRepository.findByBaseUserId(userId)).thenReturn(Optional.empty());

        LoanApplyException ex = assertThrows(LoanApplyException.class, () -> service.getUserInfo(userId));
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("User or Customer Profile not found"));
    }
}