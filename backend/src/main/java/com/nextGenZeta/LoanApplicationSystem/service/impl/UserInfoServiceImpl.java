package com.nextGenZeta.LoanApplicationSystem.service.impl;

import com.nextGenZeta.LoanApplicationSystem.controllers.response.UserResponseDTO;
import com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode;
import com.nextGenZeta.LoanApplicationSystem.exception.LoanApplyException;
import com.nextGenZeta.LoanApplicationSystem.model.entities.BaseUser;
import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import com.nextGenZeta.LoanApplicationSystem.repository.BaseUserRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.CustomerProfileRepository;
import com.nextGenZeta.LoanApplicationSystem.service.UserInfoService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private final CustomerProfileRepository customerProfileRepository;
    private final BaseUserRepository baseUserRepository;

    public UserInfoServiceImpl(CustomerProfileRepository customerProfileRepository, BaseUserRepository baseUserRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.baseUserRepository = baseUserRepository;
    }


    @Override
    public UserResponseDTO getUserInfo(Long userId) {
        logger.info("getUserInfo called with userId={}", userId);
        BaseUser user = baseUserRepository.findById(userId).orElse(null);
        CustomerProfile customerProfile = customerProfileRepository.findByBaseUserId(userId).orElse(null);

        if (user == null || customerProfile == null) {
            logger.error("User or Customer Profile not found for userId={}", userId);
            throw new LoanApplyException(ErrorCode.INTERNAL_SERVER_ERROR, "User or Customer Profile not found for user ID: " + userId);
        }

        return convertToUserResponseDTO(user, customerProfile);

    }

    private UserResponseDTO convertToUserResponseDTO(BaseUser user, CustomerProfile customerProfile) {
        return UserResponseDTO.builder()
                .name(customerProfile.getFullName())
                .email(user.getUsername())
                .address(customerProfile.getAddress())
                .phoneNumber(customerProfile.getPhoneNumber())
                .dateOfBirth(customerProfile.getDob())
                .gender(customerProfile.getGender())
                .build();
    }
}
