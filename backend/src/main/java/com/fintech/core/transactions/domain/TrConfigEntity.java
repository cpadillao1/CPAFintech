package com.fintech.core.transactions.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tr_configs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TrConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long configId;

    @Column(name = "mnemonic")
    private String mnemonic;

    @Column(name = "is_active")
    private Boolean isActive;

    // En TrConfigEntity.java
    @ManyToOne(fetch = FetchType.EAGER) // Eager para tener el signo a mano
    @JoinColumn(name = "type_id")
    private TrTypeEntity type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id")
    private TrReasonEntity reason;
}
