package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.BalanceResponseDTO;
import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.model.Card;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionType;
import com.example.demo.model.User;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final CardRepository cardRepository; // NOVO: Injetando repositório de cartões

    public TransactionService(TransactionRepository repository, CardRepository cardRepository) {
        this.repository = repository;
        this.cardRepository = cardRepository;
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

    @Transactional
    public Transaction createTransaction(TransactionRequestDTO dto, User user) {
        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setType(dto.type());
        transaction.setCategory(dto.category()); 
        transaction.setUser(user);

        // --- NOVO: Lógica de Cartão ---
        if (dto.cardId() != null) {
            Card card = cardRepository.findByIdAndUser(dto.cardId(), user)
                    .orElseThrow(() -> new RuntimeException("Cartão não encontrado."));
            transaction.setCard(card);
            
            // Se for saída, soma na fatura do cartão
            if (dto.type() == TransactionType.EXPENSE) {
                card.setCurrentInvoice(card.getCurrentInvoice() + dto.amount().doubleValue());
                cardRepository.save(card);
            }
        }

        return repository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = getTransactionById(id, user);

        // --- NOVO: Se apagar uma transação de cartão, devolve o limite ---
        if (transaction.getCard() != null && transaction.getType() == TransactionType.EXPENSE) {
            Card card = transaction.getCard();
            card.setCurrentInvoice(card.getCurrentInvoice() - transaction.getAmount().doubleValue());
            cardRepository.save(card);
        }

        repository.delete(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionRequestDTO dto, User user) {
        Transaction existingTransaction = getTransactionById(id, user);

        // --- NOVO: Desfaz a transação antiga no cartão antes de aplicar a nova ---
        if (existingTransaction.getCard() != null && existingTransaction.getType() == TransactionType.EXPENSE) {
            Card oldCard = existingTransaction.getCard();
            oldCard.setCurrentInvoice(oldCard.getCurrentInvoice() - existingTransaction.getAmount().doubleValue());
            cardRepository.save(oldCard);
        }

        existingTransaction.setDescription(dto.description());
        existingTransaction.setAmount(dto.amount());
        existingTransaction.setTransactionDate(dto.transactionDate());
        existingTransaction.setType(dto.type());
        existingTransaction.setCategory(dto.category());

        // --- NOVO: Aplica a nova configuração de cartão ---
        if (dto.cardId() != null) {
            Card newCard = cardRepository.findByIdAndUser(dto.cardId(), user)
                    .orElseThrow(() -> new RuntimeException("Cartão não encontrado."));
            existingTransaction.setCard(newCard);
            
            if (dto.type() == TransactionType.EXPENSE) {
                newCard.setCurrentInvoice(newCard.getCurrentInvoice() + dto.amount().doubleValue());
                cardRepository.save(newCard);
            }
        } else {
            existingTransaction.setCard(null); // Removeu o cartão da compra
        }

        return repository.save(existingTransaction);
    }

    public BalanceResponseDTO getBalanceSummary(User user) {
        List<Transaction> transactions = repository.findAllByUser(user);

        // --- NOVO: t.getCard() == null garante que compras no cartão NÃO descontem do Saldo Geral ---
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME && t.getCard() == null)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCard() == null)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new BalanceResponseDTO(totalIncome, totalExpense, balance);
    }
}