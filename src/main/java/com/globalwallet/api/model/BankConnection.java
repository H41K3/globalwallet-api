package com.globalwallet.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "bank_connections")
@Entity(name = "BankConnection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Esse é o ID que a Pluggy vai nos dar
    private String pluggyItemId;

    // Nome da instituição (ex: Nubank, Itaú) - ajuda no seu controle
    private String institutionName;

    // Relacionamento: Muitas conexões para um usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();
}
