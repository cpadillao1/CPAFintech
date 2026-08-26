package com.fintech.management.branches.repository;

import com.fintech.management.branches.domain.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Integer> {
    Optional<BranchEntity> findByCode(String code);

    // Buscamos todas las sucursales activas ordenadas por código
    List<BranchEntity> findAllByOrderByCodeAsc();
}
