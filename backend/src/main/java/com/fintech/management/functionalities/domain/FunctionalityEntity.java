package com.fintech.management.functionalities.domain;


import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "functionalities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunctionalityEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;
/*
    @Column(name = "parent_id")
    private UUID parentId;
*/
    @Column(nullable = false)
    private String type; // MODULE, SUBMODULE, ACTION

    @Column(unique = true)
    private String code;

    @Column(nullable = false)
    private String label;

    private String icon;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "block_batch", nullable = false)
    private Boolean blockBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FunctionalityEntity parent;

    // Helper para obtener el ID del padre de forma limpia
    public UUID getParentId() {
        return (parent != null) ? parent.getId() : null;
    }
}
