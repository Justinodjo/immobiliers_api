package com.immoservice.immobiliers.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Contrats;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.TypeContrat;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.enums.ValidationStatus;

@Repository
public interface ContratsRepository extends JpaRepository<Contrats, Long> {

    int countByBiensImmobiliers_Id(Long id);

    List<Contrats> findByUserInfoEmail(String email);

    List<Contrats> findByUserInfo(UserInfo user);

    List<Contrats> findByTypeContrat(TypeContrat typeContrat);

    // Récupère les contrats d’un utilisateur spécifique (locataire)
    List<Contrats> findByUserInfoId(Long userId);

    // (optionnel) Récupère aussi selon le gestionnaire
    List<Contrats> findByGestionnaireId(Long gestionnaireId);

    List<Contrats> findByStatus(String status);

    List<Contrats> findByGestionnaire(UserInfo gestionnaire);

    List<Contrats> findByDateFinBeforeAndStatusNot(LocalDate dateFin, StatusContrat status);

    List<Contrats> findByUserInfoAndStatus(UserInfo userInfo, StatusContrat status);

    List<Contrats> findByBiensImmobiliersAndDateFinAfter(BiensImmobiliers biensImmobiliers, LocalDate dateFin);

    @Query("SELECT DISTINCT b FROM Contrats c JOIN c.biensImmobiliers b " +
            "WHERE c.status = :status " +
            "AND :now BETWEEN c.dateDebut AND c.dateFin")
    List<BiensImmobiliers> findBiensImmobiliersLoues(@Param("now") LocalDate now,
            @Param("status") StatusContrat status);

    // Récupérer les biens immobiliers loués par un utilisateur spécifique et ayant
    // un contrat actif
    @Query("SELECT DISTINCT b FROM Contrats c JOIN c.biensImmobiliers b " +
            "WHERE c.userInfo = :userInfo " +
            "AND c.status = :status " +
            "AND :now BETWEEN c.dateDebut AND c.dateFin")
    List<BiensImmobiliers> findBiensImmobiliersLouesParUtilisateur(@Param("userInfo") UserInfo userInfo,
            @Param("status") StatusContrat status,
            @Param("now") LocalDate now);

    @Query("SELECT c FROM Contrats c WHERE (:type IS NULL OR c.typeContrat = :type) AND (:status IS NULL OR c.status = :status) AND (:dateDebutMin IS NULL OR c.dateDebut >= :dateDebutMin) AND (:dateFinMax IS NULL OR c.dateFin <= :dateFinMax)")
    List<Contrats> findContrats(@Param("type") TypeContrat type, @Param("status") StatusContrat status,
            @Param("dateDebutMin") LocalDate dateDebutMin, @Param("dateFinMax") LocalDate dateFinMax);

    List<Contrats> findByStatus(StatusContrat expire);

    List<Contrats> findByValidationStatus(ValidationStatus enAttente);

    int countByDetailsBiensImmobiliers_Id(Long id);

    String findByBiensImmobiliersAndStatus(BiensImmobiliers b, StatusContrat actif);

    long countByStatus(StatusContrat expire);

    /**
     * Trouve les contrats actifs pour un bien dans une période donnée
     */
    @Query("SELECT c FROM Contrats c " +
            "JOIN c.biensImmobiliers b " +
            "WHERE b.id = :bienId " +
            "AND c.status = 'ACTIF' " +
            "AND (" +
            "  (c.dateDebut <= :dateFin AND c.dateFin >= :dateDebut) " +
            ")")
    List<Contrats> findContratsByBienAndDateRange(
            @Param("bienId") Long bienId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les contrats actifs pour un détail dans une période donnée
     */
    @Query("SELECT c FROM Contrats c " +
            "JOIN c.detailsBiensImmobiliers d " +
            "WHERE d.id = :detailId " +
            "AND c.status = 'ACTIF' " +
            "AND (" +
            "  (c.dateDebut <= :dateFin AND c.dateFin >= :dateDebut) " +
            ")")
    List<Contrats> findContratsByDetailAndDateRange(
            @Param("detailId") Long detailId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    /**
     * Vérifie si un bien est disponible
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN false ELSE true END " +
            "FROM Contrats c " +
            "JOIN c.biensImmobiliers b " +
            "WHERE b.id = :bienId " +
            "AND c.status = 'ACTIF' " +
            "AND c.dateDebut <= :dateFin " +
            "AND c.dateFin >= :dateDebut")
    boolean isBienDisponible(
            @Param("bienId") Long bienId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    /**
     * Vérifie si un détail est disponible
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN false ELSE true END " +
            "FROM Contrats c " +
            "JOIN c.detailsBiensImmobiliers d " +
            "WHERE d.id = :detailId " +
            "AND c.status = 'ACTIF' " +
            "AND c.dateDebut <= :dateFin " +
            "AND c.dateFin >= :dateDebut")
    boolean isDetailDisponible(
            @Param("detailId") Long detailId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    /**
     * Récupère tous les contrats d'un utilisateur
     */
    @Query("SELECT c FROM Contrats c WHERE c.userInfo.id = :userId ORDER BY c.dateDebut DESC")
    List<Contrats> findByUserId(@Param("userId") Long userId);

    int countByGestionnaire(UserInfo gestionnaire);
    
    int countByGestionnaireId(Integer gestionnaireId);

}