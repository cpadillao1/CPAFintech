package com.fintech.management.branches.dto;

import java.util.UUID;

/**
 * Record para representar una sucursal en los listados de selección (combos).
 */
public record BranchCatalogDTO(
        Integer id,      // El UUID real que espera el backend
        String code,  // Ejemplo: "0001"
        String name   // Ejemplo: "Sucursal Quito"
) {}
