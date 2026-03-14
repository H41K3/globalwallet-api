package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionRepository repository;

    // Injeção de dependência via construtor
    public TransactionController(TransactionRepository repository) {
        this.repository = repository;
    }

    // Endpoint para LISTAR todas as transações (GET)
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    // Endpoint para SALVAR uma nova transação (POST)
    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return repository.save(transaction);
    }
}