package com.example.demo.dto;

import java.math.BigDecimal;

public record BalanceResponseDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {};