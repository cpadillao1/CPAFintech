package com.fintech.core.transactions.repository;

import com.fintech.core.transactions.domain.TrConfigEntity;
import com.fintech.core.transactions.dto.TrConfigResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrConfigRepository extends JpaRepository<TrConfigEntity, Long> {

    @Query("SELECT new com.fintech.core.transactions.dto.TrConfigResponseDTO(c.configId, c.mnemonic, r.name) " +
            "FROM TrConfigEntity c " +
            "JOIN c.reason r " +
            "WHERE c.type.typeId = :typeId AND c.isActive = true")
    List<TrConfigResponseDTO> findActiveConfigsByTypeId(@Param("typeId") Long typeId);

    // Spring genera automáticamente el SQL: SELECT * FROM tr_configs WHERE mnemonic = ?
    Optional<TrConfigEntity> findByMnemonic(String mnemonic);

}
