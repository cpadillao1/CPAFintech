package com.fintech.core.transactions.repository;

import com.fintech.core.transactions.domain.LogMov;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionQueryRepository extends JpaRepository<LogMov, Long> {

    @Query(value = """
        SELECT * FROM (
            SELECT 
                m.id as id, m.business_date as businessDate, 
                m.description as description, m.transaction_reference as reference, 
                m.amount as amount, m.previous_balance as previousBalance, 
                m.new_balance as newBalance, m.status as status,
                t.code as typeLabel, r.name as nameTran, y.code as typeTran
            FROM log_mov m
            JOIN tr_configs c ON m.config_id = c.config_id
            JOIN tr_reasons r ON r.reason_id = c.reason_id
            JOIN tr_types y on y.type_id = c.type_id
            JOIN catalog_detail t ON c.type_id = t.id
            WHERE m.account_id = :accountId AND m.business_date BETWEEN :startDate AND :endDate
            
            UNION ALL
            
            SELECT 
                h.id_original as id, h.business_date as businessDate, 
                h.description as description, h.transaction_reference as reference, 
                h.amount as amount, h.previous_balance as previousBalance, 
                h.new_balance as newBalance, h.status as status,
                t.code as typeLabel, r.name as nameTran, y.code as typeTran
            FROM hist_mov h
            JOIN tr_configs c ON h.config_id = c.config_id
            JOIN tr_reasons r ON r.reason_id = c.reason_id
            JOIN tr_types y on y.type_id = c.type_id
            JOIN catalog_detail t ON c.type_id = t.id
            WHERE h.account_id = :accountId AND h.business_date BETWEEN :startDate AND :endDate
        ) combined
        ORDER BY combined.businessDate ASC, combined.id ASC -- ORDEN CRÍTICO
        """,
            countQuery = """
            SELECT count(*) FROM (
                SELECT m.id FROM log_mov m WHERE m.account_id = :accountId AND m.business_date BETWEEN :startDate AND :endDate
                UNION ALL
                SELECT h.id_original FROM hist_mov h WHERE h.account_id = :accountId AND h.business_date BETWEEN :startDate AND :endDate
            ) count_table
        """,
            nativeQuery = true)
    Page<TransactionProjection> findFullStatementByAccountId(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}



