package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.Suggestion;
import com.immoservice.immobiliers.repository.SuggestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;

    public SuggestionService(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findAll();
    }

    public Optional<Suggestion> getSuggestionById(Long id) {
        return suggestionRepository.findById(id);
    }

    public Suggestion createSuggestion(Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    public Suggestion updateSuggestion(Long id, Suggestion suggestionDetails) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suggestion not found"));

        suggestion.setMessage(suggestionDetails.getMessage());
        suggestion.setRead(suggestionDetails.isRead());
        suggestion.setUserEmail(suggestionDetails.getUserEmail());

        return suggestionRepository.save(suggestion);
    }

    public void deleteSuggestion(Long id) {
        suggestionRepository.deleteById(id);
    }
}
