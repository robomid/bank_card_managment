package org.example.bankcardmanagement.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDtoResponse(
        Long id,
        String email,
        String username,
        boolean enabled,
        Set<String> roles,
        String verificationCode,
        LocalDateTime verificationCodeExpiration
) { }
