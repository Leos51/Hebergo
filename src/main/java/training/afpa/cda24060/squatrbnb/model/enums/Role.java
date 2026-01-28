package training.afpa.cda24060.squatrbnb.model.enums;

/**
 * Rôles utilisateur - correspond à la table 'role' en BDD
 */
public enum Role {
    
    ADMIN("Administrateur", "Administration du système", 1),
    HOTE("Hôte", "Propriétaire proposant des biens", 2),
    LOCATAIRE("Locataire", "Personne louant des biens", 3);
    
    private final String libelle;
    private final String description;
    private final int id; // Correspond à role.id en BDD
    
    Role(String libelle, String description, int id) {
        this.libelle = libelle;
        this.description = description;
        this.id = id;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getId() {
        return id;
    }
    
    public String getCode() {
        return this.name();
    }
    
    /**
     * Conversion depuis un code String (depuis la BDD)
     * @throws IllegalArgumentException si le code est invalide
     */
    public static Role fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Code role ne peut pas être null ou vide");
        }
        try {
            return valueOf(code.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role inconnu: " + code);
        }
    }
    
    /**
     * Conversion depuis un ID (depuis la BDD)
     */
    public static Role fromId(int id) {
        for (Role role : values()) {
            if (role.id == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Role ID inconnu: " + id);
    }
    
    /**
     * Vérifie si un code est valide
     */
    public static boolean isValid(String code) {
        if (code == null || code.isBlank()) return false;
        try {
            valueOf(code.toUpperCase().trim());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
