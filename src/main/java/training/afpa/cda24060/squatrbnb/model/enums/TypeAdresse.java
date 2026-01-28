package training.afpa.cda24060.squatrbnb.model.enums;

/**
 * Types d'adresse pour la table utilisateur_adresse
 */
public enum TypeAdresse {
    
    DOMICILE("Domicile", "Adresse de résidence principale"),
    FACTURATION("Facturation", "Adresse de facturation"),
    AUTRE("Autre", "Autre adresse");
    
    private final String libelle;
    private final String description;
    
    TypeAdresse(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static TypeAdresse fromCode(String code) {
        if (code == null || code.isBlank()) {
            return AUTRE;
        }
        try {
            return valueOf(code.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return AUTRE;
        }
    }
}
