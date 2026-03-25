package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.BalanceResponseDTO;
import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionType;
import com.example.demo.model.User;
import com.example.demo.repository.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions(User user) {
        return repository.findAllByUser(user);
    }

    public Transaction getTransactionById(Long id, User user) {
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transação não encontrada com o ID: " + id));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso Negado: Esta transação não pertence a você.");
        }

        return transaction;
    }

    public Transaction createTransaction(TransactionRequestDTO dto, User user) {
        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setType(dto.type());
        
        // --- Adicionando a Categoria ---
        transaction.setCategory(dto.category()); 
        
        transaction.setUser(user);

        return repository.save(transaction);
    }

    public void deleteTransaction(Long id, User user) {
        Transaction transaction = getTransactionById(id, user);
        repository.delete(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionRequestDTO dto, User user) {
        Transaction existingTransaction = getTransactionById(id, user);

        existingTransaction.setDescription(dto.description());
        existingTransaction.setAmount(dto.amount());
        existingTransaction.setTransactionDate(dto.transactionDate());
        existingTransaction.setType(dto.type());
        
        // --- Atualizando a Categoria ---
        existingTransaction.setCategory(dto.category());

        return repository.save(existingTransaction);
    }

    public BalanceResponseDTO getBalanceSummary(User user) {
        List<Transaction> transactions = repository.findAllByUser(user);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new BalanceResponseDTO(totalIncome, totalExpense, balance);
    }
}