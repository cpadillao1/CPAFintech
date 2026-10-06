package com.fintech.management.users.repository;


import com.fintech.management.functionalities.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    // Metodo clave para buscar el rol por su nombre (ej: "ADMIN", "USER")
    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);

    /*
    @EntityGraph(attributePaths = {"functionalities"})
    Optional<RoleEntity> findById(UUID id);
    */

}
