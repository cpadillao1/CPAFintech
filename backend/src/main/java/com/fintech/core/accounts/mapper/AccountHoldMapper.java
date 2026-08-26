package com.fintech.core.accounts.mapper;


import com.fintech.core.accounts.domain.AccountHoldEntity;
import com.fintech.core.accounts.dto.AccountHoldCreateRecord;
import com.fintech.core.accounts.dto.AccountHoldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountHoldMapper {

    // Para convertir de Entidad a DTO (Lo que enviamos al Frontend)
    @Mapping(source = "account.id", target = "accountId")
    AccountHoldDTO toDto(AccountHoldEntity entity);

    // Para convertir de DTO de Creación a Entidad (Lo que recibimos del API)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true) // Lo buscaremos en el Service por ID
    @Mapping(target = "statusId", ignore = true) // El Service lo pondrá como 'ACTIVE'
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    AccountHoldEntity toEntity(AccountHoldCreateRecord record);
}

