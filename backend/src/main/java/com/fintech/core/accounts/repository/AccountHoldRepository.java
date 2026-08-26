package com.fintech.core.accounts.repository;

import com.fintech.core.accounts.domain.AccountHoldEntity; // Asegúrate de que este sea el nombre real
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface AccountHoldRepository extends JpaRepository<AccountHoldEntity, Long> {

    // ERROR ANTERIOR: SELECT h FROM AccountHold h ...
    // CORRECCIÓN: Usar el nombre exacto de la clase Entity
    @Query("SELECT h FROM AccountHoldEntity h WHERE h.statusId = :activeStatus AND h.expiryDate <= :processDate")
    List<AccountHoldEntity> findExpiredHolds(
            @Param("activeStatus") Integer activeStatus,
            @Param("processDate") LocalDate processDate
    );

    List<AccountHoldEntity> findByAccountIdAndStatusId(Long accountId, Long statusId);

    @Query("SELECT h FROM AccountHoldEntity h " +
            "WHERE h.account.accountNumber = :accountNumber ")// +
            //"AND h.statusId = :activeStatus")
    Page<AccountHoldEntity> findActiveHoldsByAccountNumber(
            @Param("accountNumber") String accountNumber,
            //@Param("activeStatus") Long activeStatus,
            Pageable pageable // <-- Agregar este parámetro
    );

}
