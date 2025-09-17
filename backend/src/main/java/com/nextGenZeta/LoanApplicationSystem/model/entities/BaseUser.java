package com.nextGenZeta.LoanApplicationSystem.model.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nextGenZeta.LoanApplicationSystem.model.enums.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    @Email(message = "Username must be a valid email address")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character"
    )
    private String password;

    @NotNull(message = "Role cannot be null")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "baseUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private CustomerProfile customerProfile;

}
