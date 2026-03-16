package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BalanceResponseDTO;
import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.model.Transaction;
import com.example.demo.model.User;
import com.example.demo.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getAllTransactions(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getTransactionById(id, user));
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransactionRequestDTO dto, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTransaction(dto, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, @Valid @RequestBody TransactionRequestDTO dto, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.updateTransaction(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.deleteTransaction(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponseDTO> getBalance(@AuthenticationPrincipal User user) {
        // Chamada única e limpa para o resumo financeiro
        return ResponseEntity.ok(service.getBalanceSummary(user));
    }
}
