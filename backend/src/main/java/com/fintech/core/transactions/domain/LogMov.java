package com.fintech.core.transactions.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_mov")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogMov {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "transaction_type_id", nullable = false)
    private Long transactionTypeId;

    @Column(name = "origin_id", nullable = false)
    private Long originId; // Relación con catalog_detail (ONLINE, BATCH, ATM, etc.)

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    // Precisión bancaria: 18 enteros, 2 decimales
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "previous_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal previousBalance;

    @Column(name = "new_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal newBalance;

    @Column(name = "operation_date", nullable = false, updatable = false)
    private LocalDateTime operationDate;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "transaction_reference", nullable = false, unique = true, length = 100)
    private String transactionReference;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // REGISTERED, REVERSED

    // --- BLOQUE DE REVERSO ---
    @Column(name = "reversed_at")
    private LocalDateTime reversedAt;

    @Column(name = "reversed_by", length = 50)
    private String reversedBy;

    @Column(name = "reversal_reference", length = 100)
    private String reversalReference;

    // --- AUDITORÍA ---
    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "terminal_ip", length = 45)
    private String terminalIp;

    @Column(name = "config_id", nullable = false)
    private Long configId;

    /**
     * Hook automático de JPA para antes de insertar
     */
    @PrePersist
    protected void onCreate() {
        // Fecha y hora real del servidor al momento del insert
        this.operationDate = LocalDateTime.now();

        // Estado por defecto si llega nulo
        if (this.status == null) {
            this.status = "REGISTERED";
        }

        // El originId y businessDate DEBEN venir del Service
        // ya que dependen de la lógica de negocio y del cierre de día.
    }
}

