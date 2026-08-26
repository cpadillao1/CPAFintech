package com.fintech.management.menu.controller;

import com.fintech.management.menu.dto.MenuResponseDTO;
import com.fintech.management.menu.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    // Inyección por constructor
    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Devuelve el árbol de menú ordenado y jerárquico.
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('FUNC_QUERY')")
    public ResponseEntity<List<MenuResponseDTO>> getMenuTree() {
        List<MenuResponseDTO> tree = menuService.getFullMenuTree();
        return ResponseEntity.ok(tree);
    }
}
