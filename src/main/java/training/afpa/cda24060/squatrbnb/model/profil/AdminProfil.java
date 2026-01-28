package training.afpa.cda24060.squatrbnb.model.profil;

import training.afpa.cda24060.squatrbnb.model.enums.NiveauAdmin;

import java.time.LocalDateTime;

/**
 * Profil Admin - correspond à la table admin_profil
 * Contient les données spécifiques aux utilisateurs avec le rôle ADMIN
 */
public class AdminProfil {
    
    private Long utilisateurId;
    private NiveauAdmin niveau;
    private String departement;
    private int creePar;
    private LocalDateTime dateNomination;
    private String permissions; // JSON des permissions spécifiques
    
    // Constructeurs
    
    public AdminProfil() {
        this.niveau = NiveauAdmin.MODERATEUR;
        this.dateNomination = LocalDateTime.now();
    }
    
    public AdminProfil(Long utilisateurId) {
        this();
        this.utilisateurId = utilisateurId;
    }
    
    public AdminProfil(Long utilisateurId, NiveauAdmin niveau) {
        this(utilisateurId);
        this.niveau = niveau;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si c'est un super admin
     */
    public boolean isSuperAdmin() {
        return niveau == NiveauAdmin.SUPER_ADMIN;
    }
    
    /**
     * Vérifie si c'est au moins un admin (pas juste modérateur)
     */
    public boolean isFullAdmin() {
        return niveau == NiveauAdmin.SUPER_ADMIN || niveau == NiveauAdmin.ADMIN;
    }
    
    /**
     * Vérifie si peut modérer du contenu
     */
    public boolean canModerate() {
        return niveau != null && niveau.canModerate();
    }
    
    /**
     * Vérifie si peut gérer les utilisateurs
     */
    public boolean canManageUsers() {
        return niveau != null && niveau.canManageUsers();
    }
    
    /**
     * Vérifie si peut gérer les autres admins
     */
    public boolean canManageAdmins() {
        return niveau != null && niveau.canManageAdmins();
    }
    
    /**
     * Vérifie une permission spécifique (dans le JSON)
     */
    public boolean hasPermission(String permission) {
        if (isSuperAdmin()) return true;
        if (permissions == null || permissions.isBlank()) return false;
        // Recherche basique dans le JSON
        return permissions.contains("\"" + permission + "\"");
    }
    
    /**
     * Retourne le libellé du niveau
     */
    public String getNiveauLibelle() {
        return niveau != null ? niveau.getLibelle() : "Non défini";
    }
    
    // Getters et Setters
    
    public Long getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public NiveauAdmin getNiveau() {
        return niveau;
    }
    
    public void setNiveau(NiveauAdmin niveau) {
        this.niveau = niveau != null ? niveau : NiveauAdmin.MODERATEUR;
    }
    
    public void setNiveauFromString(String niveauStr) {
        this.niveau = NiveauAdmin.fromCode(niveauStr);
    }
    
    public String getDepartement() {
        return departement;
    }
    
    public void setDepartement(String departement) {
        this.departement = departement;
    }
    
    public int getCreePar() {
        return creePar;
    }
    
    public void setCreePar(int creePar) {
        this.creePar = creePar;
    }
    
    public LocalDateTime getDateNomination() {
        return dateNomination;
    }
    
    public void setDateNomination(LocalDateTime dateNomination) {
        this.dateNomination = dateNomination;
    }
    
    public String getPermissions() {
        return permissions;
    }
    
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
    
    @Override
    public String toString() {
        return "AdminProfil{" +
                "utilisateurId=" + utilisateurId +
                ", niveau=" + niveau +
                ", departement='" + departement + '\'' +
                '}';
    }
}
