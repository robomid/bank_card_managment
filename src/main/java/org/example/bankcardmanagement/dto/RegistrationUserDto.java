package org.example.bankcardmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegistrationUserDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address.")
        String email,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 3, message = "Password must be at least 3 characters")
        String password,

        @NotEmpty(message = "role cannot be empty")
        Set<String> roles
) { }
