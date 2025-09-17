package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.controllers.response.UserResponseDTO;
import com.nextGenZeta.LoanApplicationSystem.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserInfoControllerTest {

    @Mock
    private UserInfoService userInfoService;

    private UserInfoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new UserInfoController(userInfoService);
    }

    @Test
    void getUserInfo_returnsDto() {
        Long userId = 1L;
        UserResponseDTO dto = UserResponseDTO.builder().build(); // Use builder, not constructor
        when(userInfoService.getUserInfo(userId)).thenReturn(dto);
        assertSame(dto, controller.getUserInfo(userId));
    }
}