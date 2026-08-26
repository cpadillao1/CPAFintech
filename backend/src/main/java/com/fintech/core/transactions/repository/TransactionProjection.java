package com.fintech.core.transactions.repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TransactionProjection {
    Long getId();
    LocalDateTime getCreatedAt();
    LocalDate getBusinessDate();
    String getDescription();
    String getReference();
    BigDecimal getAmount();
    BigDecimal getPreviousBalance();
    BigDecimal getNewBalance();
    String getStatus();
    String getTypeLabel();
    String getConceptName();
    String getNameTran();
    String getTypeTran();
}
