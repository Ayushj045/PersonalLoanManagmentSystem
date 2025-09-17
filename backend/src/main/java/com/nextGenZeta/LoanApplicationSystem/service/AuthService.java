package com.nextGenZeta.LoanApplicationSystem.service;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoginRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.PasswordUpdateDTO;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.RegisterRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.AuthResponse;
import com.nextGenZeta.LoanApplicationSystem.exception.ErrorCode;
import com.nextGenZeta.LoanApplicationSystem.exception.LoanApplyException;
import com.nextGenZeta.LoanApplicationSystem.model.entities.BaseUser;
import com.nextGenZeta.LoanApplicationSystem.model.entities.CustomerProfile;
import com.nextGenZeta.LoanApplicationSystem.model.entities.UserPrincipal;
import com.nextGenZeta.LoanApplicationSystem.model.enums.UserRole;
import com.nextGenZeta.LoanApplicationSystem.repository.BaseUserRepository;
import com.nextGenZeta.LoanApplicationSystem.repository.CustomerProfileRepository;
import com.nextGenZeta.LoanApplicationSystem.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final BaseUserRepository baseUserRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;

    public void registerCustomer(RegisterRequest request) {
        BaseUser user = BaseUser.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        CustomerProfile profile = CustomerProfile.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dob(request.getDob())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .baseUser(user)
                .build();

        user.setCustomerProfile(profile);

        baseUserRepository.save(user);
    }

    public AuthResponse loginCustomer(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        BaseUser user = baseUserRepository.findByUsername(request.getEmail()).orElse(null);

        if(authentication.isAuthenticated() && user!=null ){
            String token = jwtService.generateToken(new UserPrincipal(user));
            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .role(user.getRole().name())
                    .build();
        }

        else if (!authentication.isAuthenticated()) {
            throw new LoanApplyException(ErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
        } else {
            throw new LoanApplyException(ErrorCode.INTERNAL_SERVER_ERROR, "Internal Server error, please login later");
        }
    }

    public String updatePassword(Long userId, PasswordUpdateDTO passwordUpdateDTO) {
        BaseUser user = baseUserRepository.findById(userId).orElse(null);
        if(user == null){
            throw new LoanApplyException(ErrorCode.USER_NOT_FOUND, "User not found with ID: " + userId);
        }
        boolean passwordVerified = passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword());
        if(!passwordVerified) {
            throw new LoanApplyException(ErrorCode.INVALID_CREDENTIALS, "Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        baseUserRepository.save(user);
        return "Password updated successfully";
    }
}
