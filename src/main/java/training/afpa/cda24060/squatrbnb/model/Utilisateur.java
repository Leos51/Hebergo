package training.afpa.cda24060.squatrbnb.model;



import training.afpa.cda24060.squatrbnb.model.enums.Role;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classe abstraite représentant un utilisateur
 * Correspond à la table 'utilisateur' en BDD
 * 
 * Héritée par :
 * - Hote : utilisateur avec le rôle HOTE
 * - Locataire : utilisateur avec le rôle LOCATAIRE
 * - Admin : utilisateur avec le rôle ADMIN
 * - UtilisateurMultiRole : utilisateur avec plusieurs rôles
 */


public abstract class Utilisateur {

    protected Long id;
    protected String email;
    protected String motDePasse;
    protected String nom;
    protected String prenom;
    protected String telephone;
    protected String photoUrl;
    protected boolean actif;
    protected LocalDateTime dateInscription;
    protected LocalDateTime dateDerniereConnexion;
    protected LocalDateTime dateModification;
    
    // Rôles - utilisation d'EnumSet pour performance et type-safety
    protected Set<Role> roles = EnumSet.noneOf(Role.class);
    
    // Adresse principale (chargée à la demande)
    protected Adresse adressePrincipale;
    
    // Constructeurs
    
    protected Utilisateur() {
        this.actif = true;
        this.dateInscription = LocalDateTime.now();
    }
    
    protected Utilisateur(String email, String motDePasse, String nom, String prenom) {
        this();
        setEmail(email);
        this.motDePasse = motDePasse;
        setNom(nom);
        setPrenom(prenom);
    }
    
    // Méthodes abstraites
    
    /**
     * Retourne le rôle principal de l'utilisateur
     */
    public abstract Role getRolePrincipal();
    
    /**
     * Retourne l'URL du dashboard approprié
     */
    public abstract String getDashboardUrl();
    
    // Méthodes de gestion des rôles
    
    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
    
    /**
     * Ajoute un rôle à l'utilisateur
     */
    public void addRole(Role role) {
        if (role != null) {
            roles.add(role);
        }
    }
    
    /**
     * Retire un rôle à l'utilisateur
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }
    
    /**
     * Vérifie si l'utilisateur a le rôle ADMIN
     */
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }
    
    /**
     * Vérifie si l'utilisateur a le rôle HOTE
     */
    public boolean isHote() {
        return hasRole(Role.HOTE);
    }
    
    /**
     * Vérifie si l'utilisateur a le rôle LOCATAIRE
     */
    public boolean isLocataire() {
        return hasRole(Role.LOCATAIRE);
    }
    
    /**
     * Vérifie si l'utilisateur a plusieurs rôles
     */
    public boolean isMultiRole() {
        return roles.size() > 1;
    }
    
    /**
     * Retourne les rôles sous forme de String (pour la BDD)
     */
    public String getRolesAsString() {
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.joining(","));
    }
    
    /**
     * Définit les rôles depuis une String (depuis la BDD)
     */
    public void setRolesFromString(String rolesStr) {
        roles.clear();
        if (rolesStr != null && !rolesStr.isBlank()) {
            for (String code : rolesStr.split(",")) {
                if (Role.isValid(code.trim())) {
                    roles.add(Role.fromCode(code.trim()));
                }
            }
        }
    }
    
    // Méthodes utilitaires
    
    /**
     * Retourne le nom complet
     */
    public String getNomComplet() {
        StringBuilder sb = new StringBuilder();
        if (prenom != null) sb.append(prenom);
        if (nom != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(nom);
        }
        return sb.toString();
    }
    
    /**
     * Retourne les initiales
     */
    public String getInitiales() {
        StringBuilder sb = new StringBuilder();
        if (prenom != null && !prenom.isEmpty()) {
            sb.append(Character.toUpperCase(prenom.charAt(0)));
        }
        if (nom != null && !nom.isEmpty()) {
            sb.append(Character.toUpperCase(nom.charAt(0)));
        }
        return sb.toString();
    }
    
    /**
     * Vérifie si le compte est actif et peut se connecter
     */
    public boolean canLogin() {
        return actif && email != null && motDePasse != null;
    }
    
    /**
     * Vérifie si le profil est complet
     */
    public boolean isProfilComplet() {
        return email != null && !email.isBlank()
            && nom != null && !nom.isBlank()
            && prenom != null && !prenom.isBlank()
            && telephone != null && !telephone.isBlank();
    }
    
    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = (email != null) ? email.toLowerCase().trim() : null;
    }
    
    public String getMotDePasse() {
        return motDePasse;
    }
    
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = (nom != null) ? nom.trim() : null;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = (prenom != null) ? prenom.trim() : null;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    
    /**
     * Retourne l'URL de la photo ou un avatar par défaut
     */
    public String getPhotoUrlOrDefault() {
        if (photoUrl != null && !photoUrl.isBlank()) {
            return photoUrl;
        }
        return null;
    }
    
    public boolean isActif() {
        return actif;
    }
    
    public void setActif(boolean actif) {
        this.actif = actif;
    }
    
    public LocalDateTime getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    public LocalDateTime getDateDerniereConnexion() {
        return dateDerniereConnexion;
    }
    
    public void setDateDerniereConnexion(LocalDateTime dateDerniereConnexion) {
        this.dateDerniereConnexion = dateDerniereConnexion;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    public Set<Role> getRoles() {
        return EnumSet.copyOf(roles); // Retourne une copie pour l'encapsulation
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles.clear();
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }
    
    public Adresse getAdressePrincipale() {
        return adressePrincipale;
    }
    
    public void setAdressePrincipale(Adresse adressePrincipale) {
        this.adressePrincipale = adressePrincipale;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nomComplet='" + getNomComplet() + '\'' +
                ", roles=" + getRolesAsString() +
                ", actif=" + actif +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
