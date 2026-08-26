package com.fintech.core.transactions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReversalRequestDTO(
        @NotNull(message = "Original transaction ID is mandatory")
        Integer transactionId,

        @NotBlank(message = "Reversal reason is mandatory")
        String reason,

        @NotBlank(message = "User is mandatory")
        String user
) {}
