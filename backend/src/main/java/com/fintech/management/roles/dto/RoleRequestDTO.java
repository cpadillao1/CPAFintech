package com.fintech.management.roles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;

/**
 * DTO para la creación de roles con validaciones JSR-303.
 */
public record RoleRequestDTO(
        @NotBlank(message = "El nombre del rol es obligatorio")
        String name,
        @NotBlank(message = "La descripcion del rol es obligatorio")
        String description,
        @NotEmpty(message = "Debe seleccionar al menos un permiso")
        Set<UUID> functionalityIds
) {}