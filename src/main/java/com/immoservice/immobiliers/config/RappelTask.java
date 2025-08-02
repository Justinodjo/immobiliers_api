// package com.immoservice.immobiliers.config;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;

// import com.immoservice.immobiliers.entities.Contrats;
// import com.immoservice.immobiliers.repository.ContratsRepository;
// import com.immoservice.immobiliers.services.NotificationService;

// public class RappelTask {
// @Autowired
// private NotificationService notificationService;
// @Autowired
// private ContratsRepository contratsRepository;

// @Scheduled(cron = "0 0 0 * * *")
// public void envoyerRappels() {
// // Récupérer les contrats
// List<Contrats> contrats = contratsRepository.findAll();
// for (Contrats contrat : contrats) {
// // Vérifier si le contrat est proche de son échéance
// if (contrat.estEcheanceProchaine(30)) {
// // 30 jours avant l'échéance
// notificationService.envoyerNotifications(contrat.getBiensImmobiliers().getUserInfo(),
// "Echéance prochaine pour le contrat du bien " +
// contrat.getBiensImmobiliers().getNom());
// }
// }
// }
// }
