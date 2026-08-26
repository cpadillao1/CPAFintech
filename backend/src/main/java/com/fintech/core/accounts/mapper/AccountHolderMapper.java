package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountHolder;
import com.fintech.core.accounts.dto.AccountCreateRequestDTO;
import com.fintech.core.accounts.dto.AccountHolderResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountHolderMapper {

    @Mapping(target = "id", ignore = true) // Se genera en el Service
    @Mapping(target = "account", ignore = true) // Se asigna manualmente al tener el ID de la cuenta
    @Mapping(target = "statusId", ignore = true) // Se asigna por lógica de negocio (ACTIVO)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "customer", ignore = true)      // Lo seteamos manualmente en el Service
    @Mapping(target = "ownershipType", ignore = true)
    AccountHolder toEntity(AccountCreateRequestDTO.AccountHolderRequestDTO dto);

    @Mapping(target = "holderTypeName", source = "ownershipType.name")
    AccountHolderResponseDTO toResponse(AccountHolder entity);
}
