package com.fintech.management.catalog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "catalog")
@Getter
@Setter
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //private String code; // Ej: "GENDER", "IDENTIFICATION_TYPE"
    private String name; // Ej: "Género", "Tipo de Identificación"
    private String description;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL)
    private List<CatalogDetail> details;
}

