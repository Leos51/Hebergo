package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.profil.AdminProfil;
import training.afpa.cda24060.squatrbnb.model.profil.HoteProfil;
import training.afpa.cda24060.squatrbnb.model.profil.LocataireProfil;

import java.util.Set;

/**
 * Factory pour créer le bon type d'utilisateur selon les rôles
 * 
 * Utilisée par les DAOs pour instancier le bon type de classe
 * lors du mapping depuis la base de données
 */
public class UtilisateurFactory {
    
    /**
     * Crée un utilisateur du bon type selon ses rôles
     * 
     * @param id ID de l'utilisateur
     * @param roles Set des rôles
     * @param profilHote Profil hôte (peut être null)
     * @param profilLocataire Profil locataire (peut être null)
     * @param profilAdmin Profil admin (peut être null)
     * @return L'utilisateur du bon type
     */
    public static Utilisateur create(Long id, Set<Role> roles,
                                     HoteProfil profilHote,
                                     LocataireProfil profilLocataire,
                                     AdminProfil profilAdmin) {
        
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Un utilisateur doit avoir au moins un rôle");
        }
        
        boolean isAdmin = roles.contains(Role.ADMIN);
        boolean isHote = roles.contains(Role.HOTE);
        boolean isLocataire = roles.contains(Role.LOCATAIRE);
        
        // Priorité : Admin > HoteLocataire > Hote > Locataire
        
        if (isAdmin) {
            Admin admin = new Admin(id, profilAdmin);
            // Un admin peut aussi avoir d'autres rôles
            if (isHote) admin.addRole(Role.HOTE);
            if (isLocataire) admin.addRole(Role.LOCATAIRE);
            return admin;
        }
        
        if (isHote && isLocataire) {
            return new HoteLocataire(id, profilHote, profilLocataire);
        }
        
        if (isHote) {
            return new Hote(id, profilHote);
        }
        
        if (isLocataire) {
            return new Locataire(id, profilLocataire);
        }
        
        // Ne devrait jamais arriver
        throw new IllegalArgumentException("Rôles non reconnus: " + roles);
    }
    
    /**
     * Crée un utilisateur à partir d'une String de rôles (depuis BDD)
     */
    public static Utilisateur createFromRolesString(Long id, String rolesString,
                                                    HoteProfil profilHote,
                                                    LocataireProfil profilLocataire,
                                                    AdminProfil profilAdmin) {
        
        Set<Role> roles = java.util.EnumSet.noneOf(Role.class);
        
        if (rolesString != null && !rolesString.isBlank()) {
            for (String code : rolesString.split(",")) {
                if (Role.isValid(code.trim())) {
                    roles.add(Role.fromCode(code.trim()));
                }
            }
        }
        
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Aucun rôle valide dans: " + rolesString);
        }
        
        return create(id, roles, profilHote, profilLocataire, profilAdmin);
    }
    
    /**
     * Crée un nouvel utilisateur pour l'inscription
     * 
     * @param role Rôle initial choisi lors de l'inscription
     * @param email Email
     * @param motDePasse Mot de passe hashé
     * @param nom Nom
     * @param prenom Prénom
     * @return Nouvel utilisateur du bon type
     */
    public static Utilisateur createForInscription(Role role, String email, 
                                                   String motDePasse, String nom, String prenom) {
        return switch (role) {
            case ADMIN -> new Admin(email, motDePasse, nom, prenom);
            case HOTE -> new Hote(email, motDePasse, nom, prenom);
            case LOCATAIRE -> new Locataire(email, motDePasse, nom, prenom);
        };
    }
    
    /**
     * Vérifie si un utilisateur peut avoir un rôle supplémentaire
     */
    public static boolean canAddRole(Utilisateur utilisateur, Role newRole) {
        if (utilisateur == null || newRole == null) return false;
        
        // Un utilisateur a déjà ce rôle
        if (utilisateur.hasRole(newRole)) return false;
        
        // Un admin ne peut pas devenir autre chose (mais un hôte/locataire peut devenir admin)
        // Un hôte peut devenir locataire et vice-versa
        
        return true;
    }
    
    /**
     * Convertit un Hote en HoteLocataire (ajout du rôle locataire)
     */
    public static HoteLocataire upgradeToHoteLocataire(Hote hote) {
        HoteLocataire hl = new HoteLocataire();
        
        // Copier les données de base
        hl.setId(hote.getId());
        hl.setEmail(hote.getEmail());
        hl.setMotDePasse(hote.getMotDePasse());
        hl.setNom(hote.getNom());
        hl.setPrenom(hote.getPrenom());
        hl.setTelephone(hote.getTelephone());
        hl.setPhotoUrl(hote.getPhotoUrl());
        hl.setActif(hote.isActif());
        hl.setDateInscription(hote.getDateInscription());
        hl.setDateDerniereConnexion(hote.getDateDerniereConnexion());
        hl.setAdressePrincipale(hote.getAdressePrincipale());
        
        // Copier le profil hôte
        hl.setProfilHote(hote.getProfil());
        
        // Créer un nouveau profil locataire vide
        hl.setProfilLocataire(new LocataireProfil(hote.getId()));
        
        return hl;
    }
    
    /**
     * Convertit un Locataire en HoteLocataire (ajout du rôle hôte)
     */
    public static HoteLocataire upgradeToHoteLocataire(Locataire locataire) {
        HoteLocataire hl = new HoteLocataire();
        
        // Copier les données de base
        hl.setId(locataire.getId());
        hl.setEmail(locataire.getEmail());
        hl.setMotDePasse(locataire.getMotDePasse());
        hl.setNom(locataire.getNom());
        hl.setPrenom(locataire.getPrenom());
        hl.setTelephone(locataire.getTelephone());
        hl.setPhotoUrl(locataire.getPhotoUrl());
        hl.setActif(locataire.isActif());
        hl.setDateInscription(locataire.getDateInscription());
        hl.setDateDerniereConnexion(locataire.getDateDerniereConnexion());
        hl.setAdressePrincipale(locataire.getAdressePrincipale());
        
        // Copier le profil locataire
        hl.setProfilLocataire(locataire.getProfil());
        
        // Créer un nouveau profil hôte vide
        hl.setProfilHote(new HoteProfil(locataire.getId()));
        
        return hl;
    }
}
