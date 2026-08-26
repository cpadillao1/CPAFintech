package com.fintech.management.products.service;


import com.fintech.audit.aspect.Audit;
import com.fintech.management.products.domain.ProductEntity;
import com.fintech.management.products.dto.ProductDTO;
import com.fintech.management.products.mapper.ProductMapper;
import com.fintech.management.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @PreAuthorize("hasAuthority('PROD_CREATE')")
    @Audit(action = "PROD_CREATE", module = "PRODUCTS")
    @Transactional
    public ProductDTO create(ProductDTO dto) {
        ProductEntity entity = productMapper.toEntity(dto);
        ProductEntity saved = productRepository.saveAndFlush(entity);
        return productMapper.toDto(saved);
    }

    @PreAuthorize("hasAuthority('PROD_QUERY')")
    @Audit(action = "PROD_QUERY", module = "PRODUCTS")
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .toList();
    }
}