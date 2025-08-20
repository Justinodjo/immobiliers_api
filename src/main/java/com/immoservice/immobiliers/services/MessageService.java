package com.immoservice.immobiliers.services;

import com.immoservice.immobiliers.entities.Message;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.MessageRepository;
import com.immoservice.immobiliers.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserInfoRepository userInfoRepository;

    public MessageService(MessageRepository messageRepository, UserInfoRepository userInfoRepository) {
        this.messageRepository = messageRepository;
        this.userInfoRepository = userInfoRepository;
    }

    // Créer un message
    public Message envoyerMessage(String contenu, Integer expediteurId, Integer destinataireId) {
        UserInfo expediteur = userInfoRepository.findById(expediteurId)
                .orElseThrow(() -> new RuntimeException("Expéditeur introuvable avec ID : " + expediteurId));
        UserInfo destinataire = userInfoRepository.findById(destinataireId)
                .orElseThrow(() -> new RuntimeException("Destinataire introuvable avec ID : " + destinataireId));

        Message message = new Message();
        message.setContenu(contenu);
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setDateEnvoi(LocalDateTime.now());

        return messageRepository.save(message);
    }

    // Récupérer tous les messages
    public List<Message> getTousMessages() {
        return messageRepository.findAll();
    }

    // Récupérer message par ID
    public Message getMessageParId(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message introuvable avec ID : " + id));
    }

    // Récupérer messages envoyés par un utilisateur
    public List<Message> getMessagesEnvoyes(Integer expediteurId) {
        return messageRepository.findByExpediteurId(expediteurId);
    }

    // Récupérer messages reçus par un utilisateur
    public List<Message> getMessagesRecus(Integer destinataireId) {
        return messageRepository.findByDestinataireId(destinataireId);
    }

    // Supprimer message
    public void supprimerMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new RuntimeException("Message introuvable avec ID : " + id);
        }
        messageRepository.deleteById(id);
    }
}
