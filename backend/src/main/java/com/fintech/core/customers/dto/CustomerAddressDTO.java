package com.fintech.core.customers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CustomerAddressDTO(
        Long id,

        @NotBlank(message = "La dirección es obligatoria")
        String addressLine,

        @NotBlank(message = "La ciudad es obligatoria")
        String city,

        String state,

        @NotBlank(message = "El país es obligatorio")
        String country,

        String postalCode,

        @NotNull(message = "El tipo de dirección es obligatorio")
        Long addressTypeId,

        Boolean isPrimary
) {}
