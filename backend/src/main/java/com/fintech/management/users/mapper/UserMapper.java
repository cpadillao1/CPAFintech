package com.fintech.management.users.mapper;

import com.fintech.management.branches.mapper.BranchMapper;
import com.fintech.management.users.domain.UserEntity;
import com.fintech.management.users.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {BranchMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE // Evita warnings por campos de auditoría
)
public interface UserMapper {

    // Al pasar a DTO, extraemos el ID de la entidad branch para el campo branchId del DTO
    @Mapping(target = "branchId", source = "branch.id")
    UserDTO toDto(UserEntity userEntity);

    // Al guardar, ignoramos el password (se maneja con BCrypt en el Service)
    // Y mapeamos el branchId del DTO hacia el objeto branch de la entidad
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "branch.id", source = "branchId")
    UserEntity toEntity(UserDTO userDto);
}
