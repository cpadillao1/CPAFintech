package com.fintech.eod.dto;

import java.math.BigDecimal;

public record InterestRangeDTO(
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal rateValue
) {}
