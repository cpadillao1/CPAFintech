package com.fintech.core.accounts.service;

import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.core.accounts.domain.AccountHoldEntity;
import com.fintech.core.accounts.dto.AccountHoldCreateRecord;
import com.fintech.core.accounts.dto.AccountHoldDTO;
import com.fintech.core.accounts.mapper.AccountHoldMapper;
import com.fintech.core.accounts.repository.AccountHoldRepository;
import com.fintech.core.accounts.repository.AccountRepository;
import com.fintech.management.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountHoldService {

    private final AccountHoldRepository holdRepository;
    private final AccountRepository accountRepository;
    private final AccountHoldMapper holdMapper;
    private final CatalogService catalogService;

    /**
     * Crea un nuevo bloqueo y actualiza el saldo disponible de la cuenta.
     */
    @Transactional
    public AccountHoldDTO createHold(AccountHoldCreateRecord record, String currentUser) {
        // 1. Buscar la cuenta por número de cuenta en lugar de ID
        AccountEntity account = accountRepository.findByAccountNumber(record.accountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found with Number: " + record.accountNumber()));

        // 2. Obtener estado inicial desde catálogo
        Long statusActive = catalogService.getDetailIdByCode("HOLD_STATUS", "ACTIVE");

        // 3. Mapear Record a Entidad
        // Nota: Asegúrate de que tu Mapper ignore el setAccount automático si usas MapStruct,
        // o hazlo manualmente como aquí:
        AccountHoldEntity entity = holdMapper.toEntity(record);
        entity.setAccount(account); // Seteamos la cuenta encontrada
        entity.setHoldTypeId(record.holdTypeId());
        entity.setStatusId(statusActive);
        entity.setStartDate(record.startDate() != null ? record.startDate() : LocalDate.now());
        entity.setExpiryDate(record.expiryDate());
        entity.setCreatedBy(currentUser);

        // 4. Actualizar Saldos (Lógica de negocio intacta)
        if (account.getAmountHold() == null) account.setAmountHold(BigDecimal.ZERO);

        account.setAmountHold(account.getAmountHold().add(record.amount()));
        account.setAvailableBalance(account.getBalanceToday().subtract(account.getAmountHold()));

        // 5. Persistencia
        accountRepository.save(account);
        AccountHoldEntity savedEntity = holdRepository.save(entity);

        return holdMapper.toDto(savedEntity);
    }

    /**
     * Consulta de bloqueos activos para el Frontend.
     */
    @Transactional(readOnly = true)
    public Page<AccountHoldDTO> getActiveHoldsByAccountNumber(String accountNumber,
                                                              Pageable pageable) {
        //Long statusActive = catalogService.getDetailIdByCode("HOLD_STATUS", "ACTIVE");
        //Page<AccountHoldEntity> entities = holdRepository.findActiveHoldsByAccountNumber(accountNumber, statusActive, pageable);
        Page<AccountHoldEntity> entities = holdRepository.findActiveHoldsByAccountNumber(accountNumber, pageable);

        return entities.map(hold -> {
            // Obtenemos los valores de catálogos primero
            String holdTypeDesc = catalogService.getDetailNameById(hold.getHoldTypeId());
            String statusDesc = catalogService.getDetailNameById(hold.getStatusId());
            //String ownerName = hold.getAccount().getCustomerName();

            // Creamos el Record final de una sola vez con toda la información
            return new AccountHoldDTO(
                    hold.getId(),
                    hold.getAccount().getId(),
                    hold.getAmount(),
                    hold.getHoldTypeId(),
                    holdTypeDesc != null ? holdTypeDesc : "GENERAL",
                    hold.getReferenceNumber(),
                    hold.getDescription(),
                    hold.getStartDate(),
                    hold.getExpiryDate(),
                    hold.getCreatedBy(),
                    statusDesc != null ? statusDesc : "ACTIVE"

            );
        });
    }


    /**
     * Levanta un bloqueo de forma MANUAL (Invocado por el Controller/FE).
     */
    @Transactional
    public void releaseHoldManual(Long holdId,
                                  String observations,
                                  String currentUser) {
        Long releaseTypeManual = catalogService.getDetailIdByCode("HOLD_RELEASE", "MANUAL");
        Long statusReleased = catalogService.getDetailIdByCode("HOLD_STATUS", "RELEASED");

        this.executeReleaseProcess(holdId, statusReleased, releaseTypeManual, observations, currentUser);
    }

    /**
     * Levanta un bloqueo por SISTEMA (Invocado por el Job de EoD).
     */
    @Transactional
    public void releaseHoldBySystem(Long holdId) {
        Long releaseTypeSystem = catalogService.getDetailIdByCode("HOLD_RELEASE", "BATCH_EOD");
        Long statusExpired = catalogService.getDetailIdByCode("HOLD_STATUS", "EXPIRED");

        String autoObs = "Automatic release by End of Day process due to expiration date.";

        this.executeReleaseProcess(holdId, statusExpired, releaseTypeSystem, autoObs, "SYSTEM_BATCH");
    }

    /**
     * MOTOR DE LIBERACIÓN CENTRALIZADO (Privado)
     * Garantiza que el dinero regrese al disponible correctamente en ambos casos.
     */
    private void executeReleaseProcess(Long holdId,
                                       Long targetStatus,
                                       Long releaseType,
                                       String obs,
                                       String user) {
        AccountHoldEntity hold = holdRepository.findById(holdId)
                .orElseThrow(() -> new RuntimeException("Hold record not found: " + holdId));

        Long statusActive = catalogService.getDetailIdByCode("HOLD_STATUS", "ACTIVE");

        // Regla de Oro: Solo se liberan bloqueos que están activos
        if (!hold.getStatusId().equals(statusActive)) {
            throw new RuntimeException("The hold is not in ACTIVE status and cannot be released.");
        }

        // 1. Ajustar saldos en la Cuenta
        AccountEntity account = hold.getAccount();
        account.setAmountHold(account.getAmountHold().subtract(hold.getAmount()));
        account.setAvailableBalance(account.getBalanceToday().subtract(account.getAmountHold()));

        // 2. Marcar el bloqueo como finalizado
        hold.setStatusId(targetStatus);
        hold.setReleaseTypeId(releaseType);
        hold.setReleasedBy(user);
        hold.setReleasedAt(LocalDateTime.now());
        hold.setReleaseObservations(obs);

        // 3. Guardar cambios (Atómico por @Transactional)
        accountRepository.save(account);
        holdRepository.save(hold);
    }
}