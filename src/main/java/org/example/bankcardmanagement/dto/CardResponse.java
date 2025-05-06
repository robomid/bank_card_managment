package org.example.bankcardmanagement.dto;

import org.example.bankcardmanagement.model.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponse(
        Long id,
        String cardNumber,
        String cardHolderName,
        LocalDate expirationDate,
        BigDecimal balance,
        CardStatus status
) {
}
