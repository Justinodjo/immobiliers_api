package com.immoservice.immobiliers.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void envoyerEmail(String destinataire, String sujet, String templateName, Map<String, Object> variables) {
        try {
            // Si Thymeleaf n'est pas disponible, utiliser un email simple
            if (templateEngine == null) {
                String contenu = buildSimpleEmailContent(variables);
                envoyerEmailSimple(destinataire, sujet, contenu);
                return;
            }
            // Créer un MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("dev@mainsdorplus.com");

            // Définir le destinataire, sujet, et contenu de l'email
            messageHelper.setTo(destinataire);
            messageHelper.setSubject(sujet);

            // Créer un contexte Thymeleaf
            Context context = new Context();
            context.setVariables((Map<String, Object>) variables); // Injecter les variables dans le contexte

            // Rendre le template Thymeleaf
            String body = templateEngine.process(templateName, context);

            // Définir le corps de l'email
            messageHelper.setText(body, true); // true pour indiquer que le message est en HTML

            // Envoyer l'email
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email HTML à {}: {}", destinataire, e.getMessage());
            // Fallback vers email simple
            try {
                String contenu = buildSimpleEmailContent(variables);
                envoyerEmailSimple(destinataire, sujet, contenu);
            } catch (Exception ex) {
                throw new RuntimeException("Erreur lors de l'envoi de l'email", ex);
            }
        }
    }

    /**
     * Envoyer un email simple (sans template)
     */
    public void envoyerEmailSimple(String destinataire, String sujet, String contenu) {
        try {
            // Créer un MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("dev@mainsdorplus.com");
            messageHelper.setTo(destinataire);
            messageHelper.setSubject(sujet);
            messageHelper.setText(contenu);

            javaMailSender.send(mimeMessage);
            logger.info("Email simple envoyé à {}", destinataire);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email à {}: {}", destinataire, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    /**
     * Construire un contenu d'email simple à partir des variables
     */
    public String buildSimpleEmailContent(Map<String, Object> variables) {
        StringBuilder content = new StringBuilder();

        if (variables.containsKey("userName")) {
            content.append("Bonjour ").append(variables.get("userName")).append(",\n\n");
        }

        if (variables.containsKey("verificationCode")) {
            content.append("Votre code de vérification est : ").append(variables.get("verificationCode"))
                    .append("\n\n");
            content.append("Ce code est valide pendant ").append(variables.get("expirationMinutes"))
                    .append(" minutes.\n\n");
        }

        if (variables.containsKey("resetLink")) {
            content.append("Vous pouvez également cliquer sur ce lien :\n");
            content.append(variables.get("resetLink")).append("\n\n");
        }

        content.append("Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n");
        content.append("Cordialement,\n");
        content.append("L'équipe Hweon");

        return content.toString();
    }

}
