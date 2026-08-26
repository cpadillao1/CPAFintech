package com.fintech.core.customers.controller;

import com.fintech.core.customers.domain.CustomerEntity;
import com.fintech.core.customers.dto.CustomerDTO;
import com.fintech.core.customers.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerDTO customerDto) {
        return new ResponseEntity<>(customerService.createCustomer(customerDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAll() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/document/{docNumber}")
    public ResponseEntity<CustomerDTO> getByDocument(@PathVariable String docNumber) {
        // Ahora el servicio devuelve un DTO, y el ResponseEntity espera un DTO.
        return ResponseEntity.ok(customerService.getByDocumentNumber(docNumber));
    }
}