package com.fintech.management.subproducts.mapper;

import com.fintech.management.products.domain.ProductEntity;
import com.fintech.management.subproducts.domain.InterestGroupEntity;
import com.fintech.management.subproducts.domain.SubproductEntity;
import com.fintech.management.subproducts.dto.SubproductDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class SubproductMapperImpl implements SubproductMapper {

    @Override
    public SubproductDTO toDto(SubproductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Integer interestGroupId = null;
        String productName = null;
        Integer id = null;
        String code = null;
        String name = null;
        String description = null;
        boolean generatesInterest = false;
        String statementFrequency = null;
        String status = null;
        String accountSequenceId = null;

        interestGroupId = entityInterestGroupId( entity );
        productName = entityProductName( entity );
        id = entity.getId();
        code = entity.getCode();
        name = entity.getName();
        description = entity.getDescription();
        if ( entity.getGeneratesInterest() != null ) {
            generatesInterest = entity.getGeneratesInterest();
        }
        statementFrequency = entity.getStatementFrequency();
        status = entity.getStatus();
        accountSequenceId = entity.getAccountSequenceId();

        Integer productId = null;

        SubproductDTO subproductDTO = new SubproductDTO( id, productId, productName, interestGroupId, code, name, description, generatesInterest, statementFrequency, status, accountSequenceId );

        return subproductDTO;
    }

    @Override
    public SubproductEntity toEntity(SubproductDTO dto) {
        if ( dto == null ) {
            return null;
        }

        SubproductEntity.SubproductEntityBuilder subproductEntity = SubproductEntity.builder();

        subproductEntity.id( dto.id() );
        subproductEntity.code( dto.code() );
        subproductEntity.name( dto.name() );
        subproductEntity.description( dto.description() );
        subproductEntity.status( dto.status() );
        subproductEntity.generatesInterest( dto.generatesInterest() );
        subproductEntity.statementFrequency( dto.statementFrequency() );
        subproductEntity.accountSequenceId( dto.accountSequenceId() );
        subproductEntity.interestGroupId( dto.interestGroupId() );

        return subproductEntity.build();
    }

    private Integer entityInterestGroupId(SubproductEntity subproductEntity) {
        if ( subproductEntity == null ) {
            return null;
        }
        InterestGroupEntity interestGroup = subproductEntity.getInterestGroup();
        if ( interestGroup == null ) {
            return null;
        }
        Integer id = interestGroup.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityProductName(SubproductEntity subproductEntity) {
        if ( subproductEntity == null ) {
            return null;
        }
        ProductEntity product = subproductEntity.getProduct();
        if ( product == null ) {
            return null;
        }
        String name = product.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
