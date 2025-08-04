package com.example.demo.controller;


import com.example.demo.dto.AccountDTO;
import com.example.demo.entities.Account;
import com.example.demo.entities.Customer;
import com.example.demo.repos.CustomerLoginRepository;
import com.example.demo.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService service;
    
    @Autowired
    private CustomerLoginRepository customerRepo;

    @GetMapping("/customer/{customerId}")
    public List<AccountDTO> getAccountsForCustomer(@PathVariable Long customerId) {
        return service.getAccountsByCustomerId(customerId);
    }

    @PutMapping("/{accountNo}")
    public Account updateAccount(@PathVariable Long accountNo, @RequestBody Account updated) {
        return service.updateAccount(accountNo, updated);
    }

    @PatchMapping("/customers/{customerId}")
    public Customer patchCustomer(@PathVariable Long customerId, @RequestBody Customer patch) {
        Customer existing = customerRepo.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (patch.getEmail() != null) {
            existing.setEmail(patch.getEmail());
        }
        if (patch.getMobileNo() != null) {
            existing.setMobileNo(patch.getMobileNo());
        }

        return customerRepo.save(existing);
    }



    @DeleteMapping("/{accountNo}")
    public void deleteAccount(@PathVariable Long accountNo) {
        service.deleteAccount(accountNo);
    }
}

