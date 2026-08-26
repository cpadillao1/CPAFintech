package com.fintech.core.transactions.mapper;

import com.fintech.core.transactions.domain.LogMov;
import com.fintech.core.transactions.dto.TransactionRequestDTO;
import com.fintech.core.transactions.dto.TransactionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    /**
     * Convierte el DTO de entrada a la Entidad.
     * Nota: El saldo y la fecha de negocio se setean en el Service
     * por seguridad, no aquí.
     */
    public LogMov toEntity(TransactionRequestDTO dto) {
        if (dto == null) return null;

        return LogMov.builder()
                .accountId(dto.accountId())
                .originId(dto.originId())
                .amount(dto.amount())
                .description(dto.description())
                .transactionReference(dto.transactionReference())
                .createdBy(dto.createdBy())
                .terminalIp(dto.terminalIp())
                .configId(dto.configId())
                .build();
    }

    /**
     * Convierte la Entidad al DTO de respuesta para el Frontend.
     * Aquí podrías inyectar repositorios de catálogos si quisieras
     * traer los nombres (Nombres vs IDs).
     */
    public TransactionResponseDTO toResponse(LogMov entity) {
        if (entity == null) return null;

        return new TransactionResponseDTO(
                entity.getId(),
                entity.getAccountId(),
                "PENDING_TYPE_NAME", // Esto lo llenaremos en el Service con el catálogo
                "PENDING_ORIGIN_NAME", // Esto lo llenaremos en el Service con el catálogo
                entity.getBusinessDate(),
                entity.getAmount(),
                entity.getPreviousBalance(),
                entity.getNewBalance(),
                entity.getOperationDate(),
                entity.getDescription(),
                entity.getTransactionReference(),
                entity.getStatus()
        );
    }
}
