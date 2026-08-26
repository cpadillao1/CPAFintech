package com.fintech.management.catalog.controller;

import com.fintech.management.catalog.dto.CatalogDetailDTO;
import com.fintech.management.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogs")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/details")
    public ResponseEntity<List<CatalogDetailDTO>> getDetails(@RequestParam String name) {
        return ResponseEntity.ok(catalogService.getDetailsByCatalog(name));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CatalogDetailDTO>> getAll() {
        return ResponseEntity.ok(catalogService.getAllActiveDetails());
    }
}

