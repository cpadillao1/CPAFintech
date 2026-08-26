package com.fintech.core.accounts.service;

import com.fintech.core.accounts.domain.AccountRestrictionEntity;
import com.fintech.core.accounts.dto.AccountRestrictionDTO;
import com.fintech.core.accounts.dto.AccountRestrictionRequest;
import com.fintech.core.accounts.mapper.AccountRestrictionMapper;
import com.fintech.core.accounts.repository.AccountRepository;
import com.fintech.core.accounts.repository.AccountRestrictionRepository;
import com.fintech.management.catalog.service.CatalogService;
import com.fintech.management.control_system.service.SystemControlService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountRestrictionService {

    private final AccountRestrictionRepository restrictionRepository;
    private final AccountRepository accountRepository;
    private final AccountRestrictionMapper mapper;
    private final CatalogService catalogService;
    private final SystemControlService controlService;


    @Transactional
    public AccountRestrictionDTO createRestriction(AccountRestrictionRequest request) {

        var account = accountRepository.findByAccountNumber(request.accountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Obtenemos todas las restricciones activas de la cuenta
        List<AccountRestrictionEntity> activeRestrictions =
                restrictionRepository.findByAccountIdAndStatusCatId(account.getId(), request.statusCatId());

        String newTypeName = catalogService.getDetailNameById(request.restrictionTypeCatId()).toUpperCase();

        // --- NUEVA VALIDACIÓN DE REDUNDANCIA CRUZADA ---
        boolean hasDebit = activeRestrictions.stream()
                .anyMatch(r -> catalogService.getDetailNameById(r.getRestrictionTypeCatId()).toUpperCase().contains("DEBIT"));
        boolean hasCredit = activeRestrictions.stream()
                .anyMatch(r -> catalogService.getDetailNameById(r.getRestrictionTypeCatId()).toUpperCase().contains("CREDIT"));
        boolean hasTotal = activeRestrictions.stream()
                .anyMatch(r -> catalogService.getDetailNameById(r.getRestrictionTypeCatId()).toUpperCase().contains("TOTAL"));

        // Regla 1: Si ya hay TOTAL, no entra nada más
        if (hasTotal) {
            throw new RuntimeException("The account already has a TOTAL restriction.");
        }

        // Regla 2: Si quieres ingresar TOTAL, pero ya existen DEBITO y CREDITO
        if (newTypeName.contains("TOTAL") && hasDebit && hasCredit) {
            throw new RuntimeException("Redundant: The account already has both DEBIT and CREDIT restrictions active.");
        }

        // Regla 3: Duplicado exacto (Tu lógica inicial mejorada)
        if (activeRestrictions.stream().anyMatch(r -> r.getRestrictionTypeCatId().equals(request.restrictionTypeCatId()))) {
            throw new RuntimeException("There is already an active restriction of type " + newTypeName + " on the account.");
        }

        var controlSystem = controlService.getControlEntity();

        // 4. Mapeo y Persistencia
        AccountRestrictionEntity entity = mapper.toEntity(request);

        if (entity.getStartDate().isBefore(controlSystem.getBusinessDate())) {
            throw new RuntimeException("The start date cannot be earlier than the business date.");
        }

        if (entity.getAuthorizer() == null) {
            entity.setAuthorizer("SYSTEM_FE");
        }
        entity.setAccountId(account.getId());

        AccountRestrictionEntity savedEntity = restrictionRepository.save(entity);

        // 5. Construcción de respuesta
        AccountRestrictionDTO dto = mapper.toDto(savedEntity);
        dto.setAccountNumber(account.getAccountNumber());
        dto.setRestrictionType(catalogService.getDetailNameById(savedEntity.getRestrictionTypeCatId()));
        dto.setReasonCode(catalogService.getDetailNameById(savedEntity.getReasonCodeCatId()));
        dto.setStatus(catalogService.getDetailNameById(savedEntity.getStatusCatId()));

        return dto;
    }

    @Transactional(readOnly = true)
    public List<AccountRestrictionDTO> getActiveByAccountNumber(String accountNumber, Long statusActiveId) {
        // 1. Validar cuenta
        var account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        // 2. Obtener entidades
        List<AccountRestrictionEntity> entities =
                restrictionRepository.findByAccountIdAndStatusCatId(account.getId(), statusActiveId);

        // 3. Transformar y Enriquecer (Traducción de IDs a nombres)
        return entities.stream()
                .map(entity -> {
                    AccountRestrictionDTO dto = mapper.toDto(entity);

                    // Seteamos el número de cuenta (que no está en la tabla de restricciones)
                    dto.setAccountNumber(account.getAccountNumber());

                    // Traducimos los IDs a nombres legibles usando el CatalogService
                    dto.setRestrictionType(catalogService.getDetailNameById(entity.getRestrictionTypeCatId()));
                    dto.setReasonCode(catalogService.getDetailNameById(entity.getReasonCodeCatId()));
                    dto.setStatus(catalogService.getDetailNameById(entity.getStatusCatId()));

                    return dto;
                })
                .toList();
    }


    @Transactional
    public AccountRestrictionDTO releaseRestriction(Long restrictionId, String observations, Long statusReleasedId, String authorizer) {
        // 1. Buscar la restricción
        AccountRestrictionEntity restriction = restrictionRepository.findById(restrictionId)
                .orElseThrow(() -> new EntityNotFoundException("Restriction record not found"));

        // 2. VALIDACIÓN: Solo levantar si está en estado ACTIVE
        // Obtenemos el ID del estado 'ACTIVE' dinámicamente desde el catálogo
        Long statusActiveId = catalogService.getDetailIdByCode("HOLD_STATUS", "ACTIVE");

        if (!restriction.getStatusCatId().equals(statusActiveId)) {
            throw new RuntimeException("Only active restrictions can be released. Current status: "
                    + catalogService.getDetailNameById(restriction.getStatusCatId()));
        }

        // 3. Actualizar campos de liberación
        restriction.setStatusCatId(statusReleasedId);
        restriction.setReleaseAuthorizer(authorizer); // <--- Usamos el del FE
        restriction.setReleaseObservations(observations);
        restriction.setReleaseDate(LocalDateTime.now());

        // Seteamos la fecha de fin de la restricción a la fecha actual del sistema
        var controlSystem = controlService.getControlEntity();
        restriction.setEndDate(controlSystem.getBusinessDate());

        // 4. Guardar y retornar
        AccountRestrictionEntity saved = restrictionRepository.save(restriction);

        // Mapear a DTO y enriquecer nombres de catálogos para la respuesta del FE
        AccountRestrictionDTO dto = mapper.toDto(saved);
        dto.setStatus(catalogService.getDetailNameById(saved.getStatusCatId()));
        dto.setRestrictionType(catalogService.getDetailNameById(saved.getRestrictionTypeCatId()));

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<AccountRestrictionDTO> getByStatus(Long statusCatId, Pageable pageable) {
        Page<AccountRestrictionEntity> entities = restrictionRepository.findByStatusCatId(statusCatId, pageable);

        return entities.map(entity -> {
            AccountRestrictionDTO dto = mapper.toDto(entity);

            // Enriquecemos cada registro de la página
            // Nota: Si son muchos registros, considera un Join en el futuro
            dto.setRestrictionType(catalogService.getDetailNameById(entity.getRestrictionTypeCatId()));
            dto.setReasonCode(catalogService.getDetailNameById(entity.getReasonCodeCatId()));
            dto.setStatus(catalogService.getDetailNameById(entity.getStatusCatId()));

            // El accountNumber lo sacamos vía el ID de cuenta
            accountRepository.findById(entity.getAccountId())
                    .ifPresent(acc -> dto.setAccountNumber(acc.getAccountNumber()));

            return dto;
        });
    }

    /**
     * Valida si la transacción puede proceder según las restricciones de la cuenta.
     * * @param accountId ID interno de la cuenta (Long).
     * @param transactionType Tipo de movimiento: 'DEBITO' o 'CREDITO'.
     * @param channelId ID del catálogo de canal: 59 (ONLINE) o 60 (BATCH).
     * @throws RuntimeException si existe una restricción que impide el movimiento.
     */
    public void validateAccountOperation(Long accountId, String transactionType, Long channelId) {

        // REGLA DE NEGOCIO: Si el canal es BATCH (60), omitimos validaciones de restricción
        // Esto permite que el pago de intereses o cobros de sistema se ejecuten sin problemas.
        Long channelCatalogId = catalogService.getDetailIdByCode("CHANNEL", "BATCH");
        if (channelCatalogId.equals(channelId)) {
            return;
        }

        // Si es ONLINE (59) o cualquier otro canal manual, validamos restricciones ACTIVAS
        // Usamos el ID del catálogo de estado para 'ACTIVE' (ajustar según tu tabla de catálogos)
        //Long statusActiveId = 1L; // Ejemplo: ID para estado Activo
        Long statusActiveId = catalogService.getDetailIdByCode("HOLD_STATUS", "ACTIVE");

        List<AccountRestrictionEntity> activeRestrictions = restrictionRepository
                .findByAccountIdAndStatusCatId(accountId, statusActiveId);

        if (activeRestrictions.isEmpty()) {
            return; // Sin restricciones, puede operar
        }

        for (AccountRestrictionEntity restriction : activeRestrictions) {
            // Obtenemos el ID del tipo de restricción
            Long typeId = restriction.getRestrictionTypeCatId();

            // Lógica basada en IDs de catálogo de tipos de restricción
            // Asumiendo IDs de ejemplo (ajustar a tus valores reales):
            // 100: TOTAL, 101: NO DEBITO, 102: NO CREDITO
            Long restrictionTotalId = catalogService.getDetailIdByCode("RESTRICTION_TYPE", "TOTAL");

            if (typeId.equals(restrictionTotalId)) {
                throw new RuntimeException("The account is blocked for all transactions.");
            }
            Long restrictionDebitId = catalogService.getDetailIdByCode("RESTRICTION_TYPE", "DEBIT");

            if (typeId.equals(restrictionDebitId) && "DEBIT".equals(transactionType)) {
                throw new RuntimeException("The account has an active restriction on withdrawals/debits.");
            }

            Long restrictionCreditId = catalogService.getDetailIdByCode("RESTRICTION_TYPE", "CREDIT");

            if (typeId.equals(restrictionCreditId) && "CREDIT".equals(transactionType)) {
                throw new RuntimeException("The account has an active restriction on receiving deposits/credits.");
            }
        }
    }

}
