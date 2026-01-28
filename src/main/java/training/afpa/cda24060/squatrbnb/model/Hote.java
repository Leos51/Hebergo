package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.profil.HoteProfil;

/**
 * Utilisateur avec le rôle Hôte
 * Propriétaire proposant des biens à la location
 */
public class Hote extends Utilisateur {
    
    private HoteProfil profil;
    
    // Constructeurs
    
    public Hote() {
        super();
        addRole(Role.HOTE);
        this.profil = new HoteProfil();
    }
    
    public Hote(String email, String motDePasse, String nom, String prenom) {
        super(email, motDePasse, nom, prenom);
        addRole(Role.HOTE);
        this.profil = new HoteProfil();
    }
    
    public Hote(Long id, HoteProfil profil) {
        this();
        this.id = id;
        this.profil = profil;
        if (profil != null) {
            profil.setUtilisateurId(id);
        }
    }
    
    // Implémentation des méthodes abstraites
    
    @Override
    public Role getRolePrincipal() {
        return Role.HOTE;
    }
    
    @Override
    public String getDashboardUrl() {

        return "/hote/dashboard";
    }
    
    // Méthodes spécifiques Hôte (délégation vers le profil)
    
    /**
     * Retourne le nom d'affichage (raison sociale si pro, sinon nom complet)
     */
    public String getNomAffichage() {
        if (profil != null && profil.isProfessionnel() && profil.getRaisonSociale() != null) {
            return profil.getRaisonSociale();
        }
        return getNomComplet();
    }
    
    public boolean isVerifie() {
        return profil != null && profil.isVerifie();
    }
    
    public boolean isProfessionnel() {
        return profil != null && profil.isProfessionnel();
    }
    
    public String getNoteFormatee() {
        return profil != null ? profil.getNoteFormatee() : "Nouveau";
    }
    
    public int getNbBiens() {
        return profil != null ? profil.getNbBiens() : 0;
    }
    
    public int getNbAvis() {
        return profil != null ? profil.getNbAvis() : 0;
    }
    
    /**
     * Vérifie si le profil hôte est complet pour publier des biens
     */
    public boolean canPublierBiens() {
        return profil != null 
            && profil.isProfilCompletPourPublication()
            && isProfilComplet();
    }
    
    // Getters et Setters
    
    public HoteProfil getProfil() {
        return profil;
    }
    
    public void setProfil(HoteProfil profil) {
        this.profil = profil;
        if (profil != null) {
            profil.setUtilisateurId(id);
        }
    }
    
    @Override
    public void setId(Long id) {
        super.setId(id);
        if (profil != null) {
            profil.setUtilisateurId(id);
        }
    }
    
    // Raccourcis vers les propriétés du profil
    
    public String getDescription() {
        return profil != null ? profil.getDescription() : null;
    }
    
    public void setDescription(String description) {
        if (profil == null) profil = new HoteProfil(id);
        profil.setDescription(description);
    }
    
    public String getSiret() {
        return profil != null ? profil.getSiret() : null;
    }
    
    public void setSiret(String siret) {
        if (profil == null) profil = new HoteProfil(id);
        profil.setSiret(siret);
    }

    @Override
    public String toString() {
        return "Hote{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nomComplet='" + getNomComplet() + '\'' +
                ", verifie=" + isVerifie() +
                ", nbBiens=" + getNbBiens() +
                ", note=" + getNoteFormatee() +
                '}';
    }
}
