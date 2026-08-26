package com.fintech.core.accounts.repository;

import com.fintech.core.accounts.domain.AccountSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountSequenceRepository extends JpaRepository<AccountSequence, String> {
    @Modifying
    @Query("UPDATE AccountSequence s SET s.currentValue = s.currentValue + 1 WHERE s.sequenceId = :id")
    void incrementSequence(String id);

}

