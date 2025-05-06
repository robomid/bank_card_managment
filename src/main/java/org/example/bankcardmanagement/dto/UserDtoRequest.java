package org.example.bankcardmanagement.dto;

public record UserDtoRequest(
        String email,
        String username,
        Boolean enabled
) {
}
