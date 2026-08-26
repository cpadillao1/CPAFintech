package com.fintech.management.subproducts.mapper;

import com.fintech.management.subproducts.domain.InterestGroupEntity;
import com.fintech.management.subproducts.domain.InterestRangeEntity;
import com.fintech.management.subproducts.dto.InterestGroupDTO;
import com.fintech.management.subproducts.dto.InterestRangeDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class InterestGroupMapperImpl implements InterestGroupMapper {

    @Override
    public InterestGroupDTO toDto(InterestGroupEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Integer id = null;
        String code = null;
        String name = null;
        String description = null;
        String status = null;
        List<InterestRangeDTO> ranges = null;

        id = entity.getId();
        code = entity.getCode();
        name = entity.getName();
        description = entity.getDescription();
        status = entity.getStatus();
        ranges = interestRangeEntityListToInterestRangeDTOList( entity.getRanges() );

        InterestGroupDTO interestGroupDTO = new InterestGroupDTO( id, code, name, description, status, ranges );

        return interestGroupDTO;
    }

    @Override
    public InterestGroupEntity toEntity(InterestGroupDTO dto) {
        if ( dto == null ) {
            return null;
        }

        InterestGroupEntity interestGroupEntity = new InterestGroupEntity();

        interestGroupEntity.setRanges( interestRangeDTOListToInterestRangeEntityList( dto.ranges() ) );
        interestGroupEntity.setId( dto.id() );
        interestGroupEntity.setCode( dto.code() );
        interestGroupEntity.setName( dto.name() );
        interestGroupEntity.setDescription( dto.description() );
        interestGroupEntity.setStatus( dto.status() );

        return interestGroupEntity;
    }

    protected InterestRangeDTO interestRangeEntityToInterestRangeDTO(InterestRangeEntity interestRangeEntity) {
        if ( interestRangeEntity == null ) {
            return null;
        }

        Integer id = null;
        String rangeName = null;
        BigDecimal rateValue = null;
        String rateType = null;
        BigDecimal minAmount = null;
        BigDecimal maxAmount = null;
        String status = null;

        id = interestRangeEntity.getId();
        rangeName = interestRangeEntity.getRangeName();
        rateValue = interestRangeEntity.getRateValue();
        rateType = interestRangeEntity.getRateType();
        minAmount = interestRangeEntity.getMinAmount();
        maxAmount = interestRangeEntity.getMaxAmount();
        status = interestRangeEntity.getStatus();

        InterestRangeDTO interestRangeDTO = new InterestRangeDTO( id, rangeName, rateValue, rateType, minAmount, maxAmount, status );

        return interestRangeDTO;
    }

    protected List<InterestRangeDTO> interestRangeEntityListToInterestRangeDTOList(List<InterestRangeEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<InterestRangeDTO> list1 = new ArrayList<InterestRangeDTO>( list.size() );
        for ( InterestRangeEntity interestRangeEntity : list ) {
            list1.add( interestRangeEntityToInterestRangeDTO( interestRangeEntity ) );
        }

        return list1;
    }

    protected InterestRangeEntity interestRangeDTOToInterestRangeEntity(InterestRangeDTO interestRangeDTO) {
        if ( interestRangeDTO == null ) {
            return null;
        }

        InterestRangeEntity interestRangeEntity = new InterestRangeEntity();

        interestRangeEntity.setId( interestRangeDTO.id() );
        interestRangeEntity.setRangeName( interestRangeDTO.rangeName() );
        interestRangeEntity.setRateValue( interestRangeDTO.rateValue() );
        interestRangeEntity.setRateType( interestRangeDTO.rateType() );
        interestRangeEntity.setMinAmount( interestRangeDTO.minAmount() );
        interestRangeEntity.setMaxAmount( interestRangeDTO.maxAmount() );
        interestRangeEntity.setStatus( interestRangeDTO.status() );

        return interestRangeEntity;
    }

    protected List<InterestRangeEntity> interestRangeDTOListToInterestRangeEntityList(List<InterestRangeDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<InterestRangeEntity> list1 = new ArrayList<InterestRangeEntity>( list.size() );
        for ( InterestRangeDTO interestRangeDTO : list ) {
            list1.add( interestRangeDTOToInterestRangeEntity( interestRangeDTO ) );
        }

        return list1;
    }
}
