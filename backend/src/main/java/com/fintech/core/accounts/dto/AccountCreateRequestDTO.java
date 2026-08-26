package com.fintech.core.accounts.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

public record AccountCreateRequestDTO(

        @NotNull(message = "El cliente titular es obligatorio")
        Long customerId,

        @NotNull(message = "El subproducto es obligatorio")
        Integer subproductId,

        @NotNull(message = "La sucursal es obligatoria")
        Integer branchId,

        @NotNull(message = "La moneda es obligatoria")
        Long currencyId,

        @NotNull(message = "El tipo de titularidad es obligatorio")
        Long ownershipTypeId,

        @NotBlank(message = "El usuario creador es obligatorio")
        String createdBy,

        @NotEmpty(message = "Debe haber al menos un titular registrado")
        @Valid // Importante para validar los objetos dentro de la lista
        List<AccountHolderRequestDTO> holders
) {
    // Record anidado para los titulares
    public record AccountHolderRequestDTO(
            @NotNull(message = "ID de cliente requerido")
            Long customerId,

            @NotNull(message = "Rol de titular requerido")
            String ownershipTypeCode
    ) {}
}
