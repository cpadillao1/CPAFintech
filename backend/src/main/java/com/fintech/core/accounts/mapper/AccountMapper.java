package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.core.accounts.dto.AccountCreateRequestDTO;
import com.fintech.core.accounts.dto.AccountResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    @Mapping(target = "id", ignore = true) // Lo generamos manualmente en el Service
    @Mapping(target = "availableBalance", ignore = true)
    @Mapping(target = "balanceToday", ignore = true)
    @Mapping(target = "balanceYesterday", ignore = true)
    @Mapping(target = "amountHold", ignore = true)
    @Mapping(target = "accruedInterestMonth", ignore = true)
    @Mapping(target = "amountNdToday", ignore = true)
    @Mapping(target = "amountNdYesterday", ignore = true)
    @Mapping(target = "amountNcToday", ignore = true)
    @Mapping(target = "amountNcYesterday", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "openingDate", ignore = true)
    @Mapping(target = "lastProcessedDate", ignore = true)
    @Mapping(target = "statusId", ignore = true) // Se asigna por lógica de negocio
    @Mapping(target = "generatesInterest", ignore = true) // Se hereda del subproducto
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "accountType", ignore = true)
    @Mapping(target = "subproduct", ignore = true) // Lo seteamos en el Service
    @Mapping(target = "holders", ignore = true)
    AccountEntity toEntity(AccountCreateRequestDTO dto);

    @Mapping(target = "statusName", ignore = true) // Lo mapearemos desde el catálogo si es necesario
    AccountResponseDTO toResponse(AccountEntity entity);


}
