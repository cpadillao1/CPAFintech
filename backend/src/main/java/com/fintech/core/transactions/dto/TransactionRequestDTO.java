package com.fintech.core.transactions.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para la captura de nuevos movimientos (Depósitos, Retiros, etc.)
 * Usamos Record para garantizar inmutabilidad.
 */
public record TransactionRequestDTO(

        @NotNull(message = "Account ID is mandatory")
        Long accountId,

        //@NotNull(message = "Transaction Type is mandatory")
        //Integer transactionTypeId,

        @NotNull(message = "Origin is mandatory")
        Long originId,

        @NotNull(message = "Amount is mandatory")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @Size(max = 255, message = "Description too long")
        String description,

        @NotBlank(message = "Transaction reference is mandatory for idempotency")
        String transactionReference,

        @NotBlank(message = "The creator user is required")
        String createdBy,

        String terminalIp,

        Long configId,
        String originCode




) {}
