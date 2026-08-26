package com.fintech.management.products.repository;

import com.fintech.management.products.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    // Buscamos por el código único (ej: 'SAV')
    Optional<ProductEntity> findByCode(String code);
}