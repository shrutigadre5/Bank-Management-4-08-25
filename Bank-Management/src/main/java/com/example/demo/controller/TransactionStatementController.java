package com.example.demo.controller;

import com.example.demo.client.AccountClient;
import com.example.demo.entities.TransactionStatement;
import com.example.demo.service.TransactionStatementService;
import com.example.demo.vo.AccountDTO;
import com.example.demo.vo.StatementWithBalanceDTO;
import com.example.demo.vo.TransactionStatementDTO;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/transactions")
public class TransactionStatementController {

    @Autowired
    private TransactionStatementService service;
    
    @Autowired
	private AccountClient accountClient;  // FeignClient or RestTemplate

    // =========================
    // CRUD & basic entity reads
    // =========================

    // 🔄 Create a transaction (POST)
    @PostMapping("/add")
    public TransactionStatement addTransaction(@RequestBody TransactionStatement transaction) {
        return service.addTransaction(transaction);
    }

    // 🔍 Read a transaction by ID (GET)
    @GetMapping("/get/{id}")
    public TransactionStatement getTransactionById(@PathVariable Long id) {
        return service.getTransactionById(id);
    }

    // 📄 Read all transactions by account number (entity list)
    @GetMapping("/{accountNumber}")
    public List<TransactionStatement> getAllTransactions(@PathVariable String accountNumber) {
        return service.getAllTransactions(accountNumber);
    }

    // 📊 Get last N transactions (entity list)
    @GetMapping("/{accountNumber}/latest")
    public List<TransactionStatement> getLatestTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        return service.getLatestTransactions(accountNumber, limit);
    }

    // 📆 Get transactions from the last 6 months (entity list)
    @GetMapping("/{accountNumber}/last6months")
    public List<TransactionStatement> getLastSixMonthsTransactions(@PathVariable String accountNumber) {
        return service.getLastSixMonthsTransactions(accountNumber);
    }

    // ✏️ Update a transaction (PUT)
    @PutMapping("/update/{id}")
    public TransactionStatement updateTransaction(@PathVariable Long id,
                                                  @RequestBody TransactionStatement transaction) {
        return service.updateTransaction(id, transaction);
    }

    // 🗑️ Delete a transaction (DELETE)
    @DeleteMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        return service.deleteTransaction(id);
    }

    // =========================
    // Statement (DTO) endpoints
    // =========================

    // 📄 Statement (DTO) – all rows for an account, minimal fields + derived txnType/counterparty
    @GetMapping("/{accountNumber}/statement")
    public List<TransactionStatementDTO> getStatement(@PathVariable String accountNumber) {
        // If you created a simple pass-through method, else call searchStatement with defaults
        return service.searchStatement(
                accountNumber,
                "ALL",      // type
                null,       // from
                null,       // to
                "transactionDate",
                "DESC",
                0,          // page
                100         // size (adjust default as you wish)
        );
    }

    // 📊 Statement (DTO) – latest N
    @GetMapping("/{accountNumber}/statement/latest")
    public List<TransactionStatementDTO> getLatestStatement(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "5") @Min(1) int limit) {
        // If you added service.getLatestStatement(account, limit) use that; otherwise route via search with size=limit
        return service.searchStatement(
                accountNumber,
                "ALL",
                null,
                null,
                "transactionDate",
                "DESC",
                0,
                limit
        );
    }

    // 🔎 Statement (DTO) – powerful search with type/date range/sort/pagination
    // type: ALL | DEBIT | CREDIT
    // from/to: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss (to is exclusive if date-only)
    // sortBy: transactionDate | amount | paymentMethod | status | transactionId
    // direction: ASC | DESC
    @GetMapping("/{accountNumber}/statement/search")
    public List<TransactionStatementDTO> searchStatement(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "50") @Min(1) Integer size
    ) {
        return service.searchStatement(accountNumber, type, from, to, sortBy, direction, page, size);
    }
    
    @GetMapping("/{accountNo}/statementWithBalance")
    public ResponseEntity<StatementWithBalanceDTO> getStatementWithBalance(
            @PathVariable String accountNo,
            @RequestParam(defaultValue = "5") int count) {

        // 1. Fetch latest N transactions (DTOs)
        List<TransactionStatementDTO> transactions = service.getLatestStatementDTO(accountNo, count);

        // 2. Fetch account info from Feign Client
        AccountDTO account = accountClient.getAccountByNumber(accountNo);

        // 3. Build final DTO
        StatementWithBalanceDTO dto = new StatementWithBalanceDTO();
        dto.setAccountNumber(accountNo);
        dto.setBalance(account.getBalance());
        dto.setTransactions(transactions);

        return ResponseEntity.ok(dto);
    }


}

