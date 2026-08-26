package com.fintech.core.accounts.service;

import com.fintech.core.accounts.dto.AggregatedReportDTO;
import com.fintech.core.accounts.repository.AccountSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AccountReportService {
    private final AccountSnapshotRepository snapshotRepository;

    public AggregatedReportDTO getDailyReportSummary(LocalDate date) {
        return snapshotRepository.getAggregatedTotalsByDate(date);
    }
}
