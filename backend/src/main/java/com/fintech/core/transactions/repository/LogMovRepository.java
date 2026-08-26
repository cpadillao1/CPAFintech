package com.fintech.core.transactions.repository;

import com.fintech.core.transactions.domain.LogMov;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogMovRepository extends JpaRepository<LogMov, Integer> {

    /**
     * Verifica si una referencia de transacción ya existe.
     * Vital para evitar duplicidad de cargos/abonos por reintentos de red.
     */
    boolean existsByTransactionReference(String transactionReference);

    /**
     * Busca todos los movimientos de una cuenta ordenados del más reciente al más antiguo.
     * Útil para la "Cartola" o Estado de Cuenta en el Frontend.
     */
    List<LogMov> findByAccountIdOrderByBusinessDateDescOperationDateDesc(Long accountId);

    /**
     * Busca transacciones por cuenta y estado.
     * Ideal para tu pantalla de reversos: filtrará solo las 'REGISTERED'.
     */
    List<LogMov> findByAccountIdAndStatus(Long accountId, String status);

    /**
     * Busca por referencia única.
     */
    Optional<LogMov> findByTransactionReference(String transactionReference);

    /**
     * Ejemplo de Query personalizada: Obtener el total movido por una cuenta en un día específico.
     * Útil para controles de límites diarios en el EoD.
     */
    @Query("SELECT SUM(l.amount) FROM LogMov l WHERE l.accountId = :accId AND l.businessDate = :bDate AND l.status = 'REGISTERED'")
    java.math.BigDecimal sumAmountByAccountAndBusinessDate(@Param("accId") Integer accountId,
                                                           @Param("bDate") java.time.LocalDate businessDate);
}
