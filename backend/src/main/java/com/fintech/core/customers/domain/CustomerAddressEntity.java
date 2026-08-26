package com.fintech.core.customers.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "customer_address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    private String city;
    private String state;
    private String postalCode;
    private String country;

    @Column(name = "address_type_id", nullable = false)
    private Long addressTypeId;

    @Column(name = "is_primary")
    private Boolean isPrimary;
}