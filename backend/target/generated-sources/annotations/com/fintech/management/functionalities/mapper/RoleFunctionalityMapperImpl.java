package com.fintech.management.functionalities.mapper;

import com.fintech.management.functionalities.domain.FunctionalityEntity;
import com.fintech.management.functionalities.domain.RoleEntity;
import com.fintech.management.functionalities.dto.FunctionalityDTO;
import com.fintech.management.functionalities.dto.RoleResponseDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class RoleFunctionalityMapperImpl implements RoleFunctionalityMapper {

    @Override
    public FunctionalityDTO toFunctionalityDto(FunctionalityEntity entity) {
        if ( entity == null ) {
            return null;
        }

        FunctionalityDTO.FunctionalityDTOBuilder functionalityDTO = FunctionalityDTO.builder();

        functionalityDTO.id( entity.getId() );
        functionalityDTO.name( entity.getName() );
        functionalityDTO.code( entity.getCode() );
        functionalityDTO.label( entity.getLabel() );
        functionalityDTO.type( entity.getType() );
        functionalityDTO.icon( entity.getIcon() );
        functionalityDTO.sortOrder( entity.getSortOrder() );
        functionalityDTO.parentId( entity.getParentId() );

        return functionalityDTO.build();
    }

    @Override
    public List<FunctionalityDTO> toFunctionalityDtoList(List<FunctionalityEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<FunctionalityDTO> list = new ArrayList<FunctionalityDTO>( entities.size() );
        for ( FunctionalityEntity functionalityEntity : entities ) {
            list.add( toFunctionalityDto( functionalityEntity ) );
        }

        return list;
    }

    @Override
    public RoleResponseDTO toRoleResponseDto(RoleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RoleResponseDTO.RoleResponseDTOBuilder roleResponseDTO = RoleResponseDTO.builder();

        roleResponseDTO.roleId( entity.getId() );
        roleResponseDTO.roleName( entity.getName() );
        roleResponseDTO.functionalities( functionalityEntitySetToFunctionalityDTOList( entity.getFunctionalities() ) );
        roleResponseDTO.description( entity.getDescription() );

        return roleResponseDTO.build();
    }

    protected List<FunctionalityDTO> functionalityEntitySetToFunctionalityDTOList(Set<FunctionalityEntity> set) {
        if ( set == null ) {
            return null;
        }

        List<FunctionalityDTO> list = new ArrayList<FunctionalityDTO>( set.size() );
        for ( FunctionalityEntity functionalityEntity : set ) {
            list.add( toFunctionalityDto( functionalityEntity ) );
        }

        return list;
    }
}
