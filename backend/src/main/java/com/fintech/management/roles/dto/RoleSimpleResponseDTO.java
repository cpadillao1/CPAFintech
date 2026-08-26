package com.fintech.management.roles.dto;

import java.util.UUID;

/**
 * DTO optimizado para listas y ComboBoxes.
 * Solo transporta lo estrictamente necesario.
 */
public record RoleSimpleResponseDTO(
        UUID id,
        String name
) {}
