package com.globalwallet.api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.globalwallet.api.dto.BalanceResponseDTO;
import com.globalwallet.api.dto.TransactionRequestDTO;
import com.globalwallet.api.model.Card;
import com.globalwallet.api.model.Transaction;
import com.globalwallet.api.model.TransactionType;
import com.globalwallet.api.model.User;
import com.globalwallet.api.repository.CardRepository;
import com.globalwallet.api.repository.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final CardRepository cardRepository;

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
        
        // SALVANDO O NOVO CAMPO: PIX, ACCOUNT ou CARD
        transaction.setPaymentMethod(dto.paymentMethod()); 
        
        transaction.setUser(user);

        // --- Lógica de Cartão ---
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

        // --- Se apagar uma transação de cartão, devolve o limite ---
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

        // --- Desfaz a transação antiga no cartão antes de aplicar a nova ---
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
        
        // ATUALIZANDO O NOVO CAMPO: PIX, ACCOUNT ou CARD
        existingTransaction.setPaymentMethod(dto.paymentMethod());

        // --- Aplica a nova configuração de cartão ---
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

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME && t.getCard() == null)
                .map(t -> t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCard() == null)
                .map(t -> t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new BalanceResponseDTO(totalIncome, totalExpense, balance);
    }
}