package com.fintech.management.subproducts.service;

import com.fintech.audit.aspect.Audit;
import com.fintech.management.products.domain.ProductEntity;
import com.fintech.management.products.repository.ProductRepository;
import com.fintech.management.subproducts.domain.SubproductEntity;
import com.fintech.management.subproducts.dto.SubproductDTO;
import com.fintech.management.subproducts.mapper.SubproductMapper;
import com.fintech.management.subproducts.repository.InterestGroupRepository;
import com.fintech.management.subproducts.repository.SubproductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubproductService {

    private final SubproductRepository subproductRepository;
    private final ProductRepository productRepository;
    private final SubproductMapper subproductMapper;
    private final InterestGroupRepository interestGroupRepository;

    @PreAuthorize("hasAuthority('SPRO_CREATE')")
    @Audit(action = "SPRO_CREATE", module = "SUBPRODUCTS")
    @Transactional
    public SubproductDTO create(Integer productId, SubproductDTO dto) {
        // 1. Validación de existencia del padre
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("El producto financiero con ID " + productId + " no existe."));

        // 2. Mapeo y asociación
        SubproductEntity entity = subproductMapper.toEntity(dto);
        entity.setProduct(product);

        // --- LÓGICA DINÁMICA DE SECUENCIA ---
        // Deducimos la secuencia según el código del producto padre (SAV o CHK)
        String productCode = product.getCode(); // Asumiendo que ProductEntity tiene getCode()

        if ("SAV".equalsIgnoreCase(productCode)) {
            entity.setAccountSequenceId("SAV_GLOBAL");
        } else if ("CHK".equalsIgnoreCase(productCode)) {
            entity.setAccountSequenceId("CHK_GLOBAL");
        } else {
            // Opcional: Un default o lanzar error si el producto no tiene secuencia definida
            throw new EntityNotFoundException("No existe una secuencia definida para el tipo de producto: " + productCode);
        }
        // ------------------------------------

        if (dto.interestGroupId() != null) {
            var interestGroup = interestGroupRepository.findById(dto.interestGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("Estrategia no encontrada"));
            entity.setInterestGroup(interestGroup);
        }
        // 3. saveAndFlush: VITAL para que el @Audit capture errores de duplicados (ej: mismo code)
        SubproductEntity saved = subproductRepository.saveAndFlush(entity);

        return subproductMapper.toDto(saved);
    }


    public List<SubproductDTO> getSubproductsByProductId(Integer productId) {
        // El repositorio ya te devuelve las entidades
        List<SubproductEntity> entities = subproductRepository.findByProductId(productId);

        // El mapper ya sabe convertir de Entity a DTO (incluyendo el interestGroupId)
        return entities.stream()
                .map(subproductMapper::toDto)
                .toList();
    }

    public List<SubproductDTO> findAll() {
        return subproductRepository.findAll().stream()
                .map(subproductMapper::toDto)
                .toList();
    }

}