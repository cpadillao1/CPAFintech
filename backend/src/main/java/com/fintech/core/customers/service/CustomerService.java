package com.fintech.core.customers.service;

import com.fintech.core.customers.dto.CustomerDTO;
import java.util.UUID;
import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDto);
    CustomerDTO getCustomerById(Long id);
    List<CustomerDTO> getAllCustomers();

    CustomerDTO getByDocumentNumber(String documentNumber);
    // Podríamos agregar update y delete luego
}
