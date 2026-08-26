package com.fintech.management.subproducts.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InterestRangeDTO(
        Integer id,
        String rangeName,
        BigDecimal rateValue,
        String rateType,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String status
) {}

