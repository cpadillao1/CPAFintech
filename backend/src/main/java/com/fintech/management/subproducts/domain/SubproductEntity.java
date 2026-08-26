package com.fintech.management.subproducts.domain;

import com.fintech.management.products.domain.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subproduct")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubproductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private ProductEntity product;

    @Column(unique = true, nullable = false, length = 15)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "generates_interest")
    private Boolean generatesInterest;

    @Column(nullable = false, length = 10)
    private String statementFrequency;

    // Relación con las Tasas de Interés (Tiering)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_group_id")
    private InterestGroupEntity interestGroup;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "account_sequence_id")
    private String accountSequenceId;

    // 1. Agrega este campo para mapeo directo (solo lectura)
    @Column(name = "interest_group_id", insertable = false, updatable = false)
    private Integer interestGroupId;

    // 2. Método para obtener el ID de manera segura
    public Integer getInterestGroupIdValue() {
        if (this.interestGroupId != null) {
            return this.interestGroupId;
        }
        return (this.interestGroup != null) ? this.interestGroup.getId() : null;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
    }
}
