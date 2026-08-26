package com.fintech.core.accounts.dto;

public record AccountHolderDetailDTO(
        Long customerId,
        String fullName, // Nombre concatenado
        String documentNumber,
        String ownershipType // Ej: "TITULAR PRINCIPAL", "ADICIONAL"
) {}

