package com.example.demo.service.impl;

import com.example.demo.client.AccountClient;
import com.example.demo.entities.TransactionStatement;
import com.example.demo.repos.TransactionStatementRepo;
import com.example.demo.service.TransactionStatementService;
import com.example.demo.vo.AccountDTO;
import com.example.demo.vo.TransactionStatementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionStatementServiceImpl implements TransactionStatementService {

    @Autowired private TransactionStatementRepo repo;
    @Autowired private AccountClient accountClient; // optional enrichment

    // ➕ Create
    @Override
    public TransactionStatement addTransaction(TransactionStatement transaction) {
        return repo.save(transaction);
    }

    // 🔍 Read
    @Override
    public TransactionStatement getTransactionById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<TransactionStatement> getAllTransactions(String accountNumber) {
        try {
            AccountDTO a = accountClient.getAccountByNumber(accountNumber);
            System.out.println("Account: " + (a != null ? a.getHolderName() : "N/A"));
        } catch (Exception ignored) {}
        return repo.findBySenderAccountNoOrReceiverAccountNoOrderByTransactionDateDesc(accountNumber, accountNumber);
    }

    @Override
    public List<TransactionStatement> getLatestTransactions(String accountNumber, int limit) {
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "transactionDate"));
        return repo.findLatestForAccount(accountNumber, pageable);
    }

    @Override
    public List<TransactionStatement> getLastSixMonthsTransactions(String accountNumber) {
        LocalDate today = LocalDate.now();
        LocalDate from  = today.minusMonths(6);

        // IMPORTANT: convert to java.util.Date for repo method
        Date fromTs = toDate(from.atStartOfDay());
        Date toTs   = toDate(today.plusDays(1).atStartOfDay()); // exclusive upper bound

        return repo.findBetweenForAccount(accountNumber, fromTs, toTs);
    }

    @Override
    public TransactionStatement updateTransaction(Long id, TransactionStatement incoming) {
        return repo.findById(id).map(ex -> {
            ex.setSenderAccountNo(incoming.getSenderAccountNo());
            ex.setReceiverAccountNo(incoming.getReceiverAccountNo());
            ex.setPaymentMethod(incoming.getPaymentMethod());
            ex.setAmount(incoming.getAmount());
            ex.setStatus(incoming.getStatus());
            ex.setRemarks(incoming.getRemarks());
            ex.setTransactionDate(incoming.getTransactionDate());
            ex.setCreatedAt(incoming.getCreatedAt());
            return repo.save(ex);
        }).orElse(null);
    }

    @Override
    public String deleteTransaction(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return "Deleted transaction id: " + id;
        }
        return "Transaction id not found: " + id;
    }

    // =========================
    // Statement (DTO) search
    // =========================
    @Override
    public List<TransactionStatementDTO> searchStatement(
            String accountNumber,
            String type,
            String from,
            String to,
            String sortBy,
            String direction,
            Integer page,
            Integer size
    ) {
        String effectiveType = normalizeType(type); // ALL/DEBIT/CREDIT
        Date fromTs = parseFrom(from); // -> Date or null
        Date toTs   = parseTo(to);     // -> Date or null (exclusive if date-only)

        Sort sort = buildSort(sortBy, direction);
        Pageable pageable = PageRequest.of(
                page != null && page >= 0 ? page : 0,
                size != null && size > 0 ? size : 50,
                sort
        );

        List<TransactionStatement> items =
                repo.searchForAccount(accountNumber, effectiveType, fromTs, toTs, pageable);

        return items.stream()
                .map(t -> toDto(t, accountNumber))
                .collect(Collectors.toList());
    }

    // ---- helpers ----
    private String normalizeType(String type) {
        if (type == null) return "ALL";
        String t = type.trim().toUpperCase(Locale.ROOT);
        return (t.equals("DEBIT") || t.equals("CREDIT")) ? t : "ALL";
    }

    private Date parseFrom(String from) {
        if (from == null || from.isBlank()) return null;
        try {
            if (from.length() <= 10) {
                LocalDate d = LocalDate.parse(from); // yyyy-MM-dd
                return toDate(d.atStartOfDay());
            }
            LocalDateTime dt = LocalDateTime.parse(from); // yyyy-MM-dd'T'HH:mm:ss
            return toDate(dt);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid 'from' format. Use yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    private Date parseTo(String to) {
        if (to == null || to.isBlank()) return null;
        try {
            if (to.length() <= 10) {
                LocalDate d = LocalDate.parse(to);
                return toDate(d.plusDays(1).atStartOfDay()); // exclusive next day 00:00
            }
            LocalDateTime dt = LocalDateTime.parse(to);
            return toDate(dt); // treat provided datetime as exclusive upper bound instant
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid 'to' format. Use yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    private Sort buildSort(String sortBy, String direction) {
        String field = (sortBy == null || sortBy.isBlank()) ? "transactionDate" : sortBy;
        Sort.Direction dir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Set<String> allowed = Set.of("transactionDate", "amount", "paymentMethod", "status", "transactionId");
        if (!allowed.contains(field)) field = "transactionDate";
        return Sort.by(dir, field);
    }

    // Convert LocalDateTime/LocalDate -> java.util.Date
    private Date toDate(LocalDateTime ldt) {
        return java.sql.Timestamp.valueOf(ldt);
    }
    private Date toDate(LocalDate ld) {
        return toDate(ld.atStartOfDay());
    }

    private TransactionStatementDTO toDto(TransactionStatement t, String accountNumber) {
        TransactionStatementDTO dto = new TransactionStatementDTO();
        dto.setTransactionId(t.getTransactionId());
        dto.setAmount(t.getAmount());

        if (t.getTransactionDate() != null) {
            dto.setTransactionDate(
                t.getTransactionDate().toInstant()
                  .atZone(ZoneId.systemDefault()).toLocalDateTime()
            );
        }

        dto.setPaymentMethod(t.getPaymentMethod());
        dto.setStatus(t.getStatus());
        dto.setRemarks(t.getRemarks());

        boolean outgoing = accountNumber != null && accountNumber.equals(t.getSenderAccountNo());
        dto.setTxnType(outgoing ? "DEBIT" : "CREDIT");
        dto.setCounterpartyAccount(outgoing ? t.getReceiverAccountNo() : t.getSenderAccountNo());
        return dto;
    }
    
    @Override
    public List<TransactionStatementDTO> getLatestStatementDTO(String accountNumber, int count) {
        return searchStatement(accountNumber, "ALL", null, null, "transactionDate", "DESC", 0, count);
    }

}
