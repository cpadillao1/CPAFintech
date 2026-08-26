package com.fintech.core.accounts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AccountSearchDTO(
        Long id,
        String accountNumber,
        String productName,
        String subproductName,
        String status,
        String createdBy,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate openingDate,
        BigDecimal availableBalance,
        BigDecimal balanceToday,
        BigDecimal balanceYesterday,
        BigDecimal amountHold,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
        LocalDate lastProcessedDate,
        BigDecimal accruedInterestMonth,
        BigDecimal amountNdToday,
        BigDecimal amountNdYesterday,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime lastDateNd,
        BigDecimal amountNcToday,
        BigDecimal amountNcYesterday,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime lastDateNc,
        List<AccountHolderDetailDTO> holders

) {}

