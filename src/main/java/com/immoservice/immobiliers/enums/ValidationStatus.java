package com.immoservice.immobiliers.enums;

public enum ValidationStatus {
    EN_ATTENTE, // Contrat créé mais pas encore confirmé
    CONFIRME_PAR_GESTIONNAIRE, // Gestionnaire ou propriétaire a validé
    VALIDEE_PAR_LOCATAIRE, // Locataire a validé et paiement possible
    VALIDEE_PAR_PROPRIETAIRE, // Propriétaire a validé et paiement possible
    CONFIRME_PAR_ADMINISTRATEUR, // Administrateur a validé,
    CONFIRME_PAR_MASTER_ADMINISTRATEUR // Master administrateur a validé

}
