package com.fintech.management.subproducts.mapper;

import com.fintech.management.subproducts.domain.InterestGroupEntity;
import com.fintech.management.subproducts.dto.InterestGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterestGroupMapper {

    InterestGroupDTO toDto(InterestGroupEntity entity);

    @Mapping(target = "ranges", source = "ranges")
    InterestGroupEntity toEntity(InterestGroupDTO dto);

    // MapStruct detectará automáticamente la relación entre
    // InterestRangeEntity <-> InterestRangeDTO
}

