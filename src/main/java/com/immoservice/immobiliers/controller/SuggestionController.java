package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.Suggestion;
import com.immoservice.immobiliers.services.SuggestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping
    public List<Suggestion> getAllSuggestions() {
        return suggestionService.getAllSuggestions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Suggestion> getSuggestionById(@PathVariable Long id) {
        return suggestionService.getSuggestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Suggestion createSuggestion(@RequestBody Suggestion suggestion) {
        return suggestionService.createSuggestion(suggestion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Suggestion> updateSuggestion(@PathVariable Long id, @RequestBody Suggestion suggestion) {
        try {
            Suggestion updated = suggestionService.updateSuggestion(id, suggestion);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable Long id) {
        suggestionService.deleteSuggestion(id);
        return ResponseEntity.noContent().build();
    }
}
