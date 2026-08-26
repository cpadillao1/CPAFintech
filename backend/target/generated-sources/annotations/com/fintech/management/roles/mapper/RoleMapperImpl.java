package com.fintech.management.roles.mapper;

import com.fintech.management.functionalities.domain.FunctionalityEntity;
import com.fintech.management.functionalities.domain.RoleEntity;
import com.fintech.management.functionalities.dto.FunctionalityDTO;
import com.fintech.management.functionalities.dto.RoleResponseDTO;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public FunctionalityDTO toFunctionalityDto(FunctionalityEntity entity) {
        if ( entity == null ) {
            return null;
        }

        FunctionalityDTO.FunctionalityDTOBuilder functionalityDTO = FunctionalityDTO.builder();

        functionalityDTO.parentId( entityParentId( entity ) );
        functionalityDTO.id( entity.getId() );
        functionalityDTO.name( entity.getName() );
        functionalityDTO.code( entity.getCode() );
        functionalityDTO.label( entity.getLabel() );
        functionalityDTO.type( entity.getType() );
        functionalityDTO.icon( entity.getIcon() );
        functionalityDTO.sortOrder( entity.getSortOrder() );

        return functionalityDTO.build();
    }

    @Override
    public RoleResponseDTO toRoleResponseDto(RoleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RoleResponseDTO.RoleResponseDTOBuilder roleResponseDTO = RoleResponseDTO.builder();

        roleResponseDTO.roleId( entity.getId() );
        roleResponseDTO.roleName( entity.getName() );
        roleResponseDTO.description( entity.getDescription() );

        return roleResponseDTO.build();
    }

    private UUID entityParentId(FunctionalityEntity functionalityEntity) {
        if ( functionalityEntity == null ) {
            return null;
        }
        FunctionalityEntity parent = functionalityEntity.getParent();
        if ( parent == null ) {
            return null;
        }
        UUID id = parent.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
