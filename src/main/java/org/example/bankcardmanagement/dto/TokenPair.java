package org.example.bankcardmanagement.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) { }
