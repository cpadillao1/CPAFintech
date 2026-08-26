package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountBalanceSnapshotEntity;
import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.core.accounts.dto.AccountSnapshotDTO;
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
public class AccountSnapshotMapperImpl implements AccountSnapshotMapper {

    @Override
    public AccountSnapshotDTO toDto(AccountBalanceSnapshotEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AccountSnapshotDTO.AccountSnapshotDTOBuilder accountSnapshotDTO = AccountSnapshotDTO.builder();

        accountSnapshotDTO.accountNumber( entityAccountAccountNumber( entity ) );
        accountSnapshotDTO.snapshotDate( entity.getSnapshotDate() );
        accountSnapshotDTO.availableBalance( entity.getAvailableBalance() );
        accountSnapshotDTO.balanceToday( entity.getBalanceToday() );
        accountSnapshotDTO.appliedRate( entity.getAppliedRate() );
        accountSnapshotDTO.interestDay( entity.getInterestDay() );
        accountSnapshotDTO.remainderBefore( entity.getRemainderBefore() );
        accountSnapshotDTO.remainderAfter( entity.getRemainderAfter() );
        accountSnapshotDTO.grossInterest( entity.getGrossInterest() );
        accountSnapshotDTO.accruedMonthToDate( entity.getAccruedMonthToDate() );
        accountSnapshotDTO.amountHold( entity.getAmountHold() );
        accountSnapshotDTO.totalNdDay( entity.getTotalNdDay() );
        accountSnapshotDTO.totalNcDay( entity.getTotalNcDay() );

        return accountSnapshotDTO.build();
    }

    @Override
    public List<AccountSnapshotDTO> toDtoList(List<AccountBalanceSnapshotEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<AccountSnapshotDTO> list = new ArrayList<AccountSnapshotDTO>( entities.size() );
        for ( AccountBalanceSnapshotEntity accountBalanceSnapshotEntity : entities ) {
            list.add( toDto( accountBalanceSnapshotEntity ) );
        }

        return list;
    }

    private String entityAccountAccountNumber(AccountBalanceSnapshotEntity accountBalanceSnapshotEntity) {
        if ( accountBalanceSnapshotEntity == null ) {
            return null;
        }
        AccountEntity account = accountBalanceSnapshotEntity.getAccount();
        if ( account == null ) {
            return null;
        }
        String accountNumber = account.getAccountNumber();
        if ( accountNumber == null ) {
            return null;
        }
        return accountNumber;
    }
}
