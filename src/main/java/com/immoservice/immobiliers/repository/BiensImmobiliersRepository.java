package com.immoservice.immobiliers.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.immoservice.immobiliers.entities.Arrondissement;
import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Commune;
import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.entities.Quartier;
import com.immoservice.immobiliers.entities.StatusContrat;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;

@Repository
public interface BiensImmobiliersRepository extends JpaRepository<BiensImmobiliers, Long> {
  List<BiensImmobiliers> findByUserInfoEmail(String email);

  List<BiensImmobiliers> findByTypeBiensImmobiliereId(Long typeId);

  Page<BiensImmobiliers> findByTypeBiensImmobiliere(TypeBiensImmobiliere typeBiensImmobiliere, Pageable pageable);

  Page<BiensImmobiliers> findByUserInfo(UserInfo userInfo, Pageable pageable);

  long countByUserInfo(UserInfo userInfo);

  List<BiensImmobiliers> findByUserInfoId(Integer userId);

  // Variante simple
  List<BiensImmobiliers> findByStatusTrueAndAVendreFalse();

  // Variante avancée : exclure les biens liés à un contrat ayant statut donné
  @Query("""
      SELECT b FROM BiensImmobiliers b
      WHERE b.status = true
        AND (b.aVendre = false OR b.aVendre IS NULL)
        AND NOT EXISTS (
            SELECT c FROM Contrats c
            JOIN c.biensImmobiliers cb
            WHERE cb = b
              AND c.status = :statutContrat
        )
      """)
  List<BiensImmobiliers> findAvailableExcludingActiveContrats(
      @Param("statutContrat") StatusContrat statutContrat);

  @Query("SELECT b FROM BiensImmobiliers b WHERE " +
      "(:typeId IS NULL OR b.typeBiensImmobiliere.id = :typeId) AND (" +
      "LOWER(CONCAT(" +
      "COALESCE(b.nom, ''), ' ', " +
      "COALESCE(b.description, ''), ' ', " +
      "COALESCE(b.adresse, ''), ' ', " +
      "COALESCE(CAST(b.prix AS string), ''), ' ', " +
      "COALESCE(CAST(b.typeUnite AS string), ''), ' ', " +
      "COALESCE(b.commune.libelle, ''), ' ', " +
      "COALESCE(b.arrondissement.libelle, ''), ' ', " +
      "COALESCE(b.departement.libelle, ''), ' ', " +
      "COALESCE(b.userInfo.name, ''), ' ', " +
      "COALESCE(CAST(b.status AS string), '')" +
      ")) LIKE LOWER(CONCAT('%', :texte, '%'))" +
      ")")
  List<BiensImmobiliers> searchByTextAndType(@Param("texte") String texte, @Param("typeId") Long typeId);

  List<BiensImmobiliers> findByUserInfoAndQuartier(UserInfo user, Quartier quartier);

  List<BiensImmobiliers> findByUserInfoAndArrondissement(UserInfo user, Arrondissement arrondissement);

  List<BiensImmobiliers> findByUserInfoAndCommune(UserInfo user, Commune commune);

  List<BiensImmobiliers> findByUserInfoAndDepartement(UserInfo user, Departement departement);

  List<BiensImmobiliers> findAll(Specification<BiensImmobiliers> spec);

  Page<BiensImmobiliers> findByCommune(Commune commune, Pageable pageable);

  List<BiensImmobiliers> findByDepartement(Departement departement);

  List<BiensImmobiliers> findByArrondissement(Arrondissement arrondissement);

  @Query("""
          SELECT b FROM BiensImmobiliers b
          WHERE b.id <> :bienId
            AND (:typeId IS NULL OR b.typeBiensImmobiliere.id = :typeId)
            AND b.prix BETWEEN :prixMin AND :prixMax
      """)
  List<BiensImmobiliers> findSimilarBiens(
      @Param("bienId") Long bienId,
      @Param("typeId") Long typeId,
      @Param("prixMin") Double prixMin,
      @Param("prixMax") Double prixMax,
      Pageable pageable);

  long countByStatusTrue();

  long countByAVendreTrue();

  long countByStatusTrueAndAVendreFalse();

  @Query("""
          SELECT b.typeBiensImmobiliere.nomTypeBiensImmo, COUNT(b)
          FROM BiensImmobiliers b
          GROUP BY b.typeBiensImmobiliere.nomTypeBiensImmo
      """)
  List<Object[]> countByTypeBien();

  @Query("""
          SELECT b.commune.libelle, COUNT(b)
          FROM BiensImmobiliers b
          GROUP BY b.commune.libelle
      """)
  List<Object[]> countByCommune();

  @Query("SELECT AVG(b.prix) FROM BiensImmobiliers b")
  Double findAveragePrix();

  List<BiensImmobiliers> findByPrixBetween(Double minPrix, Double maxPrix);

  List<BiensImmobiliers> findByAVendreTrue();
}