package com.fintech.audit.repository;

import com.fintech.audit.domain.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) // <--- ESTO ES VITAL
    <S extends AuditLogEntity> S save(S entity);
}
