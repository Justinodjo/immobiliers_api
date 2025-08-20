package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.TicketSupport;
import com.immoservice.immobiliers.services.TicketSupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketSupportController {

    private final TicketSupportService ticketSupportService;

    public TicketSupportController(TicketSupportService ticketSupportService) {
        this.ticketSupportService = ticketSupportService;
    }

    @PostMapping("/creer/{userId}")
    public ResponseEntity<TicketSupport> creerTicket(@PathVariable Integer userId, @RequestBody TicketSupport ticket) {
        return ResponseEntity.ok(ticketSupportService.creerTicket(userId, ticket));
    }

    @GetMapping("/tous")
    public ResponseEntity<List<TicketSupport>> getTousLesTickets() {
        return ResponseEntity.ok(ticketSupportService.getTousLesTickets());
    }

    @GetMapping("/utilisateur/{userId}")
    public ResponseEntity<List<TicketSupport>> getTicketsParUtilisateur(@PathVariable Integer userId) {
        return ResponseEntity.ok(ticketSupportService.getTicketsParUtilisateur(userId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<TicketSupport>> getTicketsParStatut(@PathVariable String statut) {
        return ResponseEntity.ok(ticketSupportService.getTicketsParStatut(statut));
    }

    @PutMapping("/{ticketId}/statut")
    public ResponseEntity<TicketSupport> mettreAJourStatut(@PathVariable Long ticketId, @RequestParam String statut) {
        return ResponseEntity.ok(ticketSupportService.mettreAJourStatut(ticketId, statut));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> supprimerTicket(@PathVariable Long ticketId) {
        ticketSupportService.supprimerTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
