package com.fintech.core.accounts.repository;

import com.fintech.core.accounts.domain.AccountBalanceSnapshotEntity;
import com.fintech.core.accounts.dto.AggregatedReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AccountSnapshotRepository extends JpaRepository<AccountBalanceSnapshotEntity, Long> {

    // Cambiamos List por Page
    Page<AccountBalanceSnapshotEntity> findByAccount_AccountNumberAndSnapshotDateBetweenOrderBySnapshotDateAsc(
            String accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT new com.fintech.core.accounts.dto.AggregatedReportDTO(" +
            ":date, " +
            "COALESCE(SUM(s.availableBalance), 0), " +
            "COALESCE(SUM(s.balanceToday), 0), " +
            "COALESCE(SUM(s.amountHold), 0), " +
            "COALESCE(SUM(s.accruedMonthToDate), 0), " +
            "COALESCE(SUM(s.totalNdDay), 0), " +
            "COALESCE(SUM(s.totalNcDay), 0)) " +
            "FROM AccountBalanceSnapshotEntity s " +
            "WHERE s.snapshotDate = :date") // Es vital filtrar por fecha
    AggregatedReportDTO getAggregatedTotalsByDate(@Param("date") LocalDate date);
}



