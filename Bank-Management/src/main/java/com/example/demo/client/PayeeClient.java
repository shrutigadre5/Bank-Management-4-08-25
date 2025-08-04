package com.example.demo.client;
import com.example.demo.dto.PayeeRequest;
import com.example.demo.entities.Payee;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "Online-Banking-Payee-Management", url = "http://localhost:8082")
public interface PayeeClient {

    @GetMapping("/api/payees/customer/{customer_Id}")
    List<Payee> getPayeesByCustomer_Id(@PathVariable("customer_Id") long customer_Id);

    @PostMapping("/api/payees/add")
    Payee addPayee(@RequestBody Payee payee);
}

