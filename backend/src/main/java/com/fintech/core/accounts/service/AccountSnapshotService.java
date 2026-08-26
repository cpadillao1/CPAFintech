package com.fintech.core.accounts.service;


import com.fintech.core.accounts.dto.AccountSnapshotDTO;
import com.fintech.core.accounts.mapper.AccountSnapshotMapper;
import com.fintech.core.accounts.repository.AccountSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor // Genera el constructor para la inyección de dependencias (Lombok)
public class AccountSnapshotService {

    private final AccountSnapshotRepository snapshotRepository;
    private final AccountSnapshotMapper snapshotMapper;

    @Transactional(readOnly = true)
    public Page<AccountSnapshotDTO> getHistoryByAccountNumber(
            String accountNumber,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        var pageable = PageRequest.of(page, size);

        var entitiesPage = snapshotRepository
                .findByAccount_AccountNumberAndSnapshotDateBetweenOrderBySnapshotDateAsc(
                        accountNumber, startDate, endDate, pageable);

        // MapStruct y Spring Data JPA permiten mapear páginas así:
        return entitiesPage.map(snapshotMapper::toDto);
    }
}
