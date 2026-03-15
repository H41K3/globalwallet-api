package com.example.demo.infra.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResourceExceptionHandler {

    // Captura erros genéricos de lógica (como o "ID não encontrado")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardError> handleRuntime(RuntimeException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
            Instant.now(),
            status.value(),
            "Erro na solicitação",
            e.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    // NOVO MÉTODO: Captura especificamente erros de validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Pega apenas a primeira mensagem de erro para manter o JSON limpo
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        
        StandardError err = new StandardError(
            Instant.now(),
            status.value(),
            "Erro de validação",
            errorMessage,
            request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}