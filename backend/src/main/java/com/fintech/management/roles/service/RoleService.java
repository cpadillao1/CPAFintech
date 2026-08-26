package com.fintech.management.roles.service;

import com.fintech.audit.aspect.Audit;
import com.fintech.management.functionalities.dto.FunctionalityDTO;
import com.fintech.management.functionalities.dto.RoleResponseDTO;
import com.fintech.management.functionalities.mapper.RoleFunctionalityMapper;
import com.fintech.management.roles.dto.RoleRequestDTO;
import com.fintech.management.menu.repository.FunctionalityRepository;
import com.fintech.management.functionalities.domain.FunctionalityEntity;
import com.fintech.management.functionalities.domain.RoleEntity;
import com.fintech.management.users.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final FunctionalityRepository functionalityRepository;
    private final RoleFunctionalityMapper roleMapper;

    /**
     * Crea un nuevo rol con su descripción y lista de permisos asociados.
     * @param dto Contiene nombre, descripción y lista de UUIDs de funcionalidades.
     * @return Mensaje de éxito para el sistema de auditoría.
     */
    @PreAuthorize("hasAuthority('FUNC_CREATE_PERM')")
    @Audit(action = "FUNC_CREATE_PERM", module = "FUNCTIONALITIES")
    @Transactional
    public String createRoleWithPermissions(RoleRequestDTO dto) { // 👈 Cambiado de void a String
        log.info("Iniciando creación de rol: {}", dto.name());

        // 1. Validar si el nombre del rol ya existe (Normalizado a mayúsculas)
        String normalizedName = dto.name().trim().toUpperCase();
        if (roleRepository.existsByName(normalizedName)) {
            throw new RuntimeException("El nombre del rol '" + normalizedName + "' ya está en uso");
        }

        // 2. Validar que la lista de IDs no sea nula o vacía
        if (dto.functionalityIds() == null || dto.functionalityIds().isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos una funcionalidad para el rol");
        }

        // 3. Buscar funcionalidades en la BD
        List<FunctionalityEntity> foundFunctions = functionalityRepository.findAllById(dto.functionalityIds());

        // 4. Verificar integridad: que todos los IDs enviados existan realmente
        if (foundFunctions.size() != dto.functionalityIds().size()) {
            log.error("Error de integridad: Se enviaron {} IDs pero solo se encontraron {}",
                    dto.functionalityIds().size(), foundFunctions.size());
            throw new RuntimeException("Uno o más IDs de funcionalidades no son válidos o no existen");
        }

        // 5. Mapear a Entidad y Guardar
        RoleEntity role = new RoleEntity();
        role.setName(normalizedName);
        role.setDescription(dto.description());
        role.setFunctionalities(new HashSet<>(foundFunctions));

        roleRepository.save(role);

        String successMessage = "Rol '" + normalizedName + "' creado exitosamente con " + foundFunctions.size() + " permisos";
        log.info(successMessage);

        return successMessage; // 👈 Retornamos el String para que el @Audit no reciba null
    }

    @PreAuthorize("hasAuthority('FUNC_QUERY_ROL')")
    @Audit(action = "FUNC_QUERY_ROL", module = "FUNCTIONALITIES")
    @Transactional(readOnly = true)
    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    @PreAuthorize("hasAuthority('FUNC_QUERY_ROL')")
    @Audit(action = "FUNC_QUERY_ROL", module = "FUNCTIONALITIES")
    @Transactional(readOnly = true)
    public Optional<RoleEntity> findById(UUID id) {
        return roleRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleWithFunctionalities(UUID roleId) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // 1. Convertimos todas las entidades a DTOs planos usando el Mapper
        List<FunctionalityDTO> allDtos = role.getFunctionalities().stream()
                .map(roleMapper::toFunctionalityDto)
                .collect(Collectors.toList());

        // 2. Construimos el árbol jerárquico
        List<FunctionalityDTO> hierarchicalFunctionalities = buildTree(allDtos);

        return RoleResponseDTO.builder()
                .roleId(role.getId())
                .roleName(role.getName())
                .description(role.getDescription())
                .functionalities(hierarchicalFunctionalities)
                .build();
    }

    private List<FunctionalityDTO> buildTree(List<FunctionalityDTO> allItems) {
        // Creamos un mapa para buscar rápidamente por ID
        Map<UUID, FunctionalityDTO> lookup = allItems.stream()
                .collect(Collectors.toMap(FunctionalityDTO::getId, f -> f));

        List<FunctionalityDTO> rootElements = new ArrayList<>();

        for (FunctionalityDTO item : allItems) {
            // Buscamos si este item tiene un padre en la lista de permitidos del rol

            //UUID parentId = findParentIdInEntity(item.getId(), allItems);

            // Nota: Como el DTO no tiene el parentId (por limpieza),
            // podemos obtenerlo de la entidad o añadirlo temporalmente al DTO.
            // Si lo añadimos al DTO es más fácil:

            if (item.getParentId() == null) {
                rootElements.add(item);
            } else {
                FunctionalityDTO parent = lookup.get(item.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(item);
                }
            }
        }

        // Ordenamos por sortOrder si es necesario
        rootElements.sort(Comparator.comparingInt(f -> f.getSortOrder() != null ? f.getSortOrder() : 0));
        return rootElements;
    }

}