package com.globalwallet.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CardRequestDTO(
        
        @NotBlank(message = "O nome do cartão é obrigatório")
        String name,

        @NotBlank(message = "Os 4 últimos dígitos são obrigatórios")
        String lastDigits,

        @NotNull(message = "O limite do cartão é obrigatório")
        @Positive(message = "O limite deve ser maior que zero")
        Double totalLimit,

        @NotBlank(message = "A cor do cartão é obrigatória")
        String color,

        // --- NOVOS CAMPOS ADICIONADOS AQUI ---
        String flag,
        String type,
        Integer closingDate,
        Integer dueDate
) {
}