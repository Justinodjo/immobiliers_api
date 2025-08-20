package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.TicketSupport;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.TicketSupportRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketSupportService {

    private final TicketSupportRepository ticketSupportRepository;
    private final UserInfoRepository userInfoRepository;

    public TicketSupportService(TicketSupportRepository ticketSupportRepository,
            UserInfoRepository userInfoRepository) {
        this.ticketSupportRepository = ticketSupportRepository;
        this.userInfoRepository = userInfoRepository;
    }

    public TicketSupport creerTicket(Integer userId, TicketSupport ticket) {
        UserInfo createur = userInfoRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec ID : " + userId));
        ticket.setCreateur(createur);
        ticket.setStatut("OUVERT");
        return ticketSupportRepository.save(ticket);
    }

    public List<TicketSupport> getTousLesTickets() {
        return ticketSupportRepository.findAll();
    }

    public List<TicketSupport> getTicketsParUtilisateur(Integer userId) {
        return ticketSupportRepository.findByCreateurId(userId);
    }

    public List<TicketSupport> getTicketsParStatut(String statut) {
        return ticketSupportRepository.findByStatut(statut);
    }

    public TicketSupport mettreAJourStatut(Long ticketId, String statut) {
        TicketSupport ticket = ticketSupportRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé avec ID : " + ticketId));
        ticket.setStatut(statut);
        return ticketSupportRepository.save(ticket);
    }

    public void supprimerTicket(Long ticketId) {
        if (!ticketSupportRepository.existsById(ticketId)) {
            throw new RuntimeException("Ticket non trouvé avec ID : " + ticketId);
        }
        ticketSupportRepository.deleteById(ticketId);
    }
}
