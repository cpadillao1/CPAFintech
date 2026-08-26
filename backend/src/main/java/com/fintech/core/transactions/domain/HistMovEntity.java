package com.fintech.core.transactions.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hist_mov")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistMovEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long histId;

    private Long idOriginal;
    private Long accountId;
    private Long transactionTypeId;
    private Long originId;
    private Long configId;
    private LocalDate businessDate;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private String description;
    private String transactionReference;
    private String status;
    private LocalDateTime createdAt;
    private String createdBy;
    private String terminalIp;
    private LocalDateTime reversedAt;
    private String reversedBy;
    private String reversalReference;
    private LocalDateTime archivedAt;
}

