package com.fintech.core.transactions.controller;


import com.fintech.core.transactions.domain.TrTypeEntity;
import com.fintech.core.transactions.dto.TrConfigResponseDTO;
import com.fintech.core.transactions.repository.TrConfigRepository;
import com.fintech.core.transactions.repository.TrTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction-configs")
@RequiredArgsConstructor
public class TrConfigController {

    private final TrConfigRepository trConfigRepository;
    private final TrTypeRepository trTypeRepository;

    /**
     * Obtiene todos los tipos de transacción (Débito/Crédito)
     * Útil si prefieres usar esta tabla en lugar del catálogo general
     */
    @GetMapping("/types")
    public ResponseEntity<List<TrTypeEntity>> getAllTypes() {
        return ResponseEntity.ok(trTypeRepository.findAll());
    }

    /**
     * Obtiene las configuraciones (Motivos) filtradas por el ID del tipo.
     * Este es el que usa el segundo combo de tu pantalla NcNdCreate.jsx
     */
    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<TrConfigResponseDTO>> getConfigsByTypeId(@PathVariable Long typeId) {
        List<TrConfigResponseDTO> configs = trConfigRepository.findActiveConfigsByTypeId(typeId);
        return ResponseEntity.ok(configs);
    }
}
