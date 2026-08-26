package com.fintech.management.subproducts.controller;

import com.fintech.management.subproducts.dto.SubproductDTO;
import com.fintech.management.subproducts.service.SubproductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/management/subproducts")
@RequiredArgsConstructor
public class SubproductController {

    private final SubproductService subproductService;

    @PostMapping("/product/{productId}")
    public ResponseEntity<SubproductDTO> create(
            @PathVariable Integer productId,
            @Valid @RequestBody SubproductDTO dto) { // <--- Agregado @Valid
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subproductService.create(productId, dto));
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SubproductDTO>> getByProductId(@PathVariable Integer productId) {
        List<SubproductDTO> subproducts = subproductService.getSubproductsByProductId(productId);

        if (subproducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(subproducts);
    }

    @GetMapping
    public ResponseEntity<List<SubproductDTO>> getAll() {
        return ResponseEntity.ok(subproductService.findAll());
    }

}