package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.profil.LocataireProfil;

import java.time.LocalDate;

/**
 * Utilisateur avec le rôle Locataire
 * Personne louant des biens
 */
public class Locataire extends Utilisateur {
    
    private LocataireProfil profil;
    
    // Constructeurs
    
    public Locataire() {
        super();
        addRole(Role.LOCATAIRE);
        this.profil = new LocataireProfil();
    }
    
    public Locataire(String email, String motDePasse, String nom, String prenom) {
        super(email, motDePasse, nom, prenom);
        addRole(Role.LOCATAIRE);
        this.profil = new LocataireProfil();
    }
    
    public Locataire(Long id, LocataireProfil profil) {
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
        return Role.LOCATAIRE;
    }
    
    @Override
    public String getDashboardUrl() {
        return "/locataire/dashboard";
    }
    
    // Méthodes spécifiques Locataire
    
    public boolean isVerifie() {
        return profil != null && profil.isVerifie();
    }


    
    public int getNbReservations() {
        return profil != null ? profil.getNbReservations() : 0;
    }
    
    /**
     * Vérifie si le locataire peut effectuer une réservation
     */
    public boolean canReserver() {
        return profil != null 
            && profil.isProfilCompletPourReservation()
            && isProfilComplet()
            && actif;
    }
    

    // Getters et Setters
    
    public LocataireProfil getProfil() {
        return profil;
    }
    
    public void setProfil(LocataireProfil profil) {
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
    
    public LocalDate getDateNaissance() {
        return profil != null ? profil.getDateNaissance() : null;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        if (profil == null) profil = new LocataireProfil(id);
        profil.setDateNaissance(dateNaissance);
    }
    
    public String getNationalite() {
        return profil != null ? profil.getNationalite() : null;
    }
    
    public void setNationalite(String nationalite) {
        if (profil == null) profil = new LocataireProfil(id);
        profil.setNationalite(nationalite);
    }
    

    

    
    @Override
    public String toString() {
        return "Locataire{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nomComplet='" + getNomComplet() + '\'' +
                ", verifie=" + isVerifie() +
                ", nbReservations=" + getNbReservations() +
                '}';
    }
}
