package com.immoservice.immobiliers.dto;


public class MandatGestionDTO {
    private Integer proprietaireId;  // l'utilisateur qui délègue
    private Integer gestionnaireId;  // l'utilisateur mandaté
    private Long departementId;
    private Long communeId;
    private Long arrondissementId;
    private Long quartierId;         // zone concernée (ou communeId)
    private String conditions; // optionnel : texte libre
    private String commentaire;      // optionnel : conditions ou notes

    // Getters et setters
    public Integer getProprietaireId() { return proprietaireId; }
    public void setProprietaireId(Integer proprietaireId) { this.proprietaireId = proprietaireId; }

    public Integer getGestionnaireId() { return gestionnaireId; }
    public void setGestionnaireId(Integer gestionnaireId) { this.gestionnaireId = gestionnaireId; }

    public Long getDepartementId() { return departementId; }
    public void setDepartementId(Long departementId) { this.departementId = departementId; }

    public Long getCommuneId() { return communeId; }
    public void setCommuneId(Long communeId) { this.communeId = communeId; }

    public Long getArrondissementId() { return arrondissementId; }
    public void setArrondissementId(Long arrondissementId) { this.arrondissementId = arrondissementId; }

    public Long getQuartierId() { return quartierId; }
    public void setQuartierId(Long quartierId) { this.quartierId = quartierId; }

    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
