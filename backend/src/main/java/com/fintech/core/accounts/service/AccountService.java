package com.fintech.core.accounts.service;


import com.fintech.core.accounts.dto.AccountCreateRequestDTO;
import com.fintech.core.accounts.dto.AccountResponseDTO;
import com.fintech.core.accounts.dto.AccountSearchDTO;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponseDTO createAccount(AccountCreateRequestDTO request);

    // Agrega esto al final de tu interface
    List<AccountSearchDTO> searchAccounts(Integer productId, Integer subproductId, String accountNumber);

}

