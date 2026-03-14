package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;

// A anotação @Service avisa ao Java que esta classe contém as regras de negócio
@Service
public class TransactionService {

    private final TransactionRepository repository;

    // O Service precisa acessar a Despensa (Repository)
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction createTransaction(TransactionRequestDTO dto) {
        // A lógica de conversão do DTO agora mora aqui no Chef
        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setTransactionDate(dto.transactionDate());

        return repository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        repository.deleteById(id);
    }

    public Transaction updateTransaction(Long id, TransactionRequestDTO dto) {
        Transaction existingTransaction = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o ID: " + id));

        existingTransaction.setDescription(dto.description());
        existingTransaction.setAmount(dto.amount());
        existingTransaction.setTransactionDate(dto.transactionDate());

        return repository.save(existingTransaction);
    }
}