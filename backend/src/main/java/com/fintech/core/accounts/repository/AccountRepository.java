package com.fintech.core.accounts.repository;


import com.fintech.core.accounts.domain.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    @Query("SELECT a FROM AccountEntity a " +
            "WHERE a.generatesInterest = true " +
            "AND a.statusId = :activeStatusId " +
            "AND a.lastProcessedDate < :processDate " +
            "ORDER BY a.subproduct.id ASC") // <--- EL CAMBIO ES AQUÍ: de subproductId a subproduct.id
    List<AccountEntity> findAccountsForInterestProcessing(
            @Param("activeStatusId") Integer activeStatusId,
            @Param("processDate") java.time.LocalDate processDate);

    @Query("SELECT DISTINCT a FROM AccountEntity a " +
            "LEFT JOIN FETCH a.holders h " +
            "LEFT JOIN FETCH h.customer c " +
            "JOIN a.subproduct s " +
            "JOIN s.product p " +
            "WHERE (:productId IS NULL OR p.id = :productId) " +
            "AND (:subproductId IS NULL OR s.id = :subproductId) " +
            "AND (:accountNumber IS NULL OR a.accountNumber = :accountNumber)")
    List<AccountEntity> findByHierarchy(
            @Param("productId") Integer productId,
            @Param("subproductId") Integer subproductId,
            @Param("accountNumber") String accountNumber
    );
}
