package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.model.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionRequestDTO(
    @NotBlank(message = "A descrição é obrigatória")
    String description,

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    BigDecimal amount,

    @NotNull(message = "A data é obrigatória")
    LocalDate transactionDate,

    @NotNull(message = "O tipo (INCOME/EXPENSE) é obrigatório")
    TransactionType type
) {}