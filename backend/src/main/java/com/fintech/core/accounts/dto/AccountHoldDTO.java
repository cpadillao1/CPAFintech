package com.fintech.core.accounts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;


public record AccountHoldDTO (
    Long id,
    Long accountId,
    BigDecimal amount,
    Long holdTypeId,
    String holdTypeDescription,
    String referenceNumber,
    String description,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate startDate,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    LocalDate expiryDate,
    String createdBy,
    String status
    // Otros campos que necesites mostrar en el frontend
){}

