package com.globalwallet.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.globalwallet.api.dto.CardRequestDTO;
import com.globalwallet.api.model.Card;
import com.globalwallet.api.model.User;
import com.globalwallet.api.repository.CardRepository;

@Service
public class CardService {

    private final CardRepository repository;

    public CardService(CardRepository repository) {
        this.repository = repository;
    }

    public List<Card> getAllCards(User user) {
        return repository.findAllByUser(user);
    }

    public Card getCardById(Long id, User user) {
        return repository.findByIdAndUser(id, user)
                // Usando RuntimeException padrão do Java
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado para este usuário."));
    }

    @Transactional
    public Card createCard(CardRequestDTO dto, User user) {
        // Agora o construtor tem todos os 9 parâmetros que a entidade exige
        Card card = new Card(
            user,
            dto.name(),
            dto.lastDigits(),
            dto.totalLimit(),
            dto.color(),
            dto.flag(),
            dto.type(),
            dto.closingDate(),
            dto.dueDate()
        );
        return repository.save(card);
    }

    @Transactional
    public Card updateCard(Long id, CardRequestDTO dto, User user) {
        Card card = getCardById(id, user);
        
        card.setName(dto.name());
        card.setLastDigits(dto.lastDigits());
        card.setTotalLimit(dto.totalLimit());
        card.setColor(dto.color());
        
        // Atualizando os novos campos na edição
        card.setFlag(dto.flag());
        card.setType(dto.type());
        card.setClosingDate(dto.closingDate());
        card.setDueDate(dto.dueDate());
        
        return repository.save(card);
    }

    @Transactional
    public void deleteCard(Long id, User user) {
        Card card = getCardById(id, user);
        repository.delete(card);
    }
}