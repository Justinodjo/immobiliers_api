package com.immoservice.immobiliers.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String secondName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateNaissance;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String adresse;
    private String registreCommerce;
    private Integer identifiantNumerique;
    private Integer identifiantFiscal;
    private String email;
    @Column(name = "numero", length = 20)
    private String numero;
    private String provider; // GOOGLE, FACEBOOK, GITHUB...
    private String providerId;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "fichier_id") // Clé étrangère dans la table produits
    private Fichier fichier;
    @Transient
    private String passwordConfirm;
    @JsonManagedReference
    @OneToMany(mappedBy = "userInfo", cascade = { CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE }, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Fichier> fichiers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_permission", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    // ✅ MÉTHODE UTILITAIRE : Récupérer le fichier principal
    public Fichier getFichierPrincipal() {
        return fichiers.stream()
                .filter(Fichier::isPrincipal) // Assumant qu'il y a un attribut 'principal' dans Fichier
                .findFirst()
                .orElse(null);
    }

    // ✅ MÉTHODE UTILITAIRE : Définir un fichier comme principal
    public void setFichierPrincipal(Fichier fichier) {
        // Retirer le statut principal des autres fichiers
        fichiers.forEach(f -> f.setPrincipal(false));
        // Définir le nouveau fichier comme principal
        if (fichier != null) {
            fichier.setPrincipal(true);
            if (!fichiers.contains(fichier)) {
                fichiers.add(fichier);
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Fichier getFichier() {
        return fichier;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

    public List<Fichier> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<Fichier> fichiers) {
        this.fichiers = fichiers;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getNomComplet() {
        return (name != null ? name : "") + " " + (secondName != null ? secondName : "");
    }

    public String getRegistreCommerce() {
        return registreCommerce;
    }

    public void setRegistreCommerce(String registreCommerce) {
        this.registreCommerce = registreCommerce;
    }

    public Integer getIdentifiantNumerique() {
        return identifiantNumerique;
    }

    public void setIdentifiantNumerique(Integer identifiantNumerique) {
        this.identifiantNumerique = identifiantNumerique;
    }

    public Integer getIdentifiantFiscal() {
        return identifiantFiscal;
    }

    public void setIdentifiantFiscal(Integer identifiantFiscal) {
        this.identifiantFiscal = identifiantFiscal;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", secondName='" + secondName + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", password='" + password + '\'' +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                ", numero=" + numero +
                ", fichier=" + fichier +
                ", roles=" + roles +
                ", permissions=" + permissions +
                '}';
    }

}
