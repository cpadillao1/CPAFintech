package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.core.accounts.domain.AccountHoldEntity;
import com.fintech.core.accounts.dto.AccountHoldCreateRecord;
import com.fintech.core.accounts.dto.AccountHoldDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class AccountHoldMapperImpl implements AccountHoldMapper {

    @Override
    public AccountHoldDTO toDto(AccountHoldEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long accountId = null;
        Long id = null;
        BigDecimal amount = null;
        Long holdTypeId = null;
        String referenceNumber = null;
        String description = null;
        LocalDate startDate = null;
        LocalDate expiryDate = null;
        String createdBy = null;

        accountId = entityAccountId( entity );
        id = entity.getId();
        amount = entity.getAmount();
        holdTypeId = entity.getHoldTypeId();
        referenceNumber = entity.getReferenceNumber();
        description = entity.getDescription();
        startDate = entity.getStartDate();
        expiryDate = entity.getExpiryDate();
        createdBy = entity.getCreatedBy();

        String holdTypeDescription = null;
        String status = null;

        AccountHoldDTO accountHoldDTO = new AccountHoldDTO( id, accountId, amount, holdTypeId, holdTypeDescription, referenceNumber, description, startDate, expiryDate, createdBy, status );

        return accountHoldDTO;
    }

    @Override
    public AccountHoldEntity toEntity(AccountHoldCreateRecord record) {
        if ( record == null ) {
            return null;
        }

        AccountHoldEntity.AccountHoldEntityBuilder accountHoldEntity = AccountHoldEntity.builder();

        accountHoldEntity.amount( record.amount() );
        accountHoldEntity.holdTypeId( record.holdTypeId() );
        accountHoldEntity.referenceNumber( record.referenceNumber() );
        accountHoldEntity.description( record.description() );
        accountHoldEntity.startDate( record.startDate() );
        accountHoldEntity.expiryDate( record.expiryDate() );
        accountHoldEntity.createdBy( record.createdBy() );

        return accountHoldEntity.build();
    }

    private Long entityAccountId(AccountHoldEntity accountHoldEntity) {
        if ( accountHoldEntity == null ) {
            return null;
        }
        AccountEntity account = accountHoldEntity.getAccount();
        if ( account == null ) {
            return null;
        }
        Long id = account.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
