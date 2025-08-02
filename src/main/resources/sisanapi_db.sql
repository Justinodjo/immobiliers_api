-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : lun. 26 août 2024 à 22:08
-- Version du serveur : 10.4.25-MariaDB
-- Version de PHP : 7.4.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `sisanapi_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `arrondissement`
--

CREATE TABLE `arrondissement` (
  `id_arrondissement` bigint(20) NOT NULL,
  `datecreation` datetime(6) DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `version` datetime(6) DEFAULT NULL,
  `commune_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `autres_appuis`
--

CREATE TABLE `autres_appuis` (
  `id_autre_appuis` bigint(20) NOT NULL,
  `commentaires` varchar(255) DEFAULT NULL,
  `institution` varchar(255) DEFAULT NULL,
  `nombre_beneficiaires` int(11) NOT NULL,
  `type_appui` varchar(255) DEFAULT NULL,
  `id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `commune`
--

CREATE TABLE `commune` (
  `id_commune` bigint(20) NOT NULL,
  `datecreation` datetime(6) DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `version` datetime(6) DEFAULT NULL,
  `departement_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `departement`
--

CREATE TABLE `departement` (
  `id_departement` bigint(20) NOT NULL,
  `datecreation` datetime(6) DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `pays_id` bigint(20) DEFAULT NULL,
  `version` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `ecole`
--

CREATE TABLE `ecole` (
  `id_ecole` bigint(20) NOT NULL,
  `annee_scolaire_debut` int(11) NOT NULL,
  `annee_scolaire_fin` int(11) NOT NULL,
  `arrondissement` varchar(255) DEFAULT NULL,
  `code_identification` varchar(255) DEFAULT NULL,
  `commune` varchar(255) DEFAULT NULL,
  `contact` varchar(255) DEFAULT NULL,
  `departement` varchar(255) DEFAULT NULL,
  `effectif_maternelle_filles` int(11) NOT NULL,
  `effectif_maternelle_garcons` int(11) NOT NULL,
  `effectif_total_filles` int(11) NOT NULL,
  `effectif_total_garcons` int(11) NOT NULL,
  `nom_ecole` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `equipement`
--

CREATE TABLE `equipement` (
  `id_equipement` bigint(20) NOT NULL,
  `bon_etat` bit(1) NOT NULL,
  `entree` int(11) NOT NULL,
  `mauvais_etat` bit(1) NOT NULL,
  `nature_equipement` varchar(255) DEFAULT NULL,
  `stock_final` int(11) NOT NULL,
  `stock_initial` int(11) NOT NULL,
  `id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `fiche_suivi_annuel`
--

CREATE TABLE `fiche_suivi_annuel` (
  `id_fiche_suivi_annuel` bigint(20) NOT NULL,
  `autres_informations` varchar(255) DEFAULT NULL,
  `ecole_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `infrastructure`
--

CREATE TABLE `infrastructure` (
  `id_infrastructure` bigint(20) NOT NULL,
  `bon_etat` bit(1) NOT NULL,
  `mauvais_etat` bit(1) NOT NULL,
  `nature_infrastructure` varchar(255) DEFAULT NULL,
  `nombre_infrastructure` int(11) NOT NULL,
  `id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `livraison_vivres`
--

CREATE TABLE `livraison_vivres` (
  `id_livraison_vivres` bigint(20) NOT NULL,
  `appreciation_cantine` int(11) NOT NULL,
  `information_raison_mi_chemin` varchar(255) DEFAULT NULL,
  `information_supplementaires` varchar(255) DEFAULT NULL,
  `livresaecole` bit(1) NOT NULL,
  `livres_mi_chemin` bit(1) NOT NULL,
  `raison_mi_chemin` int(11) NOT NULL,
  `fiche_suivi_annuel_id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `menu_journalier`
--

CREATE TABLE `menu_journalier` (
  `id_menu_journalier` bigint(20) NOT NULL,
  `date` varchar(255) DEFAULT NULL,
  `denree` varchar(255) DEFAULT NULL,
  `menu_du_jour` varchar(255) DEFAULT NULL,
  `nombre_filles_servis` int(11) NOT NULL,
  `nombre_garcons_servis` int(11) NOT NULL,
  `nombre_total_servis` int(11) NOT NULL,
  `observations` varchar(255) DEFAULT NULL,
  `solde_du_jour` double NOT NULL,
  `sortie` double NOT NULL,
  `stock_disponible` double NOT NULL,
  `id_semaine_journal` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `personnel_gestion_encadrement`
--

CREATE TABLE `personnel_gestion_encadrement` (
  `id_personnel_gestion_encadrement` bigint(20) NOT NULL,
  `bureauape` bit(1) NOT NULL,
  `comite_gestion_cantine` bit(1) NOT NULL,
  `cuisinieres_pour_ecole` bit(1) NOT NULL,
  `formationsape` bit(1) NOT NULL,
  `membres_formes_gestion_cantine` bit(1) NOT NULL,
  `nombre_cuisinieres` int(11) NOT NULL,
  `nombre_femmesape` int(11) NOT NULL,
  `nombre_femmes_comite` int(11) NOT NULL,
  `nombre_formationsape` int(11) NOT NULL,
  `nombre_formations_cantine` int(11) NOT NULL,
  `nombre_hommesape` int(11) NOT NULL,
  `nombre_hommes_comite` int(11) NOT NULL,
  `nombre_membresape` int(11) NOT NULL,
  `nombre_membres_comite` int(11) NOT NULL,
  `nombre_sensibilisations` int(11) NOT NULL,
  `president_comite_femme` bit(1) NOT NULL,
  `sensibilisation_participation_communautaire` bit(1) NOT NULL,
  `fiche_suivi_annuel_id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `production_ecole`
--

CREATE TABLE `production_ecole` (
  `id_production_ecole` bigint(20) NOT NULL,
  `annee` varchar(255) DEFAULT NULL,
  `champ` bit(1) NOT NULL,
  `jardin` bit(1) NOT NULL,
  `manque_production` bit(1) NOT NULL,
  `mois` varchar(255) DEFAULT NULL,
  `raison_manque_production` varchar(255) DEFAULT NULL,
  `unite_elevage` bit(1) NOT NULL,
  `fiche_suivi_annuel_id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `quartier`
--

CREATE TABLE `quartier` (
  `id_quartier` bigint(20) NOT NULL,
  `datecreation` datetime(6) DEFAULT NULL,
  `libelle` varchar(255) DEFAULT NULL,
  `version` datetime(6) DEFAULT NULL,
  `arrondissement_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `resultats_des_examens`
--

CREATE TABLE `resultats_des_examens` (
  `id_resultats_des_examens` bigint(20) NOT NULL,
  `admis_filles` int(11) NOT NULL,
  `admis_garcons` int(11) NOT NULL,
  `inscrit_filles` int(11) NOT NULL,
  `inscrits_garcons` int(11) NOT NULL,
  `nature_examen` varchar(255) DEFAULT NULL,
  `presents_filles` int(11) NOT NULL,
  `presents_garcons` int(11) NOT NULL,
  `id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `resultats_par_classe`
--

CREATE TABLE `resultats_par_classe` (
  `id_resultat_par_classe` bigint(20) NOT NULL,
  `abandon_exclusion_filles` int(11) NOT NULL,
  `abandon_exclusion_garcons` int(11) NOT NULL,
  `classe` varchar(255) DEFAULT NULL,
  `effectif_fin_annee_filles` int(11) NOT NULL,
  `effectif_fin_annee_garcons` int(11) NOT NULL,
  `effectif_inscrit_filles` int(11) NOT NULL,
  `effectif_inscrit_garcons` int(11) NOT NULL,
  `passage_classe_superieur_filles` int(11) NOT NULL,
  `passage_classe_superieure_garcons` int(11) NOT NULL,
  `redoublement_filles` int(11) NOT NULL,
  `redoublement_garcons` int(11) NOT NULL,
  `id_fiche_suivi_annuel` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `role`
--

CREATE TABLE `role` (
  `id_role` bigint(20) NOT NULL,
  `nom_role` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `semaine_journal`
--

CREATE TABLE `semaine_journal` (
  `id_semaine_journal` bigint(20) NOT NULL,
  `ancien_solde` double NOT NULL,
  `apport_especes` double NOT NULL,
  `apport_nature` varchar(255) DEFAULT NULL,
  `autres_apports` varchar(255) DEFAULT NULL,
  `cours` varchar(255) DEFAULT NULL,
  `date` varchar(255) DEFAULT NULL,
  `depenses_justification` varchar(255) DEFAULT NULL,
  `effectif_filles` int(11) NOT NULL,
  `effectif_garcons` int(11) NOT NULL,
  `effectif_total` int(11) NOT NULL,
  `nouveau_solde` double NOT NULL,
  `provenance` varchar(255) DEFAULT NULL,
  `recettes` double NOT NULL,
  `solde_final` double NOT NULL,
  `souscription_filles` int(11) NOT NULL,
  `souscription_garcons` int(11) NOT NULL,
  `souscription_total` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `user_info`
--

CREATE TABLE `user_info` (
  `id` int(11) NOT NULL,
  `adresse` varchar(255) DEFAULT NULL,
  `date_naissance` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `numero` int(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `second_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `user_info_roles`
--

CREATE TABLE `user_info_roles` (
  `user_info_id` int(11) NOT NULL,
  `roles_id_role` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `arrondissement`
--
ALTER TABLE `arrondissement`
  ADD PRIMARY KEY (`id_arrondissement`),
  ADD KEY `FKjbb2ufcv48c2lxkukdbvc9s3i` (`commune_id`);

--
-- Index pour la table `autres_appuis`
--
ALTER TABLE `autres_appuis`
  ADD PRIMARY KEY (`id_autre_appuis`),
  ADD KEY `FKfvvjqvyvtg5s6fy82odonrjqb` (`id_fiche_suivi_annuel`);

--
-- Index pour la table `commune`
--
ALTER TABLE `commune`
  ADD PRIMARY KEY (`id_commune`),
  ADD KEY `FKnpb4g8pcu98cr0xm6xlrsnyqs` (`departement_id`);

--
-- Index pour la table `departement`
--
ALTER TABLE `departement`
  ADD PRIMARY KEY (`id_departement`);

--
-- Index pour la table `ecole`
--
ALTER TABLE `ecole`
  ADD PRIMARY KEY (`id_ecole`);

--
-- Index pour la table `equipement`
--
ALTER TABLE `equipement`
  ADD PRIMARY KEY (`id_equipement`),
  ADD KEY `FKc842v58gpkutt24x4bk4q06tk` (`id_fiche_suivi_annuel`);

--
-- Index pour la table `fiche_suivi_annuel`
--
ALTER TABLE `fiche_suivi_annuel`
  ADD PRIMARY KEY (`id_fiche_suivi_annuel`),
  ADD KEY `FKb5h9nttbku55mpixaoed1arar` (`ecole_id`);

--
-- Index pour la table `infrastructure`
--
ALTER TABLE `infrastructure`
  ADD PRIMARY KEY (`id_infrastructure`),
  ADD KEY `FK2xli1ilm57jylsi025nnrvupm` (`id_fiche_suivi_annuel`);

--
-- Index pour la table `livraison_vivres`
--
ALTER TABLE `livraison_vivres`
  ADD PRIMARY KEY (`id_livraison_vivres`),
  ADD UNIQUE KEY `UKaslddd8m5yyfleetsiw4tlo82` (`fiche_suivi_annuel_id_fiche_suivi_annuel`);

--
-- Index pour la table `menu_journalier`
--
ALTER TABLE `menu_journalier`
  ADD PRIMARY KEY (`id_menu_journalier`),
  ADD KEY `FK9uahsall7e6n897s0dhqdx1th` (`id_semaine_journal`);

--
-- Index pour la table `personnel_gestion_encadrement`
--
ALTER TABLE `personnel_gestion_encadrement`
  ADD PRIMARY KEY (`id_personnel_gestion_encadrement`),
  ADD UNIQUE KEY `UKb7xkvrss4wgojrctq2fjd23iv` (`fiche_suivi_annuel_id_fiche_suivi_annuel`);

--
-- Index pour la table `production_ecole`
--
ALTER TABLE `production_ecole`
  ADD PRIMARY KEY (`id_production_ecole`),
  ADD UNIQUE KEY `UKp2xembyd7f3fgv1hjblf93bhi` (`fiche_suivi_annuel_id_fiche_suivi_annuel`);

--
-- Index pour la table `quartier`
--
ALTER TABLE `quartier`
  ADD PRIMARY KEY (`id_quartier`),
  ADD KEY `FK52esg8634qh7pluu3dp0u47jq` (`arrondissement_id`);

--
-- Index pour la table `resultats_des_examens`
--
ALTER TABLE `resultats_des_examens`
  ADD PRIMARY KEY (`id_resultats_des_examens`),
  ADD KEY `FKr4iwn11c7psiwjmj5qt030xgk` (`id_fiche_suivi_annuel`);

--
-- Index pour la table `resultats_par_classe`
--
ALTER TABLE `resultats_par_classe`
  ADD PRIMARY KEY (`id_resultat_par_classe`),
  ADD KEY `FKh2f3ekdsgywjp1hlp418asewj` (`id_fiche_suivi_annuel`);

--
-- Index pour la table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id_role`);

--
-- Index pour la table `semaine_journal`
--
ALTER TABLE `semaine_journal`
  ADD PRIMARY KEY (`id_semaine_journal`);

--
-- Index pour la table `user_info`
--
ALTER TABLE `user_info`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `user_info_roles`
--
ALTER TABLE `user_info_roles`
  ADD KEY `FKcwnmadkmjr6316by3mv95qvmo` (`roles_id_role`),
  ADD KEY `FKe65si118lwa824w1hnu68whal` (`user_info_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `arrondissement`
--
ALTER TABLE `arrondissement`
  MODIFY `id_arrondissement` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `autres_appuis`
--
ALTER TABLE `autres_appuis`
  MODIFY `id_autre_appuis` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `commune`
--
ALTER TABLE `commune`
  MODIFY `id_commune` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `departement`
--
ALTER TABLE `departement`
  MODIFY `id_departement` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `ecole`
--
ALTER TABLE `ecole`
  MODIFY `id_ecole` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `equipement`
--
ALTER TABLE `equipement`
  MODIFY `id_equipement` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `fiche_suivi_annuel`
--
ALTER TABLE `fiche_suivi_annuel`
  MODIFY `id_fiche_suivi_annuel` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `infrastructure`
--
ALTER TABLE `infrastructure`
  MODIFY `id_infrastructure` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `livraison_vivres`
--
ALTER TABLE `livraison_vivres`
  MODIFY `id_livraison_vivres` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `menu_journalier`
--
ALTER TABLE `menu_journalier`
  MODIFY `id_menu_journalier` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `personnel_gestion_encadrement`
--
ALTER TABLE `personnel_gestion_encadrement`
  MODIFY `id_personnel_gestion_encadrement` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `production_ecole`
--
ALTER TABLE `production_ecole`
  MODIFY `id_production_ecole` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `quartier`
--
ALTER TABLE `quartier`
  MODIFY `id_quartier` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `resultats_des_examens`
--
ALTER TABLE `resultats_des_examens`
  MODIFY `id_resultats_des_examens` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `resultats_par_classe`
--
ALTER TABLE `resultats_par_classe`
  MODIFY `id_resultat_par_classe` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `role`
--
ALTER TABLE `role`
  MODIFY `id_role` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `semaine_journal`
--
ALTER TABLE `semaine_journal`
  MODIFY `id_semaine_journal` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `user_info`
--
ALTER TABLE `user_info`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `arrondissement`
--
ALTER TABLE `arrondissement`
  ADD CONSTRAINT `FKjbb2ufcv48c2lxkukdbvc9s3i` FOREIGN KEY (`commune_id`) REFERENCES `commune` (`id_commune`);

--
-- Contraintes pour la table `autres_appuis`
--
ALTER TABLE `autres_appuis`
  ADD CONSTRAINT `FKfvvjqvyvtg5s6fy82odonrjqb` FOREIGN KEY (`id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `commune`
--
ALTER TABLE `commune`
  ADD CONSTRAINT `FKnpb4g8pcu98cr0xm6xlrsnyqs` FOREIGN KEY (`departement_id`) REFERENCES `departement` (`id_departement`);

--
-- Contraintes pour la table `equipement`
--
ALTER TABLE `equipement`
  ADD CONSTRAINT `FKc842v58gpkutt24x4bk4q06tk` FOREIGN KEY (`id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `fiche_suivi_annuel`
--
ALTER TABLE `fiche_suivi_annuel`
  ADD CONSTRAINT `FKb5h9nttbku55mpixaoed1arar` FOREIGN KEY (`ecole_id`) REFERENCES `ecole` (`id_ecole`);

--
-- Contraintes pour la table `infrastructure`
--
ALTER TABLE `infrastructure`
  ADD CONSTRAINT `FK2xli1ilm57jylsi025nnrvupm` FOREIGN KEY (`id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `livraison_vivres`
--
ALTER TABLE `livraison_vivres`
  ADD CONSTRAINT `FKbbeokiv2ro1r6pjg5ume4u4jt` FOREIGN KEY (`fiche_suivi_annuel_id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `menu_journalier`
--
ALTER TABLE `menu_journalier`
  ADD CONSTRAINT `FK9uahsall7e6n897s0dhqdx1th` FOREIGN KEY (`id_semaine_journal`) REFERENCES `semaine_journal` (`id_semaine_journal`);

--
-- Contraintes pour la table `personnel_gestion_encadrement`
--
ALTER TABLE `personnel_gestion_encadrement`
  ADD CONSTRAINT `FKq0xhoas71t92px3n039lduxr6` FOREIGN KEY (`fiche_suivi_annuel_id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `production_ecole`
--
ALTER TABLE `production_ecole`
  ADD CONSTRAINT `FKtmmx39jw2gws6j3jyy6bqpamu` FOREIGN KEY (`fiche_suivi_annuel_id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `quartier`
--
ALTER TABLE `quartier`
  ADD CONSTRAINT `FK52esg8634qh7pluu3dp0u47jq` FOREIGN KEY (`arrondissement_id`) REFERENCES `arrondissement` (`id_arrondissement`);

--
-- Contraintes pour la table `resultats_des_examens`
--
ALTER TABLE `resultats_des_examens`
  ADD CONSTRAINT `FKr4iwn11c7psiwjmj5qt030xgk` FOREIGN KEY (`id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `resultats_par_classe`
--
ALTER TABLE `resultats_par_classe`
  ADD CONSTRAINT `FKh2f3ekdsgywjp1hlp418asewj` FOREIGN KEY (`id_fiche_suivi_annuel`) REFERENCES `fiche_suivi_annuel` (`id_fiche_suivi_annuel`);

--
-- Contraintes pour la table `user_info_roles`
--
ALTER TABLE `user_info_roles`
  ADD CONSTRAINT `FKcwnmadkmjr6316by3mv95qvmo` FOREIGN KEY (`roles_id_role`) REFERENCES `role` (`id_role`),
  ADD CONSTRAINT `FKe65si118lwa824w1hnu68whal` FOREIGN KEY (`user_info_id`) REFERENCES `user_info` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
