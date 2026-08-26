package com.fintech.core.accounts.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "account_balance_snapshot")
public class AccountBalanceSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "snapshot_date")
    private LocalDate snapshotDate;

    @Column(name = "available_balance")
    private BigDecimal availableBalance;

    @Column(name = "balance_today")
    private BigDecimal balanceToday;

    @Column(name = "applied_rate")
    private BigDecimal appliedRate;

    @Column(name = "interest_day")
    private BigDecimal interestDay;

    @Column(name = "total_nd_day")
    private BigDecimal totalNdDay;

    @Column(name = "total_nc_day")
    private BigDecimal totalNcDay;

    @Column(name = "gross_interest")
    private BigDecimal grossInterest;

    @Column(name = "amount_hold")
    private BigDecimal amountHold;

    @Column(name = "accrued_month_to_date")
    private BigDecimal accruedMonthToDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "remainder_before") // Nombre exacto en la tabla SQL
    private BigDecimal remainderBefore;

    @Column(name = "remainder_after") // Nombre exacto en la tabla SQL
    private BigDecimal remainderAfter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private AccountEntity account; // Relación para lectura


}

