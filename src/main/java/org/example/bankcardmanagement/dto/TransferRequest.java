package org.example.bankcardmanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "fromCardId must not be null")
        Long fromCardId,

        @NotNull(message = "toCardId must not be null")
        Long toCardId,

        @NotNull(message = "amount must not be null")
        @Positive(message = "amount must be greater than 0")
        BigDecimal amount
) { }
