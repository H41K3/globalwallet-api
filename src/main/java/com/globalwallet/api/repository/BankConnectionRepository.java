package com.globalwallet.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.globalwallet.api.model.BankConnection;
import com.globalwallet.api.model.User;

public interface BankConnectionRepository extends JpaRepository<BankConnection, Long> {

    // Para buscarmos todas as conexões de um usuário específico
    List<BankConnection> findByUser(User user);

    // Para o Webhook saber de quem é o item que está mandando transação
    BankConnection findByPluggyItemId(String pluggyItemId);
}
