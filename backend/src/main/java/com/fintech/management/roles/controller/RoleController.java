package com.fintech.management.roles.controller;

import com.fintech.management.functionalities.dto.RoleResponseDTO;
import com.fintech.management.roles.dto.RoleRequestDTO;
import com.fintech.management.roles.dto.RoleSimpleResponseDTO;
import com.fintech.management.roles.service.RoleService;
import com.fintech.management.functionalities.domain.RoleEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // 👈 Necesario para el .collect

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 1. Guardar o Crear nuevo Rol con sus permisos
    @PostMapping("/save")
    public ResponseEntity<String> saveRole(@Valid @RequestBody RoleRequestDTO dto) {
        roleService.createRoleWithPermissions(dto);
        return new ResponseEntity<>("Rol guardado exitosamente", HttpStatus.CREATED);
    }

    // 1. Obtener lista simplificada para el ComboBox (GET /api/v1/roles)
    @GetMapping
    public ResponseEntity<List<RoleSimpleResponseDTO>> getAllRoles() {
        List<RoleSimpleResponseDTO> roles = roleService.findAll().stream()
                .map(role -> new RoleSimpleResponseDTO(role.getId(), role.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(roles);
    }

    // 2. Obtener solo los IDs de permisos de un rol (GET /api/v1/roles/{id}/permissions)
    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<UUID>> getRolePermissions(@PathVariable UUID id) {
        RoleEntity role = roleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        List<UUID> permissionIds = role.getFunctionalities().stream()
                .map(f -> f.getId())
                .collect(Collectors.toList());

        return ResponseEntity.ok(permissionIds);
    }

    @GetMapping("/{id}/functionalities")
    public ResponseEntity<RoleResponseDTO> getRoleFunctionalities(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleWithFunctionalities(id));
    }
}