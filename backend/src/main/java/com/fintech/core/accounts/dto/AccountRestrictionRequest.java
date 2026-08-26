package com.fintech.core.accounts.dto;

import java.time.LocalDate;

public record AccountRestrictionRequest(
        String accountNumber, // Long accountId,
        Long restrictionTypeCatId,
        Long reasonCodeCatId,
        Long statusCatId,
        LocalDate startDate,
        String observations,
        String authorizer
) {}
