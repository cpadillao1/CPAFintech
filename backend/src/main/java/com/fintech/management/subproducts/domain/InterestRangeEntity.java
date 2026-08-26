package com.fintech.management.subproducts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "interest_range")
@Getter
@Setter
public class InterestRangeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <--- FALTA ESTA LÍNEA AQUÍ
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private InterestGroupEntity group;

    @Column(name = "range_name", nullable = false)
    private String rangeName;

    private BigDecimal rateValue;
    private String rateType;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String status;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


}

