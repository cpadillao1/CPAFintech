package com.fintech.core.transactions.repository;

import com.fintech.core.transactions.domain.TrTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrTypeRepository extends JpaRepository<TrTypeEntity, Integer> {
    Optional<TrTypeEntity> findByCode(String code);
}
