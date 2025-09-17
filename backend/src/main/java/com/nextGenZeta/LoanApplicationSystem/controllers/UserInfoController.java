package com.nextGenZeta.LoanApplicationSystem.controllers;


import com.nextGenZeta.LoanApplicationSystem.controllers.response.UserResponseDTO;
import com.nextGenZeta.LoanApplicationSystem.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/user/info")
public class UserInfoController {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/{userId}")
    public UserResponseDTO getUserInfo(@PathVariable Long userId) {
        logger.info("getUserInfo called with userId={}", userId);
        return userInfoService.getUserInfo(userId);
    }


}