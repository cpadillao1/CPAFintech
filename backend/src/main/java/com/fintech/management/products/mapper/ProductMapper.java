package com.fintech.management.products.mapper;

import com.fintech.management.products.domain.ProductEntity;
import com.fintech.management.products.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDto(ProductEntity entity);
    ProductEntity toEntity(ProductDTO dto);
}
