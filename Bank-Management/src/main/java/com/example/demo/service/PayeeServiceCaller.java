package com.example.demo.service;

import com.example.demo.client.PayeeClient;
import com.example.demo.entities.Payee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayeeServiceCaller {

    private final PayeeClient payeeClient;

    @Autowired
    public PayeeServiceCaller(PayeeClient payeeClient) {
        this.payeeClient = payeeClient;
    }

    public List<Payee> getPayeesByCustomer_Id(long customer_Id) {
        return payeeClient.getPayeesByCustomer_Id(customer_Id);
    }

    public Payee addPayee(Payee payee) {
        return payeeClient.addPayee(payee);
    }
 

    public List<Payee> fetchCustomerPayees(long customer_Id) {
        // Convert long to String because Feign client expects String
        return payeeClient.getPayeesByCustomer_Id(customer_Id);
    }
}
