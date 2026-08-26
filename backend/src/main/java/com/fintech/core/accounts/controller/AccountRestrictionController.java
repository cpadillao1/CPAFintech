package com.fintech.core.accounts.controller;

import com.fintech.core.accounts.dto.AccountRestrictionDTO;
import com.fintech.core.accounts.dto.AccountRestrictionReleaseRequest;
import com.fintech.core.accounts.dto.AccountRestrictionRequest;
import com.fintech.core.accounts.service.AccountRestrictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts/restrictions")
@RequiredArgsConstructor
public class AccountRestrictionController {

    private final AccountRestrictionService restrictionService;

    @PostMapping
    public ResponseEntity<AccountRestrictionDTO> create(@RequestBody AccountRestrictionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restrictionService.createRestriction(request));
    }


    @GetMapping("/active/{accountNumber}")
    public ResponseEntity<List<AccountRestrictionDTO>> getActive(@PathVariable String accountNumber,
                                                                 @RequestParam Long statusActiveId) {
        return ResponseEntity.ok(restrictionService.getActiveByAccountNumber(accountNumber, statusActiveId));
    }


    @GetMapping("/status/{statusId}")
    public ResponseEntity<Page<AccountRestrictionDTO>> getByStatus(
            @PathVariable Long statusId,
            Pageable pageable) { // Spring rellena automáticamente page, size y sort
        return ResponseEntity.ok(restrictionService.getByStatus(statusId, pageable));
    }


    @PatchMapping("/{id}/release")
    public ResponseEntity<AccountRestrictionDTO> release(
            @PathVariable Long id,
            @Valid @RequestBody AccountRestrictionReleaseRequest request) {

        return ResponseEntity.ok(
                restrictionService.releaseRestriction(
                        id,
                        request.observations(),
                        request.statusReleasedId(),
                        request.releaseAuthorizer() // <--- Se envía al service
                )
        );
    }

}
