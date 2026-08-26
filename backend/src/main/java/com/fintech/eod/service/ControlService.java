package com.fintech.eod.service;


import com.fintech.eod.exception.BusinessRuleException;
import com.fintech.management.control_system.domain.ControlSystemEntity;
import com.fintech.management.control_system.domain.ControlSystemStatus;
import com.fintech.management.control_system.repository.ControlSystemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class ControlService {

    private final ControlSystemRepository controlRepository;

    public ControlService(ControlSystemRepository controlRepository) {
        this.controlRepository = controlRepository;
    }

    public LocalDate getBeforeBusinessDate() {
        return controlRepository.findByStatus(ControlSystemStatus.IN_CLOSING.name())
                .map(ControlSystemEntity::getBeforeBusinessDate)
                .orElseThrow(() -> new RuntimeException("CRITICAL ERROR 0: No configuration was found in control_system"));
    }

    // Nuevo metodo para obtener toda la configuración
    public ControlSystemEntity getControlEntity() {
        return controlRepository.findByStatus(ControlSystemStatus.IN_CLOSING.name())
                .orElseThrow(() -> new RuntimeException("CRITICAL ERROR 1: No configuration was found in control_system"));
    }

    public void validateSystemReadyForClosing() {
        ControlSystemEntity control = controlRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Control System not initialized"));

        if (ControlSystemStatus.IN_CLOSING.name().equals(control.getStatus())) {
            throw new BusinessRuleException("The system is already in the process of (IN_CLOSING).", HttpStatus.CONFLICT);
        }
    }

    public void lockSystemForClosing() {
        ControlSystemEntity control = controlRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Control System not initialized"));

        if (ControlSystemStatus.IN_CLOSING.name().equals(control.getStatus())) {
            throw new BusinessRuleException("The system is already locked.", HttpStatus.CONFLICT);
        }

        control.setStatus(ControlSystemStatus.IN_CLOSING.name());
        control.setEodStartAt(LocalDateTime.now());

        controlRepository.save(control);
        //log.info(">>> Closing Phase Begun: Status IN_CLOSING activado.");
    }


    public void unlockSystemAndAdvanceDate() {
        ControlSystemEntity control = controlRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Control System not initialized"));

        // Validamos que el sistema realmente esté en cierre antes de abrirlo (opcional pero recomendado)
        if (!ControlSystemStatus.IN_CLOSING.name().equals(control.getStatus())) {
            throw new BusinessRuleException("El sistema no está en cierre. No se puede ejecutar la apertura.", HttpStatus.CONFLICT);
        }

        LocalDate newBusinessDate = control.getBusinessDate().plusDays(1);
        control.setBusinessDate(newBusinessDate);
        control.setBeforeBusinessDate(control.getBeforeBusinessDate().plusDays(1));
        control.setAfterBusinessDate(control.getAfterBusinessDate().plusDays(1));
        // Validación fin de mes
        boolean isEndOfMonth = newBusinessDate.equals(newBusinessDate.with(TemporalAdjusters.lastDayOfMonth()));
        control.setMonthEnd(isEndOfMonth);
        // Asumiendo que tienes un estado ACTIVE en tu Enum ControlSystemStatus
        control.setStatus(ControlSystemStatus.ACTIVE.name());

        control.setEodStartAt(LocalDateTime.now()); // campo para auditar el fin del EoD

        controlRepository.save(control);
    }

}

