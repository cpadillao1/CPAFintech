package com.fintech.core.accounts.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_restrictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRestrictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "restriction_type_cat_id", nullable = false)
    private Long restrictionTypeCatId;

    @Column(name = "reason_code_cat_id", nullable = false)
    private Long reasonCodeCatId;

    @Column(name = "status_cat_id", nullable = false)
    private Long statusCatId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "authorizer", nullable = false, length = 30)
    private String authorizer;

    @Column(name = "observations", length = 500)
    private String observations;

    @Column(name = "release_authorizer", length = 30)
    private String releaseAuthorizer;

    @Column(name = "release_observations", length = 500)
    private String releaseObservations;

    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

