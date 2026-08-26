package com.fintech.core.accounts.dto;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountResponseDTO(
        Long id,
        String accountNumber,
        String accountType,
        BigDecimal availableBalance,
        BigDecimal balanceToday,
        LocalDate lastProcessedDate,
        String statusName,
        Boolean generatesInterest
) {}

