package com.fintech.audit.service;

import com.fintech.audit.domain.AuditLogEntity;
import com.fintech.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditRepository;

    /**
     * REQUIRES_NEW: Abre una transacción física totalmente independiente.
     * Si la creación del producto falla y hace rollback, este LOG se guarda sí o sí.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveIndependentLog(AuditLogEntity logEntry) {
        try {
            // saveAndFlush obliga a JPA a enviar el SQL a la DB de inmediato
            auditRepository.saveAndFlush(logEntry);
        } catch (Exception e) {
            // Log de emergencia por si la auditoría misma falla (ej. DB caída)
            log.error("CRITICAL: No se pudo guardar el rastro de auditoría: {}", e.getMessage());
        }
    }
}