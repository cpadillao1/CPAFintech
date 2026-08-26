package com.fintech.management.subproducts.dto;

import java.util.List;
import java.util.UUID;

public record InterestGroupDTO(
        Integer id,
        String code,
        String name,
        String description,
        String status,
        List<InterestRangeDTO> ranges
) {}
