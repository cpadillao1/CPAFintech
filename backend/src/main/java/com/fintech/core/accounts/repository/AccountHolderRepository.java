package com.fintech.core.accounts.repository;


import com.fintech.core.accounts.domain.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {

    // Usamos el guion bajo para ser explícitos en la navegación de la propiedad
    List<AccountHolder> findByAccount_Id(Long accountId);

    List<AccountHolder> findByCustomer_Id(Long customerId);

    // Este es vital para validar duplicados antes de insertar
    boolean existsByAccount_IdAndCustomer_IdAndOwnershipType_Id(Long accountId, Long customerId, Long ownershipTypeId);

}
