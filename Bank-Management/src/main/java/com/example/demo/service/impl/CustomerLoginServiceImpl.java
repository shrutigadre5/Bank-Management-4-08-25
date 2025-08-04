package com.example.demo.service.impl;

import com.example.demo.dto.CustomerLoginRequestDTO;
import com.example.demo.exception.*;
import com.example.demo.dto.CustomerLoginResponseDTO;
import com.example.demo.entities.Customer;
import com.example.demo.repos.CustomerLoginRepository;
import com.example.demo.service.CustomerLoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerLoginServiceImpl implements CustomerLoginService {

	    @Autowired
	    private CustomerLoginRepository customerLoginRepository;

	    @Autowired
	    private EmailService emailService;

	    @Override
	    public CustomerLoginResponseDTO login(CustomerLoginRequestDTO request) {
	        Long customerId = request.getCustomerId();
	        Customer customer = customerLoginRepository.findById(customerId)
	                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));

	        if (!customer.getLoginPassword().equals(request.getLoginPassword())) {
	            throw new RuntimeException("Invalid password.");
	        }

	        // Send email
	        //emailService.sendLoginConfirmationEmail(customer.getEmail(), customer.getFirstName());

	        String fullName = customer.getFirstName() + " " + customer.getLastName();
	        return new CustomerLoginResponseDTO(customer.getCustomerId(), fullName, "Login Successful");
	    }
	}
