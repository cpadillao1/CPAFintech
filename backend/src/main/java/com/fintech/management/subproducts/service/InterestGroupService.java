package com.fintech.management.subproducts.service;

import com.fintech.management.subproducts.domain.InterestGroupEntity;
import com.fintech.management.subproducts.dto.InterestGroupDTO;
import com.fintech.management.subproducts.mapper.InterestGroupMapper;
import com.fintech.management.subproducts.repository.InterestGroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestGroupService {

    // Necesitamos el de Grupos para el Save y el de Rangos para consultas específicas
    private final InterestGroupRepository groupRepository;
    private final InterestGroupMapper mapper;

    @Transactional(readOnly = true)
    public List<InterestGroupDTO> findAllActive() {
        // Ahora usamos el repositorio correcto (Group)
        return groupRepository.findByStatus("ACTIVE").stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InterestGroupDTO createGroup(InterestGroupDTO dto) {
        InterestGroupEntity entity = mapper.toEntity(dto);

        // Solo nos aseguramos de la relación bidireccional si el mapper no lo hizo
        if (entity.getRanges() != null) {
            entity.getRanges().forEach(range -> range.setGroup(entity));
        }

        try {
            InterestGroupEntity saved = groupRepository.save(entity);
            return mapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            // Log de emergencia por si la auditoría misma falla (ej. DB caída)
            String errorMessage = (e.getRootCause() != null)
                    ? e.getRootCause().getMessage()
                    : e.getMessage();

            log.error("Error de integridad al crear grupo de interés: {}", errorMessage);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public InterestGroupDTO findById(Integer id) {
        return groupRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Grupo de interés no encontrado con ID: " + id));
    }

}

