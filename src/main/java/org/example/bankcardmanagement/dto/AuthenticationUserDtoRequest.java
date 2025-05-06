package org.example.bankcardmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationUserDtoRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address.")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 3, message = "Password must be at least 3 characters")
        String password
) {}
