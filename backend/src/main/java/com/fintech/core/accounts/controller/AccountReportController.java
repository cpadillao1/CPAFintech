package com.fintech.core.accounts.controller;

import com.fintech.core.accounts.dto.AggregatedReportDTO;
import com.fintech.core.accounts.service.AccountReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class AccountReportController {

    private final AccountReportService reportService;

    /**
     * Obtiene el resumen consolidado de balances para la fecha indicada.
     * Si no se envía fecha, por defecto toma la fecha actual.
     */
    @GetMapping("/summary")
    public ResponseEntity<AggregatedReportDTO> getAccountSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate queryDate = (date != null) ? date : LocalDate.now();

        AggregatedReportDTO summary = reportService.getDailyReportSummary(queryDate);

        return ResponseEntity.ok(summary);
    }
}
