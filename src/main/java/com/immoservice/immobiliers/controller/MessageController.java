package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.Message;
import com.immoservice.immobiliers.services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Créer un message
    @PostMapping("/envoyer")
    public ResponseEntity<Message> envoyerMessage(
            @RequestParam String contenu,
            @RequestParam Integer expediteurId,
            @RequestParam Integer destinataireId) {
        Message message = messageService.envoyerMessage(contenu, expediteurId, destinataireId);
        return ResponseEntity.ok(message);
    }

    // Tous les messages
    @GetMapping
    public ResponseEntity<List<Message>> getTousMessages() {
        return ResponseEntity.ok(messageService.getTousMessages());
    }

    // Message par ID
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageParId(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessageParId(id));
    }

    // Messages envoyés par un utilisateur
    @GetMapping("/envoyes/{expediteurId}")
    public ResponseEntity<List<Message>> getMessagesEnvoyes(@PathVariable Integer expediteurId) {
        return ResponseEntity.ok(messageService.getMessagesEnvoyes(expediteurId));
    }

    // Messages reçus par un utilisateur
    @GetMapping("/recus/{destinataireId}")
    public ResponseEntity<List<Message>> getMessagesRecus(@PathVariable Integer destinataireId) {
        return ResponseEntity.ok(messageService.getMessagesRecus(destinataireId));
    }

    // Supprimer un message
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerMessage(@PathVariable Long id) {
        messageService.supprimerMessage(id);
        return ResponseEntity.noContent().build();
    }
}
