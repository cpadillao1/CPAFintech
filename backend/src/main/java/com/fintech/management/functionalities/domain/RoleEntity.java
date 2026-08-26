package com.fintech.management.functionalities.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue // Hibernate detectará automáticamente que es un UUID
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "roles_functionalities",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "functionality_id")
    )
    private Set<FunctionalityEntity> functionalities;

}
