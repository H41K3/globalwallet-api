package com.globalwallet.api.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PluggyService {

    @Value("${pluggy.client.id}")
    private String clientId;

    @Value("${pluggy.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public PluggyService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Passo 1: Pegar a API Key (Token de acesso geral da sua conta)
     */
    private String getApiKey() {
        String url = "https://api.pluggy.ai/auth";

        Map<String, String> body = new HashMap<>();
        body.put("clientId", clientId);
        body.put("clientSecret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("apiKey")) {
            return response.getBody().get("apiKey").toString();
        }
        throw new RuntimeException("Erro ao gerar API Key da Pluggy");
    }

    /**
     * Passo 2: Pegar o Connect Token (Token temporário que o Front-end vai
     * usar)
     */
    public String getConnectToken() {
        String apiKey = getApiKey();
        String url = "https://api.pluggy.ai/connect_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        // O corpo pode ser vazio, mas precisamos mandar para a requisição POST funcionar
        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("accessToken")) {
            return response.getBody().get("accessToken").toString();
        }
        throw new RuntimeException("Erro ao gerar Connect Token da Pluggy");
    }
}
