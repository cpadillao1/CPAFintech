package com.fintech.core.transactions.mapper;

import com.fintech.core.transactions.dto.TransactionDetailDTO;
import com.fintech.core.transactions.repository.TransactionProjection;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class StatementMapperImpl implements StatementMapper {

    @Override
    public TransactionDetailDTO toDto(TransactionProjection projection) {
        if ( projection == null ) {
            return null;
        }

        Long id = null;
        LocalDate businessDate = null;
        String description = null;
        String reference = null;
        BigDecimal amount = null;
        BigDecimal previousBalance = null;
        BigDecimal newBalance = null;
        String status = null;
        String typeLabel = null;
        String conceptName = null;
        String nameTran = null;
        String typeTran = null;

        id = projection.getId();
        businessDate = projection.getBusinessDate();
        description = projection.getDescription();
        reference = projection.getReference();
        amount = projection.getAmount();
        previousBalance = projection.getPreviousBalance();
        newBalance = projection.getNewBalance();
        status = projection.getStatus();
        typeLabel = projection.getTypeLabel();
        conceptName = projection.getConceptName();
        nameTran = projection.getNameTran();
        typeTran = projection.getTypeTran();

        TransactionDetailDTO transactionDetailDTO = new TransactionDetailDTO( id, businessDate, description, reference, amount, previousBalance, newBalance, status, typeLabel, conceptName, nameTran, typeTran );

        return transactionDetailDTO;
    }

    @Override
    public List<TransactionDetailDTO> toDtoList(List<TransactionProjection> projections) {
        if ( projections == null ) {
            return null;
        }

        List<TransactionDetailDTO> list = new ArrayList<TransactionDetailDTO>( projections.size() );
        for ( TransactionProjection transactionProjection : projections ) {
            list.add( toDto( transactionProjection ) );
        }

        return list;
    }
}
