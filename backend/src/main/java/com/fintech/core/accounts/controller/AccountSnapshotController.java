package com.fintech.core.accounts.controller;

import com.fintech.core.accounts.dto.AccountSnapshotDTO;
import com.fintech.core.accounts.mapper.AccountSnapshotMapper;
import com.fintech.core.accounts.repository.AccountSnapshotRepository;
import com.fintech.core.accounts.service.AccountSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountSnapshotController {


    private final AccountSnapshotService snapshotService;

    @GetMapping("/snapshots/{accountNumber}")
    public ResponseEntity<Page<AccountSnapshotDTO>> getSnapshots(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(snapshotService.getHistoryByAccountNumber(accountNumber, startDate, endDate, page, size));
    }
}

