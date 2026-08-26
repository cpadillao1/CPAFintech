package com.fintech.core.accounts.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data // <-- Esto genera Getters, Setters, toString, Equals y HashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSnapshotDTO {

    private String accountNumber; // Simplificamos para el FE
    private LocalDate snapshotDate;
    private BigDecimal availableBalance;
    private BigDecimal balanceToday;
    private BigDecimal appliedRate;
    private BigDecimal interestDay;
    private BigDecimal remainderBefore;
    private BigDecimal remainderAfter;
    private BigDecimal grossInterest;
    private BigDecimal accruedMonthToDate;
    private BigDecimal amountHold;
    private BigDecimal totalNdDay;
    private BigDecimal totalNcDay;
    // Omitimos campos técnicos como created_at o la entidad Account completa
}

