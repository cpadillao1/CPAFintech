package com.fintech.management.menu.repository;

import com.fintech.management.functionalities.domain.FunctionalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface FunctionalityRepository extends JpaRepository<FunctionalityEntity, UUID> {

    // Este metodo nos servira para traer todo de golpe y procesarlo en memoria
    // Es más eficiente para estructuras de árbol pequeñas como un menú
    List<FunctionalityEntity> findAll();

    // Opcional: Si luego quieres buscar solo por tipo (MODULE, SUBMODULE, etc.)
    List<FunctionalityEntity> findByType(String type);
}

