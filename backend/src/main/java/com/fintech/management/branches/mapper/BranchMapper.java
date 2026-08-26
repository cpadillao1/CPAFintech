package com.fintech.management.branches.mapper;

import com.fintech.management.branches.domain.BranchEntity;
import com.fintech.management.branches.dto.BranchDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    BranchDTO toDto(BranchEntity entity);

    BranchEntity toEntity(BranchDTO dto);
}
