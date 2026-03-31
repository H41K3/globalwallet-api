package com.globalwallet.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.globalwallet.api.model.BankConnection;
import com.globalwallet.api.model.User;
import com.globalwallet.api.repository.BankConnectionRepository;
import com.globalwallet.api.repository.UserRepository;
import com.globalwallet.api.service.PluggyService;

@RestController
@RequestMapping("/api/v1/pluggy")
@CrossOrigin(origins = "*")
public class PluggyController {

    private final PluggyService pluggyService;
    private final BankConnectionRepository bankConnectionRepository;
    private final UserRepository userRepository;

    public PluggyController(PluggyService pluggyService, BankConnectionRepository bankConnectionRepository, UserRepository userRepository) {
        this.pluggyService = pluggyService;
        this.bankConnectionRepository = bankConnectionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint 1: O React vai chamar este endpoint para pegar o token
     * temporário e abrir o widget da Pluggy na tela.
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getToken() {
        String connectToken = pluggyService.getConnectToken();

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", connectToken);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 2: O React vai chamar este endpoint após o usuário fazer login
     * no banco com sucesso, passando o ID da conexão. O Java salva no
     * PostgreSQL.
     */
    @PostMapping("/item")
    public ResponseEntity<String> saveBankConnection(@RequestBody Map<String, String> payload) {
        String pluggyItemId = payload.get("pluggyItemId");

        if (pluggyItemId == null || pluggyItemId.isEmpty()) {
            return ResponseEntity.badRequest().body("ID do item não fornecido");
        }

        // Pega o usuário logado via Token JWT
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(loggedInUser.getId()).orElseThrow();

        // Salva a conexão
        BankConnection connection = new BankConnection();
        connection.setPluggyItemId(pluggyItemId);
        connection.setUser(user);
        connection.setInstitutionName("Banco via Pluggy"); // Você pode pegar o nome real depois

        bankConnectionRepository.save(connection);

        return ResponseEntity.ok("Conexão salva com sucesso!");
    }
}
