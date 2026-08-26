package com.fintech.core.accounts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AccountHoldReleaseRecord(
        @NotBlank(message = "Las observaciones son obligatorias")
        @Size(min = 10, max = 500, message = "La observación debe tener entre 10 y 500 caracteres")
        String observations,
        String releasedBy
) {}
