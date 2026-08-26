package com.fintech.management.subproducts.mapper;

import com.fintech.management.subproducts.domain.SubproductEntity;
import com.fintech.management.subproducts.dto.SubproductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {InterestGroupMapper.class})
public interface SubproductMapper {

    // Al convertir a DTO, extraemos el ID del grupo para el Frontend
    @Mapping(target = "interestGroupId", source = "interestGroup.id")
    @Mapping(target = "productName", source = "product.name")
    SubproductDTO toDto(SubproductEntity entity);

    // Al convertir a Entidad (para guardar), ignoramos el objeto completo
    // porque el Service se encargará de buscar el InterestGroup por su ID
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "interestGroup", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SubproductEntity toEntity(SubproductDTO dto);
}

