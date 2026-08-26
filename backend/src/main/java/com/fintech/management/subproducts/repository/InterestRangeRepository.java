package com.fintech.management.subproducts.repository;

import com.fintech.management.subproducts.domain.InterestRangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterestRangeRepository extends JpaRepository<InterestRangeEntity, Integer> {

    // El nuevo motor de cálculo por Grupos de Interés
    @Query("SELECT ir FROM InterestRangeEntity ir " +
            "WHERE ir.group.id = :groupId " +
            "AND ir.status = 'ACTIVE' " +
            "AND :balance BETWEEN ir.minAmount AND ir.maxAmount")
    Optional<InterestRangeEntity> findActiveRateByBalance(
            @Param("groupId") Integer groupId,
            @Param("balance") BigDecimal balance
    );

    // Para el Popup del Frontend: Traer todos los rangos de un grupo específico
    List<InterestRangeEntity> findByGroupIdAndStatusOrderByMinAmountAsc(Integer groupId, String status);

    /**
     * Recupera todos los rangos registrados.
     * El Listener lo filtrará por estado 'ACTIVE' para el caché.
     */
    List<InterestRangeEntity> findAll();
}
