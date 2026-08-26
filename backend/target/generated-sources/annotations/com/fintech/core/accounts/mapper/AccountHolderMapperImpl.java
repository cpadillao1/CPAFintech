package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountHolder;
import com.fintech.core.accounts.dto.AccountCreateRequestDTO;
import com.fintech.core.accounts.dto.AccountHolderResponseDTO;
import com.fintech.management.catalog.domain.CatalogDetail;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class AccountHolderMapperImpl implements AccountHolderMapper {

    @Override
    public AccountHolder toEntity(AccountCreateRequestDTO.AccountHolderRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AccountHolder.AccountHolderBuilder accountHolder = AccountHolder.builder();

        return accountHolder.build();
    }

    @Override
    public AccountHolderResponseDTO toResponse(AccountHolder entity) {
        if ( entity == null ) {
            return null;
        }

        String holderTypeName = null;
        Long id = null;
        Long statusId = null;

        holderTypeName = entityOwnershipTypeName( entity );
        id = entity.getId();
        statusId = entity.getStatusId();

        Long customerId = null;
        Long holderTypeId = null;
        String ownershipTypeCode = null;

        AccountHolderResponseDTO accountHolderResponseDTO = new AccountHolderResponseDTO( id, customerId, holderTypeId, holderTypeName, statusId, ownershipTypeCode );

        return accountHolderResponseDTO;
    }

    private String entityOwnershipTypeName(AccountHolder accountHolder) {
        if ( accountHolder == null ) {
            return null;
        }
        CatalogDetail ownershipType = accountHolder.getOwnershipType();
        if ( ownershipType == null ) {
            return null;
        }
        String name = ownershipType.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
