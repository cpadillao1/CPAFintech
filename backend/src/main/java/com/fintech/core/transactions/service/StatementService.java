package com.fintech.core.transactions.service;

import com.fintech.core.transactions.dto.TransactionDetailDTO;
import com.fintech.core.transactions.mapper.StatementMapper;
import com.fintech.core.transactions.repository.TransactionProjection;
import com.fintech.core.transactions.repository.TransactionQueryRepository;
import com.fintech.eod.service.ControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final TransactionQueryRepository queryRepository;
    private final StatementMapper statementMapper;
    private final ControlService controlService;

    public Page<TransactionDetailDTO> getAccountStatement(Long accountId, LocalDate start, LocalDate end, Pageable pageable) {
        // --- INICIO VALIDACIONES ---
        LocalDate systemDate = controlService.getControlEntity().getBusinessDate();

        // 1. Validar que la fecha 'end' no sea mayor a la fecha contable
        if (end.isAfter(systemDate)) {
            throw new IllegalArgumentException("The due date cannot be later than the business date: " + systemDate);
        }

        // 2. Validar consistencia de rango (Start no puede ser mayor que End)
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("The start date cannot be later than the end date.");
        }

        // 3. (Opcional) Limitar el rango de búsqueda (ej. no más de 1 año)
        if (start.isBefore(end.minusYears(1))) {
            throw new IllegalArgumentException("You can only view ranges of up to one year.");
        }
        // --- FIN VALIDACIONES ---
        // 1. Obtenemos la página de proyecciones
        Page<TransactionProjection> projectionPage = queryRepository.findFullStatementByAccountId(accountId, start, end, pageable);

        // 2. Mapeamos el contenido manteniendo la estructura de página
        return projectionPage.map(statementMapper::toDto);
        // Nota: Asegúrate que tu mapper tenga el método toDto(TransactionProjection p)
    }
}


