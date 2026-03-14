package com.example.demo.controller;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.service.TransactionService;

// Usando a SUA anotação que comprovadamente funciona na sua máquina!
@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService service; // O Chef de mentira

    @Test
    public void shouldReturnOkWhenGettingAllTransactions() throws Exception {
        // Ensina o Chef de mentira a retornar uma lista vazia quando perguntarem
        Mockito.when(service.getAllTransactions()).thenReturn(Collections.emptyList());

        // Chama a API e garante que o Garçom atende bem e responde 200 OK
        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk());
    }
}