package com.fintech.management.menu.dto;

import java.util.List;
import java.util.UUID;

/**
 * Record para representar la estructura jerárquica del menú.
 * Es inmutable, limpio y moderno.
 */
public record MenuResponseDTO(
        UUID id,
        String title,           // Para MODULE (ej: CONFIGURATION)
        String name,            // Para SUBMODULE (ej: USERS)
        String label,           // Etiqueta amigable (ej: "Gestión de Usuarios")
        String code,            // Código de permiso (ej: USER)
        String icon,            // Emoji o clase de icono
        boolean blockBatch,
        List<MenuResponseDTO> children, // Submódulos hijos
        List<ActionDTO> actions         // Acciones finales (botones)
) {
    /**
     * Record interno para las acciones (Nivel 3)
     */
    public record ActionDTO(
            UUID id,
            String n, // Nombre de la acción (Label)
            String c,  // Código de la acción (Code)
            String i,
            boolean blockBatch
    ) {}
}
