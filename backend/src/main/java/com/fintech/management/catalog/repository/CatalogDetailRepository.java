package com.fintech.management.catalog.repository;

import com.fintech.management.catalog.domain.CatalogDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CatalogDetailRepository extends JpaRepository<CatalogDetail, Long> {
    // Busca todos los detalles de un catálogo específico (ej: todos los GENDER)
    List<CatalogDetail> findByCatalogNameAndActiveTrue(String catalogName);

    List<CatalogDetail> findByActiveTrue();


    @Query("SELECT d FROM CatalogDetail d " +
            "WHERE d.catalog.name = :catalogName AND d.code = :code AND d.active = true")
    Optional<CatalogDetail> findByCatalogNameAndCode(
            @Param("catalogName") String catalogName,
            @Param("code") String code);

    // AÑADIR ESTE PARA EL SERVICE
    Optional<CatalogDetail> findByIdAndActiveTrue(Long id);
}

