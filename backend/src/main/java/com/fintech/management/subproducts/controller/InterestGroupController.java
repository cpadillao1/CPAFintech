package com.fintech.management.subproducts.controller;

import com.fintech.management.subproducts.dto.InterestGroupDTO;
import com.fintech.management.subproducts.service.InterestGroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interest-groups")
@RequiredArgsConstructor
public class InterestGroupController {

    private final InterestGroupService groupService;

    @GetMapping("/active")
    public ResponseEntity<List<InterestGroupDTO>> getActiveGroups() {
        // Este es el que llama el FE en el useEffect
        return ResponseEntity.ok(groupService.findAllActive());
    }

    @PostMapping
    public ResponseEntity<InterestGroupDTO> create(@RequestBody InterestGroupDTO dto) {
        // Llamamos al service que ya tienes implementado
        return ResponseEntity.ok(groupService.createGroup(dto));
    }


    @GetMapping("/{id}")
    public ResponseEntity<InterestGroupDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(groupService.findById(id));
    }
}

