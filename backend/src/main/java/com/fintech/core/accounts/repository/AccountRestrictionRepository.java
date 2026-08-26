package com.fintech.core.accounts.repository;


import com.fintech.core.accounts.domain.AccountRestrictionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRestrictionRepository extends JpaRepository<AccountRestrictionEntity, Long> {

    /**
     * Busca restricciones por cuenta y estado.
     * Ideal para el validador de transacciones (usa el índice compuesto).
     */
    List<AccountRestrictionEntity> findByAccountIdAndStatusCatId(Long accountId, Long statusCatId);

    /**
     * Busca todas las restricciones de una cuenta para el historial del cliente.
     */
    List<AccountRestrictionEntity> findByAccountId(Long accountId);

    /**
     * Busca restricciones globales por estado.
     * Útil para el tablero de control (Active/Expired) que mencionaste.
     */
    Page<AccountRestrictionEntity> findByStatusCatId(Long statusId, Pageable pageable);

    /**
     * Verifica si ya existe una restricción activa del mismo tipo para evitar duplicados.
     */
    Optional<AccountRestrictionEntity> findByAccountIdAndRestrictionTypeCatIdAndStatusCatId(
            Long accountId,
            Long restrictionTypeCatId,
            Long statusCatId
    );
}
