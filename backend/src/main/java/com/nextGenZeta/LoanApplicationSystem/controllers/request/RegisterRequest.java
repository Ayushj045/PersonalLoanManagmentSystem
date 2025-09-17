package com.nextGenZeta.LoanApplicationSystem.controllers.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegisterRequest {

    @NotBlank(message = "Username (email) cannot be blank")
    @Email(message = "Must be a valid email address")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    @NotBlank(message = "Date of birth cannot be blank")
    private String dob;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
}
