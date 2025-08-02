package com.immoservice.immobiliers.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class PaiementService {
    private static final String API_KEY = "sk_live_uKcXC79oLvQReXGou2S691ps";
    private static final String API_URL = "https://api.fedapay.com/v1/transactions";

    @SuppressWarnings("unchecked")
    public String initierPaiement(double montant, String email, String nom) {
        // Validation des entrées
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("L'email est invalide");
        }

        RestTemplate restTemplate = new RestTemplate();

        // Corps de la requête
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", montant);
        requestBody.put("currency", "XOF");
        requestBody.put("description", "Paiement pour réservation");
        requestBody.put("callback_url", "https://votre-domaine.com/success"); // URL de confirmation
        requestBody.put("cancel_url", "https://votre-domaine.com/cancel"); // URL en cas d'annulation

        // Ajout des informations du client
        Map<String, Object> customer = new HashMap<>();
        customer.put("firstname", nom);
        customer.put("email", email);
        requestBody.put("customer", customer);

        // Création de la requête HTTP avec l'en-tête d'autorisation
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);

            System.out.println("Réponse complète de FedaPay : " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (!responseBody.containsKey("transaction")) {
                    System.err.println("Réponse FedaPay invalide : pas de clé 'transaction'.");
                    return "Erreur : FedaPay n'a pas retourné de transaction.";
                }

                Map<String, Object> transaction = (Map<String, Object>) responseBody.get("transaction");
                System.out.println("Transaction reçue : " + transaction);

                if (transaction.get("url") == null) {
                    System.err.println("Erreur : L'URL de paiement est absente.");
                    return "Erreur : L'URL de paiement est absente.";
                }

                return (String) transaction.get("url"); // Retourne l'URL de paiement
            } else {
                System.err.println("Erreur HTTP : " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Erreur avec FedaPay: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Erreur inconnue. Veuillez réessayer plus tard.";

    }
}
