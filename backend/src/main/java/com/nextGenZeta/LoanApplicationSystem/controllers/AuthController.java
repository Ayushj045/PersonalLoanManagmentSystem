package com.nextGenZeta.LoanApplicationSystem.controllers;

import com.nextGenZeta.LoanApplicationSystem.controllers.request.LoginRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.PasswordUpdateDTO;
import com.nextGenZeta.LoanApplicationSystem.controllers.request.RegisterRequest;
import com.nextGenZeta.LoanApplicationSystem.controllers.response.AuthResponse;
import com.nextGenZeta.LoanApplicationSystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("register called with request={}", request);
        authService.registerCustomer(request);
        return ResponseEntity.ok("Customer registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("login called with request={}", request);
        return ResponseEntity.ok(authService.loginCustomer(request));
    }

    @PutMapping("/update-password/{userId}")
    public ResponseEntity<String> updatePassword(@PathVariable Long userId, @Valid @RequestBody PasswordUpdateDTO request) {
        return ResponseEntity.ok(authService.updatePassword(userId, request));
    }
}
