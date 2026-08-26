package com.fintech.core.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Integer id,
        Long accountId,
        String transactionTypeName, // "Depósito", "Retiro"
        String originName,          // "Ventanilla", "ATM"
        LocalDate businessDate,
        BigDecimal amount,
        BigDecimal previousBalance,
        BigDecimal newBalance,
        LocalDateTime operationDate,
        String description,
        String transactionReference,
        String status
) {}
