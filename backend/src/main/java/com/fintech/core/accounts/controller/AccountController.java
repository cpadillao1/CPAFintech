package com.fintech.core.accounts.controller;

import com.fintech.core.accounts.dto.AccountCreateRequestDTO;
import com.fintech.core.accounts.dto.AccountHoldDTO;
import com.fintech.core.accounts.dto.AccountResponseDTO;
import com.fintech.core.accounts.dto.AccountSearchDTO;
import com.fintech.core.accounts.repository.AccountRepository;
import com.fintech.core.accounts.service.AccountHoldService;
import com.fintech.core.accounts.service.AccountService;
import com.fintech.core.customers.repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountHoldService holdService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountCreateRequestDTO request) {

        AccountResponseDTO response = accountService.createAccount(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountSearchDTO>> search(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer subproductId,
            @RequestParam(required = false) String accountNumber) {

        List<AccountSearchDTO> results = accountService.searchAccounts(productId, subproductId, accountNumber);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(@PathVariable String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(acc -> {
                    // 1. Buscamos el nombre del cliente
                    String fullName = customerRepository.findById(acc.getCustomerId())
                            .map(cust -> cust.getFirstName() + " " + cust.getLastName())
                            .orElse("Customer Not Found");

                    // 2. Construimos el JSON de respuesta compatible con todo
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", acc.getId());
                    response.put("accountNumber", acc.getAccountNumber());
                    response.put("customerName", fullName);
                    response.put("availableBalance", acc.getAvailableBalance());
                    response.put("status", acc.getStatusId()); // Campo real en tu entidad
                    response.put("customerId", acc.getCustomerId());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
/*
    @GetMapping("/query")
    public ResponseEntity<Page<AccountHoldDTO>> getActiveHolds(
            @RequestParam String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(holdService.getActiveHoldsByAccountNumber(accountNumber, page, size));
    }
*/
}
