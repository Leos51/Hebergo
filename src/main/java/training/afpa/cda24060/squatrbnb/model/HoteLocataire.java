package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.profil.HoteProfil;
import training.afpa.cda24060.squatrbnb.model.profil.LocataireProfil;

/**
 * Utilisateur avec les rôles Hôte ET Locataire
 * Exemple : Antoine Robert qui loue son appartement ET réserve chez d'autres hôtes
 */
public class HoteLocataire extends Utilisateur {
    
    private HoteProfil profilHote;
    private LocataireProfil profilLocataire;
    
    // Rôle actif (pour le dashboard actuel)
    private Role roleActif;
    
    // Constructeurs
    
    public HoteLocataire() {
        super();
        addRole(Role.HOTE);
        addRole(Role.LOCATAIRE);
        this.profilHote = new HoteProfil();
        this.profilLocataire = new LocataireProfil();
        this.roleActif = Role.HOTE; // Par défaut
    }
    
    public HoteLocataire(String email, String motDePasse, String nom, String prenom) {
        super(email, motDePasse, nom, prenom);
        addRole(Role.HOTE);
        addRole(Role.LOCATAIRE);
        this.profilHote = new HoteProfil();
        this.profilLocataire = new LocataireProfil();
        this.roleActif = Role.HOTE;
    }
    
    public HoteLocataire(Long id, HoteProfil profilHote, LocataireProfil profilLocataire) {
        this();
        this.id = id;
        this.profilHote = profilHote;
        this.profilLocataire = profilLocataire;
        if (profilHote != null) profilHote.setUtilisateurId(id);
        if (profilLocataire != null) profilLocataire.setUtilisateurId(id);
    }
    
    // Implémentation des méthodes abstraites
    
    @Override
    public Role getRolePrincipal() {
        return roleActif;
    }
    
    @Override
    public String getDashboardUrl() {
        return switch (roleActif) {
            case HOTE -> "/hote/dashboard";
            case LOCATAIRE -> "/locataire/dashboard";
            default -> "/";
        };
    }
    
    // Gestion du rôle actif
    
    /**
     * Change le rôle actif (switch entre Hôte et Locataire)
     */
    public void switchRole() {
        roleActif = (roleActif == Role.HOTE) ? Role.LOCATAIRE : Role.HOTE;
    }
    
    /**
     * Définit le rôle actif
     */
    public void setRoleActif(Role role) {
        if (role == Role.HOTE || role == Role.LOCATAIRE) {
            this.roleActif = role;
        }
    }
    
    public Role getRoleActif() {
        return roleActif;
    }
    
    public boolean isRoleActifHote() {
        return roleActif == Role.HOTE;
    }
    
    public boolean isRoleActifLocataire() {
        return roleActif == Role.LOCATAIRE;
    }
    
    // Méthodes déléguées au profil Hôte
    
    public boolean isHoteVerifie() {
        return profilHote != null && profilHote.isVerifie();
    }
    
    public boolean isProfessionnel() {
        return profilHote != null && profilHote.isProfessionnel();
    }
    
    public String getNoteHoteFormatee() {
        return profilHote != null ? profilHote.getNoteFormatee() : "Nouveau";
    }
    
    public int getNbBiens() {
        return profilHote != null ? profilHote.getNbBiens() : 0;
    }
    
    public int getNbAvisRecus() {
        return profilHote != null ? profilHote.getNbAvis() : 0;
    }
    
    // Méthodes déléguées au profil Locataire
    
    public boolean isLocataireVerifie() {
        return profilLocataire != null && profilLocataire.isVerifie();
    }
    
    public boolean isMajeur() {
        return profilLocataire != null && profilLocataire.isMajeur();
    }
    
    public Integer getAge() {
        return profilLocataire != null ? profilLocataire.getAge() : null;
    }
    
    public int getNbReservations() {
        return profilLocataire != null ? profilLocataire.getNbReservations() : 0;
    }
    
    public int getNbAvisDonnes() {
        return profilLocataire != null ? profilLocataire.getNbAvisDonnes() : 0;
    }
    
    // Vérifications combinées
    
    /**
     * Vérifie si peut publier des biens (en tant qu'hôte)
     */
    public boolean canPublierBiens() {
        return profilHote != null 
            && profilHote.isProfilCompletPourPublication()
            && isProfilComplet();
    }
    
    /**
     * Vérifie si peut réserver (en tant que locataire)
     */
    public boolean canReserver() {
        return profilLocataire != null 
            && profilLocataire.isProfilCompletPourReservation()
            && isProfilComplet()
            && actif;
    }
    
    // Getters et Setters
    
    public HoteProfil getProfilHote() {
        return profilHote;
    }
    
    public void setProfilHote(HoteProfil profilHote) {
        this.profilHote = profilHote;
        if (profilHote != null) {
            profilHote.setUtilisateurId(id);
        }
    }
    
    public LocataireProfil getProfilLocataire() {
        return profilLocataire;
    }
    
    public void setProfilLocataire(LocataireProfil profilLocataire) {
        this.profilLocataire = profilLocataire;
        if (profilLocataire != null) {
            profilLocataire.setUtilisateurId(id);
        }
    }
    
    @Override
    public void setId(Long id) {
        super.setId(id);
        if (profilHote != null) profilHote.setUtilisateurId(id);
        if (profilLocataire != null) profilLocataire.setUtilisateurId(id);
    }
    
    @Override
    public String toString() {
        return "HoteLocataire{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nomComplet='" + getNomComplet() + '\'' +
                ", roleActif=" + roleActif +
                ", nbBiens=" + getNbBiens() +
                ", nbReservations=" + getNbReservations() +
                '}';
    }
}
