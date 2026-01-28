package training.afpa.cda24060.squatrbnb.model.enums;

/**
 * Niveaux d'administration
 */
public enum NiveauAdmin {
    
    SUPER_ADMIN("Super Administrateur", 1, true, true, true),
    ADMIN("Administrateur", 2, true, true, false),
    MODERATEUR("Modérateur", 3, true, false, false);
    
    private final String libelle;
    private final int niveau;
    private final boolean canModerate;
    private final boolean canManageUsers;
    private final boolean canManageAdmins;
    
    NiveauAdmin(String libelle, int niveau, boolean canModerate, boolean canManageUsers, boolean canManageAdmins) {
        this.libelle = libelle;
        this.niveau = niveau;
        this.canModerate = canModerate;
        this.canManageUsers = canManageUsers;
        this.canManageAdmins = canManageAdmins;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public int getNiveau() {
        return niveau;
    }
    
    public boolean canModerate() {
        return canModerate;
    }
    
    public boolean canManageUsers() {
        return canManageUsers;
    }
    
    public boolean canManageAdmins() {
        return canManageAdmins;
    }
    
    /**
     * Vérifie si ce niveau est supérieur ou égal à un autre
     */
    public boolean isAtLeast(NiveauAdmin other) {
        return this.niveau <= other.niveau; // Plus le niveau est bas, plus les droits sont élevés
    }
    
    public static NiveauAdmin fromCode(String code) {
        if (code == null || code.isBlank()) {
            return MODERATEUR;
        }
        try {
            return valueOf(code.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return MODERATEUR;
        }
    }
}
