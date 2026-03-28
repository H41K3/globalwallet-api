package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank
        String fullName,
        @NotBlank
        String cpf,
        @NotBlank
        String email,
        @NotBlank
        String phone,
        @NotBlank
        String password
        ) {

}
