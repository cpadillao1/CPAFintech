package com.fintech.core.accounts.controller;

import com.fintech.core.accounts.dto.AccountHoldCreateRecord;
import com.fintech.core.accounts.dto.AccountHoldDTO;
import com.fintech.core.accounts.dto.AccountHoldReleaseRecord;
import com.fintech.core.accounts.service.AccountHoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts/holds")
@RequiredArgsConstructor
public class AccountHoldController {

    private final AccountHoldService holdService;

    @PostMapping
    public ResponseEntity<AccountHoldDTO> createHold(
            @Valid @RequestBody AccountHoldCreateRecord record) {
        // Pasamos el usuario que viene dentro del record
        AccountHoldDTO response = holdService.createHold(record, record.createdBy());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{holdId}/release")
    public ResponseEntity<Void> releaseHold(
            @PathVariable Long holdId,
            @Valid @RequestBody AccountHoldReleaseRecord releaseRequest) {

        // Pasamos el usuario que viene dentro del record de liberación
        holdService.releaseHoldManual(
                holdId,
                releaseRequest.observations(),
                releaseRequest.releasedBy()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/query")
    public ResponseEntity<Page<AccountHoldDTO>> getActiveHolds(
            @RequestParam String accountNumber,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable
    ){
            //@RequestParam(defaultValue = "0") int page,
            //@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(holdService.getActiveHoldsByAccountNumber(accountNumber, pageable));
    }


}
