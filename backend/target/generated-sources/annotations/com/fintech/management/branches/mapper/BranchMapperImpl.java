package com.fintech.management.branches.mapper;

import com.fintech.management.branches.domain.BranchEntity;
import com.fintech.management.branches.dto.BranchDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class BranchMapperImpl implements BranchMapper {

    @Override
    public BranchDTO toDto(BranchEntity entity) {
        if ( entity == null ) {
            return null;
        }

        BranchDTO branchDTO = new BranchDTO();

        branchDTO.setCode( entity.getCode() );
        branchDTO.setName( entity.getName() );
        branchDTO.setAddress( entity.getAddress() );
        branchDTO.setCity( entity.getCity() );
        branchDTO.setCountry( entity.getCountry() );
        branchDTO.setCreatedAt( entity.getCreatedAt() );

        return branchDTO;
    }

    @Override
    public BranchEntity toEntity(BranchDTO dto) {
        if ( dto == null ) {
            return null;
        }

        BranchEntity branchEntity = new BranchEntity();

        branchEntity.setCode( dto.getCode() );
        branchEntity.setName( dto.getName() );
        branchEntity.setAddress( dto.getAddress() );
        branchEntity.setCity( dto.getCity() );
        branchEntity.setCountry( dto.getCountry() );
        branchEntity.setCreatedAt( dto.getCreatedAt() );

        return branchEntity;
    }
}
