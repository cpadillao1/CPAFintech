package com.fintech.management.roles.mapper;


import com.fintech.management.functionalities.domain.FunctionalityEntity;
import com.fintech.management.functionalities.domain.RoleEntity;
import com.fintech.management.functionalities.dto.FunctionalityDTO;
import com.fintech.management.functionalities.dto.RoleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Mapea la entidad de funcionalidad al DTO.
     * Importante: Sacamos el parentId del objeto 'parent' de la entidad.
     */
    @Mapping(source = "parent.id", target = "parentId")
    FunctionalityDTO toFunctionalityDto(FunctionalityEntity entity);

    /**
     * Mapea el Rol al DTO de respuesta.
     * Ignoramos el mapeo automático de 'functionalities' para construir
     * el árbol manualmente en el Service.
     */
    @Mapping(source = "id", target = "roleId")
    @Mapping(source = "name", target = "roleName")
    @Mapping(target = "functionalities", ignore = true)
    RoleResponseDTO toRoleResponseDto(RoleEntity entity);
}
