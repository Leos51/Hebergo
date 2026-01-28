package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.NiveauAdmin;
import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.profil.AdminProfil;

/**
 * Utilisateur avec le rôle Admin
 * Administrateur du système
 */
public class Admin extends Utilisateur {

    private AdminProfil profil;

    // Constructeurs

    public Admin() {
        super();
        addRole(Role.ADMIN);
        this.profil = new AdminProfil();
    }

    public Admin(String email, String motDePasse, String nom, String prenom) {
        super(email, motDePasse, nom, prenom);
        addRole(Role.ADMIN);
        this.profil = new AdminProfil();
    }

    public Admin(String email, String motDePasse, String nom, String prenom, NiveauAdmin niveau) {
        this(email, motDePasse, nom, prenom);
        profil.setNiveau(niveau);
    }

    public Admin(Long id, AdminProfil profil) {
        this();
        this.id = id;
        this.profil = profil;
        if (profil != null) {
            profil.setUtilisateurId(id);
        }
    }

//    // Implémentation des méthodes abstraites

    @Override
    public Role getRolePrincipal() {
        return Role.ADMIN;
    }

    @Override
    public String getDashboardUrl() {
        return "/admin/dashboard";
    }


}
