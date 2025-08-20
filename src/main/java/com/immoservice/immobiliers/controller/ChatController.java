package com.immoservice.immobiliers.controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.immoservice.immobiliers.entities.Message;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(Message message) {
         // Utilise l'ID du destinataire pour router le message
        if (message.getDestinataire() != null && message.getDestinataire().getId() != null) {
            messagingTemplate.convertAndSend("/queue/user-" + message.getDestinataire().getId(), message);
        }
    }
}