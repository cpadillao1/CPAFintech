package com.fintech.core.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionDetailDTO(
        Long id,
        LocalDate businessDate,
        String description,
        String reference,
        BigDecimal amount,
        BigDecimal previousBalance,
        BigDecimal newBalance,
        String status,
        String typeLabel,
        String conceptName,
        String nameTran,
        String typeTran
) {}

