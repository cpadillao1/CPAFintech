package com.fintech.core.accounts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account_sequences")
@Getter
@Setter
public class AccountSequence {
    @Id
    @Column(name = "sequence_id")
    private String sequenceId;
    private String prefix;
    @Column(name = "current_value")
    private Long currentValue;
    private Integer length;
    private String description;
}

