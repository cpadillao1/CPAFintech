package com.fintech.core.transactions.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tr_types")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TrTypeEntity {

    @Id
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "code", length = 5, unique = true, nullable = false)
    private String code; // Ej: 'DB' o 'CR'

    @Column(name = "name", length = 50, nullable = false)
    private String name; // Ej: 'Débito' o 'Crédito'
}
