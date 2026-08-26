package com.fintech.core.transactions.mapper;


import com.fintech.core.transactions.dto.TransactionDetailDTO;
import com.fintech.core.transactions.repository.TransactionProjection;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StatementMapper {

    TransactionDetailDTO toDto(TransactionProjection projection);

    List<TransactionDetailDTO> toDtoList(List<TransactionProjection> projections);
}

