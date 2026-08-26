package com.fintech.management.branches.controller;

import com.fintech.management.branches.dto.BranchCatalogDTO;
import com.fintech.management.branches.dto.BranchDTO;
import com.fintech.management.branches.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/save")
    public ResponseEntity<BranchDTO> create(@RequestBody BranchDTO branchDto) {
        return new ResponseEntity<>(branchService.createBranch(branchDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<BranchDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(branchService.getAllBranches(page, size));
    }

    @GetMapping("/{code}")
    public ResponseEntity<BranchDTO> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(branchService.getBranchByCode(code));
    }

    @PutMapping("/{code}")
    public ResponseEntity<BranchDTO> update(@PathVariable String code, @RequestBody BranchDTO branchDto) {
        // Es una buena práctica asegurar que el código de la URL sea el mismo del DTO
        return ResponseEntity.ok(branchService.updateBranch(code, branchDto));
    }

    @GetMapping("/branches")
    public ResponseEntity<List<BranchCatalogDTO>> getAllBranches() {
        List<BranchCatalogDTO> branches = branchService.findAllBranchesCatalog();

        // Si la lista está vacía, devolvemos 204 No Content, o 200 con lista vacía
        if (branches.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(branches);
    }
}