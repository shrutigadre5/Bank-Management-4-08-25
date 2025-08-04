package com.example.demo.service;

import java.util.List;
import com.example.demo.dto.AccountDTO;
import com.example.demo.entities.Account;


public interface AccountService {
    List<AccountDTO> getAccountsByCustomerId(Long customerId);
    Account updateAccount(Long accountNo, Account updated);
    Account patchAccount(Long accountNo, Account patchData);
    void deleteAccount(Long accountNo);
}

