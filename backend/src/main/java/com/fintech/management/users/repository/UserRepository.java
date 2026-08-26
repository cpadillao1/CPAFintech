package com.fintech.management.users.repository;

import com.fintech.management.users.domain.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = {"branch", "roles", "roles.functionalities"})
    Optional<UserEntity> findByLogin(String login);

    // New method: Para validar que el usuario pertenece a la sucursal seleccionada
    @EntityGraph(attributePaths = {"branch", "roles", "roles.functionalities"})
    Optional<UserEntity> findByLoginAndBranchIdAndActiveTrue(String login, Integer branchId);

    // method for checking whether an email address already exists before registering
    boolean existsByEmail(String email);

    // method for checking whether a login already exists before registering
    boolean existsByLogin(String login);

    //@EntityGraph(attributePaths = {"branch", "roles", "roles.functionalities"})
    //Optional<UserEntity> findByLoginAndActiveTrue(String login);
}