package com.nextGenZeta.LoanApplicationSystem.controllers.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordUpdateDTO {
    private String oldPassword;
    private String newPassword;
}
