package com.fintech.management.catalog.dto;

import java.util.UUID;

public record CatalogDetailDTO(
        Long id,
        String catalogName,
        String code,
        String name
) {}
