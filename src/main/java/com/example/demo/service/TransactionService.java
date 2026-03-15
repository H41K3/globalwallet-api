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

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions(User user) {
        // Agora busca apenas as transações do usuário logado
        return repository.findAllByUser(user);
    }

    public Transaction getTransactionById(Long id, User user) {
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada com o ID: " + id));
        
        // Barreira de Segurança: Impede que um usuário acesse transações de outro
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
        
        // Vincula o usuário logado como dono da transação
        transaction.setUser(user);

        return repository.save(transaction);
    }

    public void deleteTransaction(Long id, User user) {
        // Reutiliza a nossa barreira de segurança para garantir que ele é o dono
        Transaction transaction = getTransactionById(id, user);
        repository.delete(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionRequestDTO dto, User user) {
        // Reutiliza a barreira de segurança
        Transaction existingTransaction = getTransactionById(id, user);

        existingTransaction.setDescription(dto.description());
        existingTransaction.setAmount(dto.amount());
        existingTransaction.setTransactionDate(dto.transactionDate());
        existingTransaction.setType(dto.type()); 

        return repository.save(existingTransaction);
    }

    public BalanceResponseDTO getBalanceSummary(User user) {
        // Calcula o balanço apenas com as transações do usuário logado
        List<Transaction> transactions = repository.findAllByUser(user);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if (t.getType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(t.getAmount());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new BalanceResponseDTO(totalIncome, totalExpense, balance);
    }
}