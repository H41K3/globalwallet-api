package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// No Java moderno, o "record" é a forma ideal de criar DTOs.
// Ele é leve, imutável e não precisa de Getters/Setters gigantes.
public record TransactionRequestDTO(
        
        @NotBlank(message = "A descrição é obrigatória e não pode estar em branco")
        String description,

        @NotNull(message = "O valor da transação é obrigatório")
        BigDecimal amount,

        @NotNull(message = "A data da transação é obrigatória")
        LocalDate transactionDate
) {}