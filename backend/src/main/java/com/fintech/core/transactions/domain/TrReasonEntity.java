package com.fintech.core.transactions.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tr_reasons")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TrReasonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reason_id")
    private Integer reasonId;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    // Relación con el tipo (Débito/Crédito)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private TrTypeEntity type;
}

