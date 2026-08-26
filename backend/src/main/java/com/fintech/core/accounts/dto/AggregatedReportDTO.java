package com.fintech.core.accounts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AggregatedReportDTO(
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate reportDate,
        BigDecimal totalAvailable,
        BigDecimal totalBalance,
        BigDecimal totalHolds,
        BigDecimal totalAccruedInterest,
        BigDecimal totalNd,
        BigDecimal totalNc
) {}
