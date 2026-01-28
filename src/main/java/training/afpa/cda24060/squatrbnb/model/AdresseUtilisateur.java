package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.TypeAdresse;

/**
 * Adresse liée à un utilisateur via la table utilisateur_adresse
 * Un utilisateur peut avoir plusieurs adresses (domicile, facturation, etc.)
 */
public class AdresseUtilisateur extends Adresse {
    
    private Long utilisateurId;
    private TypeAdresse type;
    private String libelle;  // Nom personnalisé (ex: "Bureau", "Parents")
    private boolean principale;
    
    // Constructeurs
    
    public AdresseUtilisateur() {
        super();
        this.type = TypeAdresse.DOMICILE;
        this.principale = false;
    }
    
    public AdresseUtilisateur(String adresse, String codePostal, String ville) {
        super(adresse, codePostal, ville);
        this.type = TypeAdresse.DOMICILE;
        this.principale = false;
    }
    
    public AdresseUtilisateur(String adresse, String codePostal, String ville, TypeAdresse type) {
        this(adresse, codePostal, ville);
        this.type = type;
    }
    
    public AdresseUtilisateur(Long utilisateurId, String adresse, String codePostal, String ville, TypeAdresse type, boolean principale) {
        this(adresse, codePostal, ville, type);
        this.utilisateurId = utilisateurId;
        this.principale = principale;
    }
    
    // Implémentation méthode abstraite
    
    @Override
    public String getTypeAdresse() {
        return type != null ? type.getLibelle() : "Adresse utilisateur";
    }
    
    // Méthodes spécifiques
    
    /**
     * Retourne le libellé d'affichage
     */
    public String getLibelleAffichage() {
        if (libelle != null && !libelle.isBlank()) {
            return libelle;
        }
        return type != null ? type.getLibelle() : "Adresse";
    }
    
    /**
     * Vérifie si c'est une adresse de facturation
     */
    public boolean isFacturation() {
        return type == TypeAdresse.FACTURATION;
    }
    
    /**
     * Vérifie si c'est le domicile
     */
    public boolean isDomicile() {
        return type == TypeAdresse.DOMICILE;
    }
    
    // Getters et Setters
    
    public Long getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public TypeAdresse getType() {
        return type;
    }
    
    public void setType(TypeAdresse type) {
        this.type = type != null ? type : TypeAdresse.AUTRE;
    }
    
    public void setTypeFromString(String typeStr) {
        this.type = TypeAdresse.fromCode(typeStr);
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public boolean isPrincipale() {
        return principale;
    }
    
    public void setPrincipale(boolean principale) {
        this.principale = principale;
    }
    
    @Override
    public String toString() {
        return "AdresseUtilisateur{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", type=" + type +
                ", principale=" + principale +
                ", adresse='" + getAdresseFormatee() + '\'' +
                '}';
    }
}
