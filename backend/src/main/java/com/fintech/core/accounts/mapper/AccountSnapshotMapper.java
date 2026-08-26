package com.fintech.core.accounts.mapper;


import com.fintech.core.accounts.domain.AccountBalanceSnapshotEntity;
import com.fintech.core.accounts.dto.AccountSnapshotDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountSnapshotMapper {

    @Mapping(source = "account.accountNumber", target = "accountNumber")
    AccountSnapshotDTO toDto(AccountBalanceSnapshotEntity entity);

    List<AccountSnapshotDTO> toDtoList(List<AccountBalanceSnapshotEntity> entities);
}
