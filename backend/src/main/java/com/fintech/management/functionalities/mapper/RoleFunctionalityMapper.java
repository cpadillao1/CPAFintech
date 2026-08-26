package com.fintech.management.functionalities.mapper;

import com.fintech.management.functionalities.domain.FunctionalityEntity;
import com.fintech.management.functionalities.domain.RoleEntity;
import com.fintech.management.functionalities.dto.FunctionalityDTO;
import com.fintech.management.functionalities.dto.RoleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleFunctionalityMapper {

    // Mapea una sola funcionalidad
    FunctionalityDTO toFunctionalityDto(FunctionalityEntity entity);

    // Mapea la lista completa
    List<FunctionalityDTO> toFunctionalityDtoList(List<FunctionalityEntity> entities);

    // Mapea el Rol completo al DTO de respuesta
    @Mapping(source = "id", target = "roleId")
    @Mapping(source = "name", target = "roleName")
    @Mapping(source = "functionalities", target = "functionalities")
    RoleResponseDTO toRoleResponseDto(RoleEntity entity);
}
