package com.fintech.core.accounts.service;


import com.fintech.audit.aspect.Audit;
import com.fintech.core.accounts.domain.AccountEntity;
import com.fintech.core.accounts.domain.AccountHolder;
import com.fintech.core.accounts.domain.CapitalizationPeriodicity;
import com.fintech.core.accounts.dto.AccountCreateRequestDTO;
import com.fintech.core.accounts.dto.AccountHolderDetailDTO;
import com.fintech.core.accounts.dto.AccountResponseDTO;
import com.fintech.core.accounts.dto.AccountSearchDTO;
import com.fintech.core.accounts.mapper.AccountHolderMapper;
import com.fintech.core.accounts.mapper.AccountMapper;
import com.fintech.core.accounts.repository.AccountRepository;
import com.fintech.core.accounts.util.AccountNumberGenerator;
import com.fintech.management.catalog.domain.CatalogDetail;
import com.fintech.management.catalog.service.CatalogService;
import com.fintech.management.control_system.service.SystemControlService;
import com.fintech.management.subproducts.repository.SubproductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final SubproductRepository subproductRepository;
    private final com.fintech.core.customers.repository.CustomerRepository customerRepository;
    private final AccountMapper accountMapper;
    private final AccountHolderMapper holderMapper;
    private final SystemControlService controlService;
    private final CatalogService catalogService;
    private final AccountNumberGenerator accountGenerator;

    @Override
    @PreAuthorize("hasAuthority('ACCO_CREATE')")
    @Audit(action = "ACCO_CREATE", module = "ACCOUNTS")
    @Transactional
    public AccountResponseDTO createAccount(AccountCreateRequestDTO request) {
        // 1. Validaciones y carga de contexto
        validateJointAccount(request);
        Long statusActiveId = catalogService.getDetailIdByCode("ACCOUNT_STATUS", "ACTIVE");
        var controlSystem = controlService.getControlEntity();

        var subproduct = subproductRepository.findById(request.subproductId())
                .orElseThrow(() -> new EntityNotFoundException("Subproduct not found: " + request.subproductId()));

        // 2. Generar número de cuenta
        String newAccountNumber = accountGenerator.getNextNumber(subproduct.getAccountSequenceId());

        // 3. Mapeo y configuración de Account
        AccountEntity account = accountMapper.toEntity(request);
        // NOTA: El ID se generará solo por la anotación @GeneratedValue en la entidad

        account.setAccountNumber(newAccountNumber);
        account.setStatusId(statusActiveId);
        account.setOpeningDate(controlSystem.getBusinessDate());
        account.setLastProcessedDate(controlSystem.getBeforeBusinessDate());
        account.setGeneratesInterest(subproduct.getGeneratesInterest());
        account.setAccountType(subproduct.getProduct().getCode());
        account.setCreatedBy(request.createdBy());
        account.setSubproduct(subproduct); // Crucial para el subproduct_id
        // --- NUEVA LÓGICA DE PERIODICIDAD ---
        // Asignamos el valor entero basado en el String del subproducto
        Integer periodicityValue = CapitalizationPeriodicity.getMonthsByCode(subproduct.getStatementFrequency());
        account.setPeriodicity(periodicityValue);
        account.setMonthsAccumulated(0); // Nace en cero como acordamos
        // ------------------------------------
        // 4. Procesar Holders y vincularlos a la Account (Usando el helper addHolder)
        processAndLinkHolders(account, request.holders(), request.createdBy());

        // 5. Un solo save para todo el grafo (Account + Holders)
        AccountEntity savedAccount = accountRepository.save(account);

        return accountMapper.toResponse(savedAccount);
    }

    private void processAndLinkHolders(AccountEntity account,
                                       List<AccountCreateRequestDTO.AccountHolderRequestDTO> holdersDto,
                                       String user) {
        Long activeStatus = catalogService.getDetailIdByCode("ACCOUNT_STATUS", "ACTIVE");

        holdersDto.forEach(dto -> {
            AccountHolder holder = holderMapper.toEntity(dto);

            holder.setStatusId(activeStatus);
            holder.setCreatedBy(user);

            CatalogDetail roleProxy = new CatalogDetail();
            roleProxy.setId(catalogService.getDetailIdByCode("ROLES_HOLDER", dto.ownershipTypeCode()));
            holder.setOwnershipType(roleProxy);
            holder.setCustomer(customerRepository.getReferenceById(dto.customerId()));

            // WE LINK TO THE PARENT (This triggers the cascade correctly)
            account.addHolder(holder);
        });
    }

    private void validateJointAccount(AccountCreateRequestDTO request) {
        Long ownershipJoint = catalogService.getDetailIdByCode("OWNERSHIP", "JOINT");
        if (request.ownershipTypeId().equals(ownershipJoint) && (request.holders() == null || request.holders().size() <= 1)) {
            throw new EntityNotFoundException("A JOINT account must have at least one joint account holder.");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ACCO_QUERY')")
    @Audit(action = "ACCO_QUERY", module = "ACCOUNTS")
    @Transactional(readOnly = true)
    public List<AccountSearchDTO> searchAccounts(Integer productId,
                                                 Integer subproductId,
                                                 String accountNumber) {
        List<AccountEntity> accounts = accountRepository.findByHierarchy(productId, subproductId, accountNumber);

        // Validamos si la lista está vacía
        if (accounts.isEmpty()) {
            throw new EntityNotFoundException("Warning: No account was found with the specified hierarchy and number " + accountNumber);
        }

        return accounts.stream().map(acc -> {
            List<AccountHolderDetailDTO> holderDtos = acc.getHolders().stream()
                    .map(h -> new AccountHolderDetailDTO(
                            h.getCustomer().getId(),
                            h.getCustomer().getFirstName() + " " + h.getCustomer().getLastName(),
                            h.getCustomer().getDocumentNumber(),
                            h.getOwnershipType().getName()
                    )).toList();

            return new AccountSearchDTO(
                    acc.getId(), acc.getAccountNumber(), acc.getSubproduct().getProduct().getName(),
                    acc.getSubproduct().getName(), "ACTIVE", acc.getCreatedBy(),
                    acc.getOpeningDate(), acc.getAvailableBalance(), acc.getBalanceToday(),
                    acc.getBalanceYesterday(), acc.getAmountHold(), acc.getLastProcessedDate(),
                    acc.getAccruedInterestMonth(), acc.getAmountNdToday(), acc.getAmountNdYesterday(),
                    acc.getLastDateNd(), acc.getAmountNcToday(), acc.getAmountNcYesterday(),
                    acc.getLastDateNc(), holderDtos
            );
        }).toList();
    }

}
