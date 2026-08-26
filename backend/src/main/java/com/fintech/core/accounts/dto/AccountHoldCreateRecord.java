package com.fintech.core.accounts.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountHoldCreateRecord(
        @NotBlank String accountNumber,
        BigDecimal amount,
        Long holdTypeId,
        String referenceNumber,
        String description,
        LocalDate startDate,
        LocalDate expiryDate,
        @NotBlank String createdBy
) {}
