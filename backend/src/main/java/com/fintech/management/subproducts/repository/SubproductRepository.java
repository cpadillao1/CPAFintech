package com.fintech.management.subproducts.repository;

import com.fintech.management.subproducts.domain.SubproductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubproductRepository extends JpaRepository<SubproductEntity, Integer> {
    // Para buscar un subproducto específico (ej: 'SAV01')
    Optional<SubproductEntity> findByCode(String code);

    // Para listar todos los subproductos de un producto padre
    List<SubproductEntity> findByProductId(Integer productId);
}