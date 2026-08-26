package com.fintech.core.customers.service;

import com.fintech.audit.aspect.Audit;
import com.fintech.core.customers.domain.CustomerEntity;
import com.fintech.core.customers.dto.CustomerAddressDTO;
import com.fintech.core.customers.dto.CustomerDTO;
import com.fintech.core.customers.dto.CustomerEmailDTO;
import com.fintech.core.customers.dto.CustomerPhoneDTO;
import com.fintech.core.customers.mapper.CustomerMapper;
import com.fintech.core.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @PreAuthorize("hasAuthority('CUST_CREATE')")
    @Audit(action = "CUST_CREATE", module = "CUSTOMERS")
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDto) {

        // 1. Validar duplicidad por número de documento
        if (customerRepository.existsByDocumentNumber(customerDto.documentNumber())) {
            throw new RuntimeException("The customer with identification " + customerDto.documentNumber() + " it already exists.");
        }

        CustomerEntity entity = customerMapper.toFullEntity(customerDto);

        CustomerEntity savedEntity = customerRepository.save(entity);
        return customerMapper.toDto(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getByDocumentNumber(String documentNumber) {
        // 1. Buscamos la entidad
        CustomerEntity entity = customerRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found with document: " + documentNumber));

        // 2. Convertimos la entidad a DTO (usando tu método de mapeo o manualmente)
        return mapToDTO(entity);
    }

    // Método helper para centralizar la conversión y evitar el "desmande" de datos
    private CustomerDTO mapToDTO(CustomerEntity entity) {
        return new CustomerDTO(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDocumentTypeId(),
                entity.getDocumentNumber(),
                entity.getBirthDate(),
                entity.getGenderId(),
                entity.getStatusId(),
                entity.getMaritalStatusId(),
                entity.getNationalityId(),
                entity.getCreatedBy(),
                // Mapeo de listas de Records
                entity.getAddresses().stream().map(a -> new CustomerAddressDTO(
                        a.getId(), a.getAddressLine(), a.getCity(), a.getState(),
                        a.getCountry(), a.getPostalCode(), a.getAddressTypeId(), a.getIsPrimary()
                )).toList(),
                entity.getPhones().stream().map(p -> new CustomerPhoneDTO(
                        p.getId(), p.getPhoneNumber(), p.getPhoneTypeId(), p.getIsPrimary()
                )).toList(),
                entity.getEmails().stream().map(e -> new CustomerEmailDTO(
                        e.getId(), e.getEmail(), e.getEmailTypeId(), e.getIsPrimary()
                )).toList()
        );
    }
}