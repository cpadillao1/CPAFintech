package com.fintech.management.control_system.repository;

import com.fintech.management.control_system.domain.ControlSystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ControlSystemRepository extends JpaRepository<ControlSystemEntity, Integer> {

    Optional<ControlSystemEntity> findByStatus(String status);
}

