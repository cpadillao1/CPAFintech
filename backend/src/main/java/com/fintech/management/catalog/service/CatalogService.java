package com.fintech.management.catalog.service;

import com.fintech.management.catalog.domain.CatalogDetail;
import com.fintech.management.catalog.dto.CatalogDetailDTO;
import com.fintech.management.catalog.repository.CatalogDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogService {
    private final CatalogDetailRepository repository;

    public List<CatalogDetailDTO> getDetailsByCatalog(String catalogName) {
        // Llamamos al nuevo método del repositorio
        return repository.findByCatalogNameAndActiveTrue(catalogName)
                .stream()
                .map(d -> new CatalogDetailDTO(
                        d.getId(),
                        d.getCatalog().getName(), // Usamos getName() que sí existe
                        d.getCode(),
                        d.getName()
                ))
                .toList();
    }

    public List<CatalogDetailDTO> getAllActiveDetails() {
        return repository.findByActiveTrue()
                .stream()
                .map(d -> new CatalogDetailDTO(
                        d.getId(),
                        d.getCatalog().getName(),
                        d.getCode(),
                        d.getName()
                ))
                .toList();
    }

    // Cacheamos este resultado para no ir a la BD en cada apertura de cuenta
    @Cacheable(value = "catalog_ids")
    public Long getDetailIdByCode(String catalogName, String code) {
        return repository.findByCatalogNameAndCode(catalogName, code)
                .map(CatalogDetail::getId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró el código " + code + " en el catálogo " + catalogName));
    }


    // Para obtener el NOMBRE basado en un ID (Lo que necesitas para el DTO)
    public String getDetailNameById(Long id) {
        return repository.findById(id)
                .map(CatalogDetail::getName)
                .orElse("N/A");
    }

}

