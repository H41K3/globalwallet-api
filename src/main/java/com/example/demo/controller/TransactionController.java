package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TransactionRequestDTO;
import com.example.demo.model.Transaction;
import com.example.demo.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    // Agora o Controlador fala apenas com o Service, e não mais com o Repository
    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        // O controlador apenas repassa o pedido
        return service.getAllTransactions();
    }

    @PostMapping
    public Transaction createTransaction(@Valid @RequestBody TransactionRequestDTO dto) {
        // Toda aquela lógica gigante de criar transação sumiu daqui!
        return service.createTransaction(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        service.deleteTransaction(id);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequestDTO dto) {
        return service.updateTransaction(id, dto);
    }
}