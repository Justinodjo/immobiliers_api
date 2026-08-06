package com.immoservice.immobiliers.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.immoservice.immobiliers.entities.Arrondissement;
import com.immoservice.immobiliers.entities.BiensImmobiliers;
import com.immoservice.immobiliers.entities.Commune;
import com.immoservice.immobiliers.entities.Departement;
import com.immoservice.immobiliers.entities.DetailsBiensImmobiliers;
import com.immoservice.immobiliers.entities.Fichier;
import com.immoservice.immobiliers.entities.SousDetailsBiensImmobiliers;
import com.immoservice.immobiliers.entities.TypeBiensImmobiliere;
import com.immoservice.immobiliers.entities.UserInfo;
import com.immoservice.immobiliers.repository.ArrondissementRepository;
import com.immoservice.immobiliers.repository.BiensImmobiliersRepository;
import com.immoservice.immobiliers.repository.CommuneRepository;
import com.immoservice.immobiliers.repository.DepartementRepository;
import com.immoservice.immobiliers.repository.FichierRepository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

@Service
public class BiensImmobiliersService {
    // private static final Logger logger =
    // LoggerFactory.getLogger(BiensImmobiliersService.class);
    @Autowired
    private BiensImmobiliersRepository biensImmobiliersRepository;
    @Autowired
    private FichierRepository fichierRepository;

    @Autowired
    private ArrondissementRepository arrondissementRepository;

    @Autowired
    private CommuneRepository communeRepository;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private UserInfoService userInfoService;

    // Méthode pour récupérer l'utilisateur actuellement connecté
    private UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userInfoService.findByUsername(username); // Suppose qu'il y a un service UserInfoService pour
                                                             // récupérer un utilisateur
        }
        return null; // Si l'utilisateur n'est pas connecté
    }

    @Transactional
    public BiensImmobiliers save(BiensImmobiliers biensImmobiliers) {

        attachRelations(biensImmobiliers);
        // Message de notification
        String message = "📢 Nouveau bien disponible à " + biensImmobiliers.getAdresse() + " !";

        // Notifier les locataires de la même zone
        userInfoService.notifierUsersParRoleEtZone("LOCATAIRE_ROLES", biensImmobiliers.getAdresse(), message);
        userInfoService.notifierUsersParRoleEtZone(
                "MANAGER_ROLES",
                biensImmobiliers.getAdresse(),
                "👤 Un nouveau bien a été ajouté par un propriétaire dans votre zone : "
                        + biensImmobiliers.getAdresse());

        return biensImmobiliersRepository.save(biensImmobiliers);
    }

    // ✅ Méthode utilitaire pour attacher les entités liées
    public void attachRelations(BiensImmobiliers biensImmobiliers) {
        // Vérifier si le fichier principal existe en base
        if (biensImmobiliers.getFichier() != null && biensImmobiliers.getFichier().getId() != null) {
            biensImmobiliers.setFichier(fichierRepository.findById(biensImmobiliers.getFichier().getId())
                    .orElseThrow(() -> new RuntimeException("Fichier non trouvé")));
        }

        // Vérifier si les fichiers secondaires existent en base
        if (biensImmobiliers.getFichiers() != null && !biensImmobiliers.getFichiers().isEmpty()) {
            List<Fichier> fichiersAttached = biensImmobiliers.getFichiers().stream()
                    .map(fichier -> fichierRepository.findById(fichier.getId())
                            .orElseThrow(() -> new RuntimeException("Fichier non trouvé : " + fichier.getId())))
                    .collect(Collectors.toList());

            biensImmobiliers.setFichiers(fichiersAttached);
        }

        if (biensImmobiliers.getArrondissement() != null) {
            Long idArr = biensImmobiliers.getArrondissement().getIdArrondissement();
            if (idArr != null) {
                Arrondissement existing = arrondissementRepository.findById(idArr)
                        .orElseThrow(() -> new RuntimeException("Arrondissement non trouvé avec ID: " + idArr));
                biensImmobiliers.setArrondissement(existing);
            } else {

                throw new RuntimeException("❌ L'Arrondissement doit avoir un ID valide. Création non autorisée ici.");
            }
        }

        // 🔹 Commune
        if (biensImmobiliers.getCommune() != null) {
            Long idCommune = biensImmobiliers.getCommune().getIdCommune();
            if (idCommune != null) {
                biensImmobiliers.setCommune(
                        communeRepository.findById(idCommune)
                                .orElseThrow(() -> new RuntimeException("Commune non trouvée avec ID: " + idCommune)));
            } else {
                throw new RuntimeException("❌ La Commune doit avoir un ID valide. Création non autorisée ici.");
            }
        }

        // 🔹 Département
        if (biensImmobiliers.getDepartement() != null) {
            Long idDepartement = biensImmobiliers.getDepartement().getIdDepartement();
            if (idDepartement != null) {
                biensImmobiliers.setDepartement(
                        departementRepository.findById(idDepartement)
                                .orElseThrow(() -> new RuntimeException(
                                        "Département non trouvé avec ID: " + idDepartement)));
            } else {
                throw new RuntimeException("❌ Le Département doit avoir un ID valide. Création non autorisée ici.");
            }
        }

        // 🔹 Détails
        // 🔹 Détails
        if (biensImmobiliers.getDetailsBiensImmobiliers() != null) {
            for (DetailsBiensImmobiliers detail : biensImmobiliers.getDetailsBiensImmobiliers()) {
                detail.setBiensImmobiliers(biensImmobiliers); // lien bidirectionnel

                // ✅ Fichier principal
                if (detail.getFichier() != null && detail.getFichier().getId() != null) {
                    Fichier fichier = fichierRepository.findById(detail.getFichier().getId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Fichier non trouvé pour Details ID: " + detail.getId()));
                    detail.setFichier(fichier);
                }

                // ✅ Liste des fichiers secondaires
                if (detail.getFichiers() != null && !detail.getFichiers().isEmpty()) {
                    List<Fichier> fichiersDetail = detail.getFichiers().stream()
                            .filter(f -> f.getId() != null)
                            .map(f -> fichierRepository.findById(f.getId())
                                    .orElseThrow(() -> new RuntimeException("Fichier non trouvé: " + f.getId())))
                            .collect(Collectors.toList());
                    detail.setFichiers(fichiersDetail);
                }

                // Gérer les sous-détails si présents
                if (detail.getSousDetailsBiensImmobiliers() != null) {
                    for (SousDetailsBiensImmobiliers sousDetail : detail.getSousDetailsBiensImmobiliers()) {
                        sousDetail.setDetailsBiensImmobiliers(detail); // lien bidirectionnel
                    }
                }
            }
        }
    }

    // Récupérer les biens disponibles selon règle métier simple (status = true &&
    // aVendre = false)
    public List<BiensImmobiliers> getDisponibles() {
        try {
            List<BiensImmobiliers> disponibles = biensImmobiliersRepository.findByStatusTrueAndAVendreFalse();
            // Si tu veux exclure biens déjà liés à des contrats "EN_COURS", utilise la
            // méthode avancée :
            // if (disponibles == null || disponibles.isEmpty()) {
            // disponibles =
            // biensImmobiliersRepository.findAvailableExcludingActiveContrats("EN_COURS");
            // }
            return disponibles;
        } catch (Exception e) {
            // logger si besoin
            e.printStackTrace();
            return List.of();
        }
    }

    public Page<BiensImmobiliers> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return biensImmobiliersRepository.findAll(pageable);
    }

    public Optional<BiensImmobiliers> getById(Long id) {
        return biensImmobiliersRepository.findById(id);
    }

    /**
     * 🆕 RECHERCHE AVANCÉE AVEC JPA SPECIFICATIONS
     */
    public List<BiensImmobiliers> searchWithAdvancedFilters(
            String texte, Long typeId, Long communeId, String communeNom,
            Boolean status, Double minPrice, Double maxPrice,
            LocalDate dateDebut, LocalDate dateFin, Integer minPieces,
            Integer occupants, Double minSuperficie, Double maxSuperficie,
            String typeUnite, Boolean aVendre) {

        Specification<BiensImmobiliers> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtre par texte (nom ou adresse)
            if (texte != null && !texte.trim().isEmpty()) {
                String searchText = "%" + texte.toLowerCase() + "%";
                Predicate nomPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nom")), searchText);
                Predicate adressePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("adresse")), searchText);
                predicates.add(criteriaBuilder.or(nomPredicate, adressePredicate));
            }

            // Filtre par type de bien
            if (typeId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("typeBiensImmobiliere").get("id"), typeId));
            }

            // Filtre par commune (ID)
            if (communeId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("commune").get("idCommune"), communeId));
            }

            // Filtre par commune (nom)
            if (communeNom != null && !communeNom.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("commune").get("libelle")),
                        "%" + communeNom.toLowerCase() + "%"));
            }

            // Filtre par statut
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Filtre par prix minimum
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("prix"), minPrice));
            }

            // Filtre par prix maximum
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("prix"), maxPrice));
            }

            // Filtre par superficie minimum
            if (minSuperficie != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("superficie"), minSuperficie));
            }

            // Filtre par superficie maximum
            if (maxSuperficie != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("superficie"), maxSuperficie));
            }

            // Filtre par nombre de pièces minimum (via détails)
            if (minPieces != null && minPieces > 0) {
                Join<BiensImmobiliers, DetailsBiensImmobiliers> detailsJoin = root.join("detailsBiensImmobiliers");
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        detailsJoin.get("nombre"), minPieces));
            }

            // Filtre par type d'unité (jour, mois, année)
            if (typeUnite != null && !typeUnite.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("typeUnite"), typeUnite.toLowerCase()));
            }

            // Filtre par vente/location
            if (aVendre != null) {
                predicates.add(criteriaBuilder.equal(root.get("aVendre"), aVendre));
            }

            // Filtre par dates (si dates de disponibilité existent)
            if (dateDebut != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("dateCreation"), dateDebut));
            }

            if (dateFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("dateCreation"), dateFin));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return biensImmobiliersRepository.findAll(spec);
    }

    /**
     * 🆕 BIENS PAR COMMUNE (avec pagination)
     */
    public Page<BiensImmobiliers> getBiensByCommune(Long communeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Commune commune = communeRepository.findById(communeId)
                .orElseThrow(() -> new RuntimeException("Commune non trouvée"));
        return biensImmobiliersRepository.findByCommune(commune, pageable);
    }

    /**
     * 🆕 BIENS PAR DÉPARTEMENT
     */
    public List<BiensImmobiliers> getBiensByDepartement(Long departementId) {
        Departement departement = departementRepository.findById(departementId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé"));
        return biensImmobiliersRepository.findByDepartement(departement);
    }

    /**
     * 🆕 BIENS PAR ARRONDISSEMENT
     */
    public List<BiensImmobiliers> getBiensByArrondissement(Long arrondissementId) {
        Arrondissement arrondissement = arrondissementRepository.findById(arrondissementId)
                .orElseThrow(() -> new RuntimeException("Arrondissement non trouvé"));
        return biensImmobiliersRepository.findByArrondissement(arrondissement);
    }

    /**
     * 🆕 BIENS PAR FOURCHETTE DE PRIX
     */
    public List<BiensImmobiliers> getBiensByPrixRange(Double minPrix, Double maxPrix) {
        return biensImmobiliersRepository.findByPrixBetween(minPrix, maxPrix);
    }

    /**
     * 🆕 BIENS À VENDRE
     */
    public List<BiensImmobiliers> getBiensAVendre() {
        return biensImmobiliersRepository.findByAVendreTrue();
    }

    /**
     * 🆕 BIENS À LOUER (disponibles, non à vendre)
     */
    public List<BiensImmobiliers> getBiensALouer() {
        return biensImmobiliersRepository.findByStatusTrueAndAVendreFalse();
    }

    /**
     * 🆕 BIENS LES PLUS POPULAIRES
     */
    public List<BiensImmobiliers> getBiensPopulaires(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "nombreVues"));
        return biensImmobiliersRepository.findAll(pageable).getContent();
    }

    /**
     * 🆕 BIENS LES MIEUX NOTÉS
     */
    public List<BiensImmobiliers> getBiensMieuxNotes(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "noteMoyenne"));
        return biensImmobiliersRepository.findAll(pageable).getContent();
    }

    /**
     * 🆕 BIENS SIMILAIRES
     */
    public List<BiensImmobiliers> getBiensSimilaires(Long bienId, int limit) {
        BiensImmobiliers bien = biensImmobiliersRepository.findById(bienId)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        // Définir une marge de prix (±20%)
        Double prixMin = bien.getPrix() * 0.8;
        Double prixMax = bien.getPrix() * 1.2;

        return biensImmobiliersRepository.findSimilarBiens(
                bienId,
                bien.getTypeBiensImmobiliere().getId(),
                prixMin,
                prixMax,
                PageRequest.of(0, limit));
    }

    // ==================== STATISTIQUES ====================

    public long countAll() {
        return biensImmobiliersRepository.count();
    }

    public long countDisponibles() {
        return biensImmobiliersRepository.countByStatusTrue();
    }

    public long countAVendre() {
        return biensImmobiliersRepository.countByAVendreTrue();
    }

    public long countEnLocation() {
        return biensImmobiliersRepository.countByStatusTrueAndAVendreFalse();
    }

    public Map<String, Long> countByType() {
        List<Object[]> results = biensImmobiliersRepository.countByTypeBien();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    public Map<String, Long> countByCommune() {
        List<Object[]> results = biensImmobiliersRepository.countByCommune();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    public Double getPrixMoyen() {
        return biensImmobiliersRepository.findAveragePrix();
    }

    public void delete(Long id) {
        biensImmobiliersRepository.deleteById(id);
    }

    public List<BiensImmobiliers> searchBiensByTextAndType(String texte, Long typeId) {
        if ((texte == null || texte.isEmpty()) && typeId == null) {
            return biensImmobiliersRepository.findAll();
        }
        return biensImmobiliersRepository.searchByTextAndType(
                texte != null ? texte : "", typeId);
    }

    public BiensImmobiliers updateBiensImmobiliers(Long id, BiensImmobiliers biensImmobiliers) {
        if (biensImmobiliersRepository.existsById(id)) {
            biensImmobiliers.setId(id);
            attachRelations(biensImmobiliers);
            return biensImmobiliersRepository.save(biensImmobiliers);
        }
        return null;
    }

    // Récupérer les biens immobiliers par type avec pagination
    public Page<BiensImmobiliers> getBiensByType(TypeBiensImmobiliere typeBiensImmobiliere, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return biensImmobiliersRepository.findByTypeBiensImmobiliere(typeBiensImmobiliere, pageable);
    }

    public Page<BiensImmobiliers> getBiensByUser(UserInfo userInfo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return biensImmobiliersRepository.findByUserInfo(userInfo, pageable);
    }

    public Page<BiensImmobiliers> getBiensByCurrentUser(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié.");
        }

        String username = authentication.getName();
        UserInfo currentUser = userInfoService.findByUsername(username);

        return biensImmobiliersRepository.findByUserInfo(currentUser, pageable);
    }

    public BiensImmobiliers consulterBien(Long id) {
        BiensImmobiliers bien = biensImmobiliersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bien non trouvé"));

        bien.incrementerVue();
        return biensImmobiliersRepository.save(bien);
    }

    public Optional<BiensImmobiliers> incrementerJaime(Long id) {
        return biensImmobiliersRepository.findById(id).map(bien -> {
            bien.setNombreJaime(bien.getNombreJaime() + 1);
            return biensImmobiliersRepository.save(bien);
        });
    }
}