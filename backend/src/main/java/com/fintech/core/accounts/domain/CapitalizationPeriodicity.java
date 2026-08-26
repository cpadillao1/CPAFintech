package com.fintech.core.accounts.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CapitalizationPeriodicity {
    MONTHLY("MONTHLY", 1),
    BIMONTHLY("BIMESTRAL", 2),
    QUARTERLY("QUARTERLY", 3),
    SEMIANNUAL("SEMIANNUAL", 6),
    ANNUAL("ANNUAL", 12);

    private final String code;
    private final Integer months;

    public static Integer getMonthsByCode(String code) {
        return Arrays.stream(CapitalizationPeriodicity.values())
                .filter(p -> p.getCode().equalsIgnoreCase(code))
                .map(CapitalizationPeriodicity::getMonths)
                .findFirst()
                .orElse(1); // Por defecto Mensual si no se encuentra
    }
}

