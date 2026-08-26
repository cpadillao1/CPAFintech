package com.fintech.core.customers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CustomerPhoneDTO(
        Long id,

        @NotBlank(message = "El número de teléfono es obligatorio")
        String phoneNumber,

        @NotNull(message = "El tipo de teléfono es obligatorio")
        Long phoneTypeId,

        Boolean isPrimary
) {}
