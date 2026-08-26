package com.fintech.core.transactions.controller;


import com.fintech.core.transactions.dto.TransactionDetailDTO;
import com.fintech.core.transactions.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;

    /**
     * Obtiene el historial unificado (Log + Histórico) de una cuenta específica.
     * URL: GET /api/v1/accounts/{accountId}/statement
     */
    @GetMapping("/{accountId}/statement")
    public ResponseEntity<Page<TransactionDetailDTO>> getStatement(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @PageableDefault(size = 10, sort = "businessDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(statementService.getAccountStatement(accountId, startDate, endDate, pageable));
    }
}
