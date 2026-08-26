package com.fintech.core.accounts.domain;


import com.fintech.management.subproducts.domain.SubproductEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "branch_id", nullable = false)
    private Integer branchId;

    @Column(name = "currency_id", nullable = false)
    private Long currencyId;

    @Column(name = "account_type", nullable = false, length = 3)
    private String accountType;

    @Column(name = "generates_interest", nullable = false)
    private Boolean generatesInterest;

    @Column(name = "opening_date", nullable = false)
    private LocalDate openingDate;

    // --- SALDOS ---
    @Column(name = "available_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;

    @Column(name = "balance_today", nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceToday;

    @Column(name = "balance_yesterday", nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceYesterday;

    @Column(name = "amount_hold", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountHold;

    // --- CONTROL EOD ---
    @Column(name = "last_processed_date", nullable = false)
    private LocalDate lastProcessedDate;

    @Column(name = "interest_remainder", precision = 19, scale = 10)
    private BigDecimal interestRemainder;

    @Column(name = "accrued_interest_month", precision = 19, scale = 10)
    private BigDecimal accruedInterestMonth;

    // --- ACUMULADORES DE MOVIMIENTOS ---
    @Column(name = "amount_nd_today")
    private BigDecimal amountNdToday;
    @Column(name = "amount_nd_yesterday")
    private BigDecimal amountNdYesterday;
    @Column(name = "last_date_nd")
    private LocalDateTime lastDateNd;

    @Column(name = "amount_nc_today")
    private BigDecimal amountNcToday;

    @Column(name = "amount_nc_yesterday")
    private BigDecimal amountNcYesterday;

    @Column(name = "last_date_nc")
    private LocalDateTime lastDateNc;

    // --- ESTADO Y TITULARIDAD ---
    @Column(name = "movement_restriction")
    private Short movementRestriction;

    @Column(name = "ownership_type_id", nullable = false)
    private Long ownershipTypeId;

    @Column(name = "status_id", nullable = false)
    private Long statusId;

    // --- AUDITORÍA Y CONCURRENCIA ---
    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "last_movement_date")
    private LocalDateTime lastMovementDate;

    @Column(name = "created_by", nullable = false, length = 30)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Integer periodicity;

    private Integer monthsAccumulated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subproduct_id", nullable = false)
    private SubproductEntity subproduct;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountHolder> holders = new ArrayList<>();

    @Transient // Esto indica que NO es una columna en la tabla 'accounts'
    private BigDecimal tempRate;

    @Transient // Solo vive en la memoria durante la ejecución del Batch
    private BigDecimal tempAccrued;

    @Transient
    private BigDecimal tempDailyInterest; // INTERÉS PURO (10 decimales)

    @Transient
    private BigDecimal tempRemainderBefore; // REMANENTE QUE VENÍA DE AYER

    @Transient
    private BigDecimal tempGrossInterest;

    // Campo transaccional (no se guarda en la tabla accounts, solo para el Writer)
    @Transient
    private BigDecimal amountToPay = BigDecimal.ZERO;

    @Transient
    public boolean shouldPayInterest() {
        if (periodicity == null || periodicity <= 0) {
            return false; // si no hay periodicidad definida, nunca paga
        }
        if (monthsAccumulated == null) {
            monthsAccumulated = 0; // inicializamos si está nulo
        }
        return monthsAccumulated % periodicity == 0;
    }

    public void addHolder(AccountHolder holder) {
        if (holders == null) holders = new ArrayList<>();
        holders.add(holder);
        holder.setAccount(this); // Esto vincula al hijo con el padre automáticamente
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (availableBalance == null)
            availableBalance = BigDecimal.ZERO;
        if (balanceToday == null)
            balanceToday = BigDecimal.ZERO;
        if (balanceYesterday == null)
            balanceYesterday = BigDecimal.ZERO;
        if (amountHold == null)
            amountHold = BigDecimal.ZERO;
        if (accruedInterestMonth == null)
            accruedInterestMonth = BigDecimal.ZERO;
        if (amountNdToday == null)
            amountNdToday = BigDecimal.ZERO;
        if (amountNdYesterday == null)
            amountNdYesterday = BigDecimal.ZERO;
        if (amountNcToday == null)
            amountNcToday = BigDecimal.ZERO;
        if (amountNcYesterday == null)
            amountNcYesterday = BigDecimal.ZERO;
        if (interestRemainder == null)
            interestRemainder = BigDecimal.ZERO;
        if (movementRestriction == null)
            movementRestriction = 0;

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
