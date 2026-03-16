package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.BalanceResponseDTO;
import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.model.Transaction;
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

    // ... outros métodos (getAll, create, delete, etc) ...
    public BalanceResponseDTO getBalanceSummary(User user) {
        List<Transaction> transactions = repository.findAllByUser(user);

        // Soma Entradas
        java.math.BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == com.example.demo.model.TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        // Soma Despesas
        java.math.BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == com.example.demo.model.TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        // Calcula Saldo Líquido
        java.math.BigDecimal balance = totalIncome.subtract(totalExpense);

        return new BalanceResponseDTO(totalIncome, totalExpense, balance);
    }
}
