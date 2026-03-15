package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Transaction;
import com.example.demo.model.User;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // O Spring Boot é inteligente o suficiente para ler esse nome e montar o "SELECT * FROM transactions WHERE user_id = ?" sozinho!
    List<Transaction> findAllByUser(User user);
}