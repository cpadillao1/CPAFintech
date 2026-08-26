package com.fintech.management.catalog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "catalog_detail")
@Getter
@Setter
public class CatalogDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "catalog_id", nullable = false)
    private Catalog catalog;

    @Column(nullable = false)
    private String code; // Ej: "M", "F"
    @Column(nullable = false)
    private String name; // Ej: "Masculino", "Femenino"
    private String description;
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

