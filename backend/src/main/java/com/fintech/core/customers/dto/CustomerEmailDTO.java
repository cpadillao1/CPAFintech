package com.fintech.core.customers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CustomerEmailDTO(
        Long id,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        String email,

        @NotNull(message = "El tipo de email es obligatorio")
        Long emailTypeId,

        Boolean isPrimary
) {}
