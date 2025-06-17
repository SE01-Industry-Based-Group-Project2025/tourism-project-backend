package com.sl_tourpal.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    // at least one upper, one lower, one digit
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
             message = "Password must contain uppercase, lowercase, and number")
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^\\+?[\\d\\s-()]+$",
             message = "Invalid phone number format")
    private String phone;

    // optional override, but defaulted to ROLE_USER in controller
    private String role;
}
