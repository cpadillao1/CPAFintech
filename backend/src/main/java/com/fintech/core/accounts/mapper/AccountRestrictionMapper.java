package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountRestrictionEntity;
import com.fintech.core.accounts.dto.AccountRestrictionDTO;
import com.fintech.core.accounts.dto.AccountRestrictionRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AccountRestrictionMapper {

    // MapStruct mapeará automáticamente los campos que se llaman igual (id, accountId, etc.)
    // Los campos String (accountNumber, etc.) quedarán en null hasta que los seteemos en el Service
    AccountRestrictionDTO toDto(AccountRestrictionEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AccountRestrictionEntity toEntity(AccountRestrictionRequest request);

    default Page<AccountRestrictionDTO> toDtoPage(Page<AccountRestrictionEntity> entities) {
        return entities.map(this::toDto);
    }
}
