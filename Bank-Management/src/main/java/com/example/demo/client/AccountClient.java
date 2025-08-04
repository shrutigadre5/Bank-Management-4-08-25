package com.example.demo.client;

import com.example.demo.vo.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
	    name = "ACCOUNT-SERVICE"  // Service name as registered in Eureka
	)
	public interface AccountClient {

	    @GetMapping("/api/accounts/{accountNumber}")
	    AccountDTO getAccountByNumber(@PathVariable("accountNumber") String accountNumber);
	}


