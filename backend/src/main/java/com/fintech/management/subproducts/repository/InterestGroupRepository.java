package com.fintech.management.subproducts.repository;

import com.fintech.management.subproducts.domain.InterestGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InterestGroupRepository extends JpaRepository<InterestGroupEntity, Integer> {
    List<InterestGroupEntity> findByStatus(String status);
}

