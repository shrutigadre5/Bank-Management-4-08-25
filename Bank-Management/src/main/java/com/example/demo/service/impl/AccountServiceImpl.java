package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.AccountDTO;
import com.example.demo.entities.Account;
import com.example.demo.entities.Customer;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.repos.AccountRepository;
import com.example.demo.repos.CustomerLoginRepository;
import com.example.demo.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private CustomerLoginRepository customerRepo;


    @Override
    public List<AccountDTO> getAccountsByCustomerId(Long customer_Id) {
        List<Account> accounts = accountRepo.findByCustomerCustomerId(customer_Id);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No accounts found for customer ID: " + customer_Id);
        }

        return accounts.stream().map(account -> {
            AccountDTO dto = new AccountDTO();
            dto.setAccountNo(account.getAccountNo());
            dto.setAccountType(account.getAccountType());
            dto.setApplicationDate(account.getApplicationDate());
            dto.setStatus(account.getStatus());
            dto.setBalance(account.getBalance());
            dto.setTransactionPassword(account.getTransactionPassword());

            Customer customer = account.getCustomer();
            dto.setCustomer_Id(customer.getCustomerId());
            dto.setFullName(customer.getFirstName() + " " + customer.getLastName());
            dto.setEmail(customer.getEmail());
            dto.setMobileNo(customer.getMobileNo());
            dto.setAadharNo(customer.getAadharNo());
            dto.setPanNo(customer.getPanNo());
            dto.setOccupation(customer.getOccupation());
            dto.setAnnualIncome(customer.getAnnualIncome());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Account updateAccount(Long accountNo, Account updated) {
        Account acc = accountRepo.findById(accountNo)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNo));

        acc.setAccountType(updated.getAccountType());
        acc.setStatus(updated.getStatus());
        acc.setBalance(updated.getBalance());
        acc.setTransactionPassword(updated.getTransactionPassword());
        return accountRepo.save(acc);
    }

    @Override
    public Account patchAccount(Long accountNo, Account patch) {
        Account existing = accountRepo.findById(accountNo)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        if (patch.getAccountType() != null) {
            existing.setAccountType(patch.getAccountType());
        }
        if (patch.getStatus() != null) {
            existing.setStatus(patch.getStatus());
        }
        if (patch.getBalance() != null) {
            existing.setBalance(patch.getBalance());
        }
        if (patch.getTransactionPassword() != null) {
            existing.setTransactionPassword(patch.getTransactionPassword());
        }

        // ✅ Update customer email and phone if present
        if (patch.getCustomer() != null) {
            Customer existingCustomer = existing.getCustomer();
            Customer patchCustomer = patch.getCustomer();

            if (patchCustomer.getEmail() != null) {
                existingCustomer.setEmail(patchCustomer.getEmail());
            }
            if (patchCustomer.getMobileNo() != null) {
                existingCustomer.setMobileNo(patchCustomer.getMobileNo());
            }

            customerRepo.save(existingCustomer); // assuming injected
        }

        return accountRepo.save(existing);
    }


    @Override
    public void deleteAccount(Long accountNo) {
        if (!accountRepo.existsById(accountNo)) {
            throw new AccountNotFoundException("Account not found: " + accountNo);
        }
        accountRepo.deleteById(accountNo);
    }
}
