package com.example.demo.repos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerCustomerId(Long customerId); // fetch accounts for given customer
    
    long countByStatus(String string);
	
	Long countByAccountTypeIgnoreCase( String accountType);
}
