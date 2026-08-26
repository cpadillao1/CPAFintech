package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.core.accounts.dto.AccountCreateRequestDTO;
import com.fintech.core.accounts.dto.AccountResponseDTO;
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
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountEntity toEntity(AccountCreateRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AccountEntity.AccountEntityBuilder accountEntity = AccountEntity.builder();

        accountEntity.customerId( dto.customerId() );
        accountEntity.branchId( dto.branchId() );
        accountEntity.currencyId( dto.currencyId() );
        accountEntity.ownershipTypeId( dto.ownershipTypeId() );
        accountEntity.createdBy( dto.createdBy() );

        return accountEntity.build();
    }

    @Override
    public AccountResponseDTO toResponse(AccountEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        String accountNumber = null;
        String accountType = null;
        BigDecimal availableBalance = null;
        BigDecimal balanceToday = null;
        LocalDate lastProcessedDate = null;
        Boolean generatesInterest = null;

        id = entity.getId();
        accountNumber = entity.getAccountNumber();
        accountType = entity.getAccountType();
        availableBalance = entity.getAvailableBalance();
        balanceToday = entity.getBalanceToday();
        lastProcessedDate = entity.getLastProcessedDate();
        generatesInterest = entity.getGeneratesInterest();

        String statusName = null;

        AccountResponseDTO accountResponseDTO = new AccountResponseDTO( id, accountNumber, accountType, availableBalance, balanceToday, lastProcessedDate, statusName, generatesInterest );

        return accountResponseDTO;
    }
}
