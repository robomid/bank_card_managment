package org.example.bankcardmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyUserDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address.")
        String email,
        @NotBlank(message = "verificationCode is required")
        String verificationCode
) { }
