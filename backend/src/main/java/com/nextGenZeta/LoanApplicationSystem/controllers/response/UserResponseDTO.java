package com.nextGenZeta.LoanApplicationSystem.controllers.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;
    private String gender;
}
