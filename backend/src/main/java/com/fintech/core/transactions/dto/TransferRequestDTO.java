package com.fintech.core.transactions.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransferRequestDTO(

        @NotBlank(message = "Source account number is required")
        String sourceAccountId,

        @NotBlank(message = "Target account number is required")
        String targetAccountId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Source transaction config ID is required")
        Long sourceConfigId, // El ID para la Nota de Débito (eg. TRF-OUT)

        @NotNull(message = "Target transaction config ID is required")
        Long targetConfigId, // El ID para la Nota de Crédito (eg. TRF-IN)

        @NotBlank(message = "Transaction reference is required")
        String transactionReference,

        String description,

        @NotBlank(message = "User login is required")
        String createdBy,

        String terminalIp,

        String originCode
) {}

