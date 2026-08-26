package com.fintech.core.accounts.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountHoldCreateDTO (
        Long accountId,
        BigDecimal amount,
        Long holdTypeId,
        String referenceNumber,
        String description,
        LocalDate expiryDate // Puede ser null si es indefinido
){}

