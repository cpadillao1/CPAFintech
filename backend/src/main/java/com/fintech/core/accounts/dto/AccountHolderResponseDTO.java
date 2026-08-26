package com.fintech.core.accounts.dto;


import jakarta.validation.constraints.NotBlank;

public record AccountHolderResponseDTO(
        Long id,
        Long customerId,
        Long holderTypeId,
        String holderTypeName, // Este lo llenas con d.name del catálogo al final
        Long statusId,
        @NotBlank String ownershipTypeCode
) {}

