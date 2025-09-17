package com.nextGenZeta.LoanApplicationSystem.controllers.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "email cannot be blank")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
