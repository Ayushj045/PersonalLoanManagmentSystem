package com.nextGenZeta.LoanApplicationSystem.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other")
    private String gender;

    @NotBlank(message = "Date of birth cannot be blank")
    private String dob;

    @NotBlank(message = "Address cannot be blank")
    @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
    private String address;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 digits"
    )
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "system_user_id", nullable = false)
    @JsonBackReference
    private BaseUser baseUser;
}
