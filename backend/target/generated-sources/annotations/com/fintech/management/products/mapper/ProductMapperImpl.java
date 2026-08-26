package com.fintech.management.products.mapper;

import com.fintech.management.products.domain.ProductEntity;
import com.fintech.management.products.dto.ProductDTO;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:56-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO toDto(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Integer id = null;
        String code = null;
        String name = null;
        String status = null;
        LocalDateTime createdAt = null;

        id = entity.getId();
        code = entity.getCode();
        name = entity.getName();
        status = entity.getStatus();
        createdAt = entity.getCreatedAt();

        ProductDTO productDTO = new ProductDTO( id, code, name, status, createdAt );

        return productDTO;
    }

    @Override
    public ProductEntity toEntity(ProductDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ProductEntity.ProductEntityBuilder productEntity = ProductEntity.builder();

        productEntity.id( dto.id() );
        productEntity.code( dto.code() );
        productEntity.name( dto.name() );
        productEntity.status( dto.status() );
        productEntity.createdAt( dto.createdAt() );

        return productEntity.build();
    }
}
