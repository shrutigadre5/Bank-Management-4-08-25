package com.example.demo.vo;

import java.math.BigDecimal;
import java.util.List;

public class StatementWithBalanceDTO {

    private String accountNumber;
    private BigDecimal balance;
    private List<TransactionStatementDTO> transactions;

    // Getters and setters
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<TransactionStatementDTO> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<TransactionStatementDTO> transactions) {
        this.transactions = transactions;
    }
}

