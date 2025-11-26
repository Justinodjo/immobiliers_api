package com.immoservice.immobiliers.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.ModePaiement;
import com.immoservice.immobiliers.entities.Paiements;
import com.immoservice.immobiliers.entities.StatutPaiement;

@Repository
public interface PaiementsRepository extends JpaRepository<Paiements, Long>, JpaSpecificationExecutor<Paiements> {
       // 🔹 Permet au callback FedaPay de retrouver le bon paiement
       Paiements findByTransactionFedapayId(String transactionFedapayId);

       // Recherche par référence
       Optional<Paiements> findByReference(String reference);

       // Recherche par statut
       List<Paiements> findByStatut(StatutPaiement statut);

       // ✅ Recherche par locataire via Contrats → UserInfo
       List<Paiements> findByContrats_UserInfo_Id(Long locataireId);

       List<Paiements> findByContrats_UserInfo_IdAndStatut(Long locataireId, StatutPaiement statut);

       // ✅ Recherche par bien via Contrats → DetailsBiensImmobiliers →
       // BiensImmobiliers

       List<Paiements> findByContrats_DetailsBiensImmobiliers_BiensImmobiliers_IdAndStatut(Long bienId,
                     StatutPaiement statut);

       // Recherche par contrat
       List<Paiements> findByContrats_Id(Long contratId);

       // Recherche par date d'échéance
       List<Paiements> findByDateEcheance(LocalDate dateEcheance);

       List<Paiements> findByDateEcheanceBetween(LocalDate dateDebut, LocalDate dateFin);

       @Query("SELECT p FROM Paiements p WHERE p.dateEcheance < :date AND p.statut = 'EN_ATTENTE'")
       List<Paiements> findPaiementsEnRetard(@Param("date") LocalDate date);

       // Recherche par date de paiement
       List<Paiements> findByDatePaiement(LocalDate datePaiement);

       List<Paiements> findByDatePaiementBetween(LocalDate dateDebut, LocalDate dateFin);

       // Recherche par mode de paiement
       List<Paiements> findByModePaiement(ModePaiement modePaiement);

       // Statistiques
       @Query("SELECT COUNT(p) FROM Paiements p WHERE p.statut = :statut")
       long countByStatut(@Param("statut") StatutPaiement statut);

       @Query("SELECT SUM(p.montant) FROM Paiements p WHERE p.statut = 'PAYE'")
       Double sumMontantPayes();

       @Query("SELECT SUM(p.montant) FROM Paiements p WHERE p.statut = 'EN_ATTENTE'")
       Double sumMontantEnAttente();

       @Query("SELECT SUM(p.montant) FROM Paiements p WHERE p.statut = 'EN_RETARD'")
       Double sumMontantEnRetard();

       @Query("SELECT SUM(p.montantPaye) FROM Paiements p WHERE p.datePaiement BETWEEN :dateDebut AND :dateFin")
       Double sumMontantPayesBetween(@Param("dateDebut") LocalDate dateDebut,
                     @Param("dateFin") LocalDate dateFin);

       // Recherche combinée
       @Query("""
                         SELECT p FROM Paiements p
                         JOIN p.contrats c
                         LEFT JOIN c.userInfo u
                         LEFT JOIN c.detailsBiensImmobiliers d
                         LEFT JOIN d.biensImmobiliers b
                         WHERE (:locataireId IS NULL OR u.id = :locataireId)
                         AND (:bienId IS NULL OR b.id = :bienId)
                         AND (:statut IS NULL OR p.statut = :statut)
                         AND (:dateDebut IS NULL OR p.dateEcheance >= :dateDebut)
                         AND (:dateFin IS NULL OR p.dateEcheance <= :dateFin)
                     """)
       List<Paiements> findPaiementsWithFilters(
                     @Param("locataireId") Long locataireId,
                     @Param("bienId") Long bienId,
                     @Param("statut") StatutPaiement statut,
                     @Param("dateDebut") LocalDate dateDebut,
                     @Param("dateFin") LocalDate dateFin);

       // Paiements récents
       @Query("SELECT p FROM Paiements p ORDER BY p.createdAt DESC")
       List<Paiements> findRecentPaiements();

       // Paiements à échéance proche
       @Query("SELECT p FROM Paiements p WHERE p.dateEcheance BETWEEN :dateDebut AND :dateFin " +
                     "AND p.statut = 'EN_ATTENTE' ORDER BY p.dateEcheance ASC")
       List<Paiements> findPaiementsEcheanceProche(
                     @Param("dateDebut") LocalDate dateDebut,
                     @Param("dateFin") LocalDate dateFin);

       // Paiements par mois
       @Query("SELECT p FROM Paiements p WHERE YEAR(p.dateEcheance) = :annee " +
                     "AND MONTH(p.dateEcheance) = :mois")
       List<Paiements> findByMois(@Param("annee") int annee, @Param("mois") int mois);

       // Paiements nécessitant un rappel
       @Query("SELECT p FROM Paiements p WHERE p.statut IN ('EN_ATTENTE', 'EN_RETARD') " +
                     "AND (p.dateDernierRappel IS NULL OR p.dateDernierRappel < :dateMin)")
       List<Paiements> findPaiementsNecessitantRappel(@Param("dateMin") java.time.LocalDateTime dateMin);

       @Query("SELECT p FROM Paiements p JOIN p.contrats c JOIN c.biensImmobiliers b WHERE b.id = :bienId")
       List<Paiements> findByBienImmobilierId(@Param("bienId") Long bienId);

       @Query("SELECT p FROM Paiements p JOIN p.contrats c JOIN c.detailsBiensImmobiliers d WHERE d.id = :detailId")
       List<Paiements> findByDetailsBienId(@Param("detailId") Long detailId);

       List<Paiements> findByContrats_BiensImmobiliers_Id(Long bienId);

}