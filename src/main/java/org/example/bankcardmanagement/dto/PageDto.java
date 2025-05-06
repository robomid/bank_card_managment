package org.example.bankcardmanagement.dto;

public record PageDto(
        int pageNumber,
        int pageSize,
        String sortBy,
        String sortDirection
) { }
