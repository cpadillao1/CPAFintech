package com.fintech.management.branches.service;

import com.fintech.audit.aspect.Audit;
import com.fintech.management.branches.domain.BranchEntity;
import com.fintech.management.branches.dto.BranchCatalogDTO;
import com.fintech.management.branches.dto.BranchDTO;
import com.fintech.management.branches.mapper.BranchMapper;
import com.fintech.management.branches.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @PreAuthorize("hasAuthority('BRAN_CREATE')")
    @Audit(action = "BRAN_CREATE", module = "BRANCHES")
    @Transactional
    public BranchDTO createBranch(BranchDTO branchDto) {
        // 1. Validar si el código de sucursal ya existe (Regla de negocio)
        if (branchRepository.findByCode(branchDto.getCode()).isPresent()) {
            throw new RuntimeException("El código de sucursal " + branchDto.getCode() + " ya existe.");
        }

        // 2. Convertir DTO a Entidad
        BranchEntity branchEntity = branchMapper.toEntity(branchDto);

        // 3. Guardar en BD
        BranchEntity savedBranch = branchRepository.save(branchEntity);

        // 4. Retornar el DTO mapeado
        return branchMapper.toDto(savedBranch);
    }

    @PreAuthorize("hasAuthority('BRAN_QUERY')")
    @Audit(action = "BRAN_QUERY", module = "BRANCHES")
    @Transactional(readOnly = true)
    public Page<BranchDTO> getAllBranches(int page, int size) {
        // Añadimos Sort.by("code").ascending() para el orden numérico/alfabético
        Pageable pageable = PageRequest.of(page, size, Sort.by("code").ascending());

        return branchRepository.findAll(pageable)
                .map(branchMapper::toDto);
    }

    @PreAuthorize("hasAuthority('BRAN_QUERY')")
    @Audit(action = "BRAN_QUERY", module = "BRANCHES")
    @Transactional(readOnly = true)
    public BranchDTO getBranchByCode(String code) {
        return branchRepository.findByCode(code)
                .map(branchMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con código: " + code));
    }

    @PreAuthorize("hasAuthority('BRAN_UPDATE')")
    @Audit(action = "BRAN_UPDATE", module = "BRANCHES")
    @Transactional
    public BranchDTO updateBranch(String code, BranchDTO dto) {
        // 1. Buscar la sucursal existente
        BranchEntity entity = branchRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada con código: " + code));

        // 2. Actualizar los campos (excepto el código, que es el ID)
        entity.setName(dto.getName());
        entity.setCity(dto.getCity());
        entity.setAddress(dto.getAddress());

        // 3. Guardar y retornar el DTO actualizado
        BranchEntity updatedEntity = branchRepository.save(entity);
        return branchMapper.toDto(updatedEntity); // Usa tu mapper o constructor manual
    }

    @PreAuthorize("hasAuthority('BRAN_QUERY')")
    @Audit(action = "BRAN_QUERY", module = "BRANCHES")
    @Transactional(readOnly = true)
    public List<BranchCatalogDTO> findAllBranchesCatalog() {
        return branchRepository.findAllByOrderByCodeAsc() // Quitamos el filtro de status
                .stream()
                .map(branch -> new BranchCatalogDTO(
                        branch.getId(),
                        branch.getCode(),
                        branch.getName()
                ))
                .collect(Collectors.toList());
    }


}