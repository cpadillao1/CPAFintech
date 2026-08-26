package com.fintech.core.accounts.mapper;

import com.fintech.core.accounts.domain.AccountRestrictionEntity;
import com.fintech.core.accounts.dto.AccountRestrictionDTO;
import com.fintech.core.accounts.dto.AccountRestrictionRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-04T14:33:57-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class AccountRestrictionMapperImpl implements AccountRestrictionMapper {

    @Override
    public AccountRestrictionDTO toDto(AccountRestrictionEntity entity) {
        if ( entity == null ) {
            return null;
        }

        AccountRestrictionDTO.AccountRestrictionDTOBuilder accountRestrictionDTO = AccountRestrictionDTO.builder();

        accountRestrictionDTO.id( entity.getId() );
        accountRestrictionDTO.accountId( entity.getAccountId() );
        accountRestrictionDTO.restrictionTypeCatId( entity.getRestrictionTypeCatId() );
        accountRestrictionDTO.reasonCodeCatId( entity.getReasonCodeCatId() );
        accountRestrictionDTO.statusCatId( entity.getStatusCatId() );
        accountRestrictionDTO.startDate( entity.getStartDate() );
        accountRestrictionDTO.endDate( entity.getEndDate() );
        accountRestrictionDTO.authorizer( entity.getAuthorizer() );
        accountRestrictionDTO.observations( entity.getObservations() );
        accountRestrictionDTO.createdAt( entity.getCreatedAt() );
        accountRestrictionDTO.releaseAuthorizer( entity.getReleaseAuthorizer() );

        return accountRestrictionDTO.build();
    }

    @Override
    public AccountRestrictionEntity toEntity(AccountRestrictionRequest request) {
        if ( request == null ) {
            return null;
        }

        AccountRestrictionEntity.AccountRestrictionEntityBuilder accountRestrictionEntity = AccountRestrictionEntity.builder();

        accountRestrictionEntity.restrictionTypeCatId( request.restrictionTypeCatId() );
        accountRestrictionEntity.reasonCodeCatId( request.reasonCodeCatId() );
        accountRestrictionEntity.statusCatId( request.statusCatId() );
        accountRestrictionEntity.startDate( request.startDate() );
        accountRestrictionEntity.authorizer( request.authorizer() );
        accountRestrictionEntity.observations( request.observations() );

        return accountRestrictionEntity.build();
    }
}
