package com.example.demo.controller;
import com.example.demo.entities.Payee;
import com.example.demo.service.PayeeServiceCaller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/api/payee-ui")
public class PayeeUIController {

    @Autowired
    private PayeeServiceCaller caller;

    @GetMapping("/payees")
    public ResponseEntity<List<Payee>> getPayees(@RequestParam long customer_Id) {
        List<Payee> payees = caller.fetchCustomerPayees(customer_Id);
        return ResponseEntity.ok(payees);
    }
}
