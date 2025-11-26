package com.immoservice.immobiliers.dto;

import java.time.LocalDate;
import java.util.List;

public class ContratDTO {
    private String typeContrat;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double loyerMensuel;
    private Double avance;
    private Double prixTotal;
    private Long userId;
    private String status;
    private String validationStatus;
    
    private List<BienDTO> biensImmobiliers;
    private List<DetailDTO> detailsBiensImmobiliers;
    private List<DocumentDTO> documents;
    
    // Getters et Setters
    public String getTypeContrat() {
        return typeContrat;
    }
    
    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public Double getLoyerMensuel() {
        return loyerMensuel;
    }
    
    public void setLoyerMensuel(Double loyerMensuel) {
        this.loyerMensuel = loyerMensuel;
    }
    
    public Double getAvance() {
        return avance;
    }
    
    public void setAvance(Double avance) {
        this.avance = avance;
    }
    
    public Double getPrixTotal() {
        return prixTotal;
    }
    
    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getValidationStatus() {
        return validationStatus;
    }
    
    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    public List<BienDTO> getBiensImmobiliers() {
        return biensImmobiliers;
    }
    
    public void setBiensImmobiliers(List<BienDTO> biensImmobiliers) {
        this.biensImmobiliers = biensImmobiliers;
    }
    
    public List<DetailDTO> getDetailsBiensImmobiliers() {
        return detailsBiensImmobiliers;
    }
    
    public void setDetailsBiensImmobiliers(List<DetailDTO> detailsBiensImmobiliers) {
        this.detailsBiensImmobiliers = detailsBiensImmobiliers;
    }
    
    public List<DocumentDTO> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }
}