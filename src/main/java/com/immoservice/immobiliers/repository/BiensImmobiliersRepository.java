package com.immoservice.immobiliers.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;

@Repository
public interface BiensImmobiliersRepository extends JpaRepository<BiensImmobiliers, Long> {
    List<BiensImmobiliers> findByUserInfoEmail(String email);

    List<BiensImmobiliers> findByTypeBiensImmobiliereId(Long typeId);

    Page<BiensImmobiliers> findByTypeBiensImmobiliere(TypeBiensImmobiliere typeBiensImmobiliere, Pageable pageable);

    long countByUserInfo(UserInfo userInfo);

    List<BiensImmobiliers> findByUserInfoId(Integer userId);

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
}