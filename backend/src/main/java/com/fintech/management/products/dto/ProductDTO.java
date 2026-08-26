package com.fintech.management.products.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDTO(
        Integer id,
        String code,
        String name,
        String status,
        LocalDateTime createdAt
) {}
