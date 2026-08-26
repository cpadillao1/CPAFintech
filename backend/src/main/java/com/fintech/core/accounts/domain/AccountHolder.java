package com.fintech.core.accounts.domain;

import com.fintech.core.customers.domain.CustomerEntity;
import com.fintech.management.catalog.domain.CatalogDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_holders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "status_id", nullable = false)
    private Long statusId;

    @Column(name = "created_by", length = 30)
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    // También asegúrate de tener el tipo de propiedad (Titular, Firmante, etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holder_type_id", nullable = false)
    private CatalogDetail ownershipType;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
