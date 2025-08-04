package com.example.demo.controller;

import com.example.demo.dto.CustomerLoginRequestDTO;
import com.example.demo.dto.CustomerLoginResponseDTO;
import com.example.demo.entities.Customer;
import com.example.demo.repos.CustomerLoginRepository;
import com.example.demo.service.CustomerLoginService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerLoginController {

    @Autowired
    private CustomerLoginService customerService;
    
    @Autowired
    private CustomerLoginRepository customerRepository;


    @PostMapping("/login")
    public CustomerLoginResponseDTO login(@RequestBody CustomerLoginRequestDTO loginRequest) {
        return customerService.login(loginRequest);
    }
    
    @GetMapping("/debug/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

}
