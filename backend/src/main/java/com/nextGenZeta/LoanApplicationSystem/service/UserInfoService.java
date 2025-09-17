package com.nextGenZeta.LoanApplicationSystem.service;

import com.nextGenZeta.LoanApplicationSystem.controllers.response.UserResponseDTO;

public interface UserInfoService {

    UserResponseDTO getUserInfo(Long userId);

}
