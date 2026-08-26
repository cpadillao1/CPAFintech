package com.fintech.management.control_system.service;

import com.fintech.audit.aspect.Audit;
import com.fintech.management.control_system.domain.ControlSystemEntity;
import com.fintech.management.control_system.dto.SystemStatusDTO;
import com.fintech.management.control_system.repository.ControlSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemControlService {

    private final ControlSystemRepository repository;

    @PreAuthorize("hasAuthority('CTRL_QUERY')")
    @Audit(action = "CTRL_QUERY", module = "CONTROL_SYSTEM")
    @Transactional(readOnly = true)
    public SystemStatusDTO getFullConfigDTO() {
        ControlSystemEntity entity = repository.findById(1)
                .orElseThrow(() -> new RuntimeException("Error: Registro de control no inicializado en la BD"));

        // Mapeo manual (o puedes usar MapStruct si lo tienes instalado)
        SystemStatusDTO dto = new SystemStatusDTO();
        dto.setId(entity.getId());
        dto.setBusinessDate(entity.getBusinessDate());
        dto.setBeforeBusinessDate(entity.getBeforeBusinessDate());
        dto.setAfterBusinessDate(entity.getAfterBusinessDate());
        dto.setStatus(entity.getStatus());
        dto.setLastUpdate(entity.getLastUpdate());
        dto.setVersion(entity.getVersion());
        return dto;
    }


    /**
     * Uso Interno: Retorna la entidad directamente para evitar mapeos innecesarios
     * cuando se requiere la configuración desde otros servicios del Core.
     */
    @Transactional(readOnly = true)
    public ControlSystemEntity getControlEntity() {
        return repository.findById(1)
                .orElseThrow(() -> new RuntimeException("Error: Registro de control no inicializado en la BD"));
    }

}


