package com.fintech.core.transactions.service;

import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.core.accounts.repository.AccountRestrictionRepository;
import com.fintech.core.transactions.domain.LogMov;
import com.fintech.core.transactions.domain.TrConfigEntity;
import com.fintech.core.transactions.dto.ReversalRequestDTO;
import com.fintech.core.transactions.dto.TransactionRequestDTO;
import com.fintech.core.transactions.dto.TransactionResponseDTO;
import com.fintech.core.transactions.dto.TransferRequestDTO;
import com.fintech.core.transactions.mapper.TransactionMapper;
import com.fintech.core.accounts.repository.AccountRepository;
import com.fintech.core.transactions.repository.LogMovRepository;
import com.fintech.core.transactions.repository.TrConfigRepository;
import com.fintech.management.catalog.service.CatalogService;
import com.fintech.management.control_system.service.SystemControlService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogMovService {

    private final LogMovRepository logMovRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final SystemControlService controlService;
    private final CatalogService catalogService;
    private final TrConfigRepository trConfigRepository;
    private final AccountRestrictionRepository restrictionRepository;

    /**
     * Procesa Transferencias entre cuentas (ND Origen -> NC Destino)
     */
    @Transactional
    public TransactionResponseDTO processTransfer(TransferRequestDTO dto) {
        // 1. Validar Idempotencia de la transferencia
        if (logMovRepository.existsByTransactionReference(dto.transactionReference())) {
            throw new RuntimeException("Duplicate transfer reference: " + dto.transactionReference());
        }

        // 2. Ejecutar Débito en Cuenta Origen (ND)
        TransactionRequestDTO debitRequest = new TransactionRequestDTO(
                Long.parseLong(dto.sourceAccountId()), // accountId
                dto.sourceConfigId(),                  // configId
                dto.amount(),                          // amount
                "TRANSFER TO: " + dto.targetAccountId() + " - " + dto.description(), // description
                dto.transactionReference(),            // transactionReference
                dto.createdBy(),                       // createdBy
                dto.terminalIp(),
                dto.sourceConfigId(),
                dto.originCode()
        );

        TransactionResponseDTO sourceResponse = processTransaction(debitRequest);

        // 3. Ejecutar Crédito en Cuenta Destino (NC)
        // Usamos una referencia ligeramente distinta o la misma según tu regla de negocio
        TransactionRequestDTO creditRequest = new TransactionRequestDTO(
                Long.parseLong(dto.targetAccountId()), // accountId
                dto.targetConfigId(),                  // configId
                dto.amount(),                          // amount
                "TRANSFER FROM: " + dto.sourceAccountId() + " - " + dto.description(), // description
                "IN-" + dto.transactionReference(),    // transactionReference
                dto.createdBy(),                       // createdBy
                dto.terminalIp(),                       // terminalIp
                dto.targetConfigId(),
                dto.originCode()
        );

        processTransaction(creditRequest);

        return sourceResponse;
    }

    /**
     * Procesa transacciones en línea y Batch (Depósitos y Retiros)
     */
    @Transactional
    public TransactionResponseDTO processTransaction(TransactionRequestDTO dto) {

        if (logMovRepository.existsByTransactionReference(dto.transactionReference())) {
            throw new RuntimeException("Duplicate transaction reference: " + dto.transactionReference());
        }

        AccountEntity account = accountRepository.findByAccountNumber(dto.accountId().toString())
                .orElseThrow(() -> new RuntimeException("Account number " + dto.accountId() + " not found"));

        Long internalId = account.getId();

        var controlSystem = controlService.getControlEntity();

        // --- LÓGICA DE FECHA VALOR (EOD) ---
        // Determinamos la fecha contable según el estado del sistema
        LocalDate accountingDate;
        boolean isPostDated = false;

        if ("BATCH_RUNNING".equals(controlSystem.getStatus()) || "IN_CLOSING".equals(controlSystem.getStatus())) {
            accountingDate = controlSystem.getAfterBusinessDate(); // T + 1
            isPostDated = true;
        } else {
            accountingDate = controlSystem.getBusinessDate(); // T
        }
        // -----------------------------------

        TrConfigEntity config = trConfigRepository.findById(dto.configId())
                .orElseThrow(() -> new EntityNotFoundException("Invalid transaction settings"));

        String typeCode = config.getType().getCode();

        boolean isDebit = "DB".equals(typeCode);

        // Ejecutar validación
        validateRestrictions(internalId, isDebit);

        // Ajuste: Obtener ID del catálogo global
        String catalogValueCode = isDebit ? "DB" : "CR";
        Long transactionCatalogId = catalogService.getDetailIdByCode("TRANSACTION", catalogValueCode);

        BigDecimal amount = dto.amount();
        BigDecimal currentTotalBalance = account.getBalanceToday();
        BigDecimal currentHold = account.getAmountHold();
        BigDecimal previousBalance = account.getAvailableBalance();

        BigDecimal realAvailable = currentTotalBalance.subtract(currentHold);
        BigDecimal newTotalBalance;

        if (isDebit) {
            if (realAvailable.compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient funds (Some funds are on hold)");
            }
            newTotalBalance = currentTotalBalance.subtract(amount);

            if (!isPostDated) {
                account.setAmountNdToday(account.getAmountNdToday().add(amount));
            }
            account.setLastDateNd(LocalDateTime.now());
        } else {
            newTotalBalance = currentTotalBalance.add(amount);
            if (!isPostDated) {
                account.setAmountNcToday(account.getAmountNcToday().add(amount));
            }
            account.setLastDateNc(LocalDateTime.now());
        }

        account.setBalanceToday(newTotalBalance);
        account.setAvailableBalance(newTotalBalance.subtract(currentHold));
        account.setUpdatedAt(LocalDateTime.now());
        account.setLastMovementDate(LocalDateTime.now());
        accountRepository.save(account);

        Long originId = catalogService.getDetailIdByCode("CHANNEL", dto.originCode());

        LogMov log = transactionMapper.toEntity(dto);
        log.setAccountId(internalId);
        log.setPreviousBalance(previousBalance);
        log.setNewBalance(newTotalBalance.subtract(currentHold));
        log.setCreatedBy(dto.createdBy());
        log.setStatus("REGISTERED");
        log.setBusinessDate(accountingDate);
        log.setTerminalIp(dto.terminalIp());
        log.setOriginId(originId);
        log.setTransactionTypeId(transactionCatalogId);
        log.setConfigId(config.getConfigId());

        LogMov savedLog = logMovRepository.save(log);

        return transactionMapper.toResponse(savedLog);
    }

    @Transactional
    public TransactionResponseDTO reverseTransaction(ReversalRequestDTO dto) {

        LogMov originalMov = logMovRepository.findById(dto.transactionId())
                .orElseThrow(() -> new EntityNotFoundException("Original transaction not found"));

        if ("REVERSED".equals(originalMov.getStatus())) {
            throw new IllegalArgumentException("The transaction has already been reversed");
        }

        AccountEntity account = accountRepository.findById(originalMov.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Associated account not found"));

        BigDecimal previousBalance = account.getBalanceToday();
        BigDecimal amountToReverse = originalMov.getAmount();
        BigDecimal newBalance;

        if (isDebitType(originalMov.getTransactionTypeId())) {
            newBalance = previousBalance.add(amountToReverse);
        } else {
            if (previousBalance.compareTo(amountToReverse) < 0) {
                throw new RuntimeException("Insufficient funds to process the reversal");
            }
            newBalance = previousBalance.subtract(amountToReverse);
        }

        account.setBalanceToday(newBalance);
        accountRepository.save(account);

        originalMov.setStatus("REVERSED");
        originalMov.setReversedAt(LocalDateTime.now());
        originalMov.setReversedBy(dto.user());
        originalMov.setReversalReference("REV-" + originalMov.getTransactionReference());
        logMovRepository.save(originalMov);

        LogMov adjustmentLog = LogMov.builder()
                .accountId(account.getId())
                .transactionTypeId(originalMov.getTransactionTypeId())
                .originId(originalMov.getOriginId())
                .businessDate(originalMov.getBusinessDate())
                .amount(amountToReverse.negate())
                .previousBalance(previousBalance)
                .newBalance(newBalance)
                .description("REVERSO: " + dto.reason())
                .transactionReference(UUID.randomUUID().toString())
                .status("REVERSED")
                .createdBy(dto.user())
                .build();

        logMovRepository.save(adjustmentLog);

        return transactionMapper.toResponse(originalMov);
    }

    private boolean isDebitType(Long typeId) {
        // Asumiendo que obtienes el código del catálogo para validar
        return typeId % 2 == 0;
    }

    private void validateRestrictions(Long accountId, boolean isDebit) {
        // 1. Obtener el ID del catálogo para estado "ACTIVE" (Ajusta el código según tu catálogo real)
        Long activeStatusId = catalogService.getDetailIdByCode("HOLD_STATUS", "ACTIVE");

        // 2. Buscar restricciones activas
        var restrictions = restrictionRepository.findByAccountIdAndStatusCatId(accountId, activeStatusId);

        // 3. Obtener los IDs de los tipos de restricción desde catálogo para comparar
        // Esto evita hardcodear IDs como 1, 2, 3
        Long restrictionDebitId = catalogService.getDetailIdByCode("RESTRICTION_TYPE", "DEBIT");
        Long restrictionCreditId = catalogService.getDetailIdByCode("RESTRICTION_TYPE", "CREDIT");
        Long restrictionTotalId = catalogService.getDetailIdByCode("RESTRICTION_TYPE", "TOTAL");

        for (var restriction : restrictions) {
            Long typeId = restriction.getRestrictionTypeCatId();

            // Si existe restricción TOTAL, bloqueo total
            if (typeId.equals(restrictionTotalId)) {
                throw new RuntimeException("Transaction blocked: Account has a TOTAL restriction.");
            }

            // Si es Débito y existe restricción de Débito
            if (isDebit && typeId.equals(restrictionDebitId)) {
                throw new RuntimeException("Transaction blocked: Account has a DEBIT restriction.");
            }

            // Si es Crédito y existe restricción de Crédito
            if (!isDebit && typeId.equals(restrictionCreditId)) {
                throw new RuntimeException("Transaction blocked: Account has a CREDIT restriction.");
            }
        }
    }
}