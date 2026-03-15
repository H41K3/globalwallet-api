package com.example.demo.controller;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.User;
import com.example.demo.service.TransactionService;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService service;

    @Test
    @WithMockUser
    public void shouldReturnOkWhenGettingAllTransactions() throws Exception {
        // Ensinamos o Mockito a esperar o User que o seu Controller agora exige
        Mockito.when(service.getAllTransactions(ArgumentMatchers.any(User.class)))
               .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/transactions"))
               .andExpect(status().isOk());
    }
}