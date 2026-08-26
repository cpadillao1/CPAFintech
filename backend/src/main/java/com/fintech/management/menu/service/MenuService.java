package com.fintech.management.menu.service;

import com.fintech.management.menu.dto.MenuResponseDTO;
import com.fintech.management.menu.repository.FunctionalityRepository;
import com.fintech.management.functionalities.domain.FunctionalityEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final FunctionalityRepository repository;

    public MenuService(FunctionalityRepository repository) {
        this.repository = repository;
    }

    public List<MenuResponseDTO> getFullMenuTree() {
        List<FunctionalityEntity> allFunctions = repository.findAll();

        return allFunctions.stream()
                .filter(f -> "MODULE".equals(f.getType()))
                // 1. ORDENAMOS LOS MÓDULOS
                .sorted(Comparator.comparingInt(f -> f.getSortOrder() != null ? f.getSortOrder() : 0))
                .map(module -> new MenuResponseDTO(
                        module.getId(),
                        module.getName(),
                        null,
                        module.getLabel(),
                        module.getCode(),
                        module.getIcon(),
                        module.getBlockBatch(),
                        buildSubmodules(module.getId(), allFunctions),
                        null
                ))
                .collect(Collectors.toList());
    }

    private List<MenuResponseDTO> buildSubmodules(UUID parentId, List<FunctionalityEntity> all) {
        return all.stream()
                .filter(f -> "SUBMODULE".equals(f.getType()) && parentId.equals(f.getParentId()))
                .sorted(Comparator.comparingInt(f -> f.getSortOrder() != null ? f.getSortOrder() : 0))
                .map(sub -> new MenuResponseDTO(
                        sub.getId(),      // 1. id (UUID)
                        null,             // 2. title (Los submódulos no suelen llevar title, solo los módulos)
                        sub.getName(),    // 3. name
                        sub.getLabel(),   // 4. label
                        sub.getCode(),    // 5. code
                        sub.getIcon(),    // 6. icon
                        sub.getBlockBatch(),
                        null,             // 7. children (Un submódulo no tiene más hijos en tu estructura actual)
                        buildActions(sub.getId(), all) // 8. actions (Nivel 3)
                ))
                .collect(Collectors.toList());
    }


    private List<MenuResponseDTO.ActionDTO> buildActions(UUID subId, List<FunctionalityEntity> all) {
        return all.stream()
                .filter(f -> "ACTION".equals(f.getType()) && subId.equals(f.getParentId()))
                // 3. ORDENAMOS LAS ACCIONES (Query, Create, Update...)
                .sorted(Comparator.comparingInt(f -> f.getSortOrder() != null ? f.getSortOrder() : 0))
                .map(act -> new MenuResponseDTO.ActionDTO(
                        act.getId(),
                        act.getLabel(),
                        act.getCode(),
                        act.getIcon(),
                        act.getBlockBatch()
                ))
                .collect(Collectors.toList());
    }
}