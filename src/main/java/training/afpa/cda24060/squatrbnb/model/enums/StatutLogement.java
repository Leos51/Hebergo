package training.afpa.cda24060.squatrbnb.model.enums;

/**
 * Statuts possibles d'un logement
 */
public enum StatutLogement {

    BROUILLON("Brouillon", "secondary"),
    DISPONIBLE("Disponible", "success"),
    INDISPONIBLE("Indisponible", "warning"),
    ARCHIVE("Archivé", "danger");

    private final String libelle;
    private final String cssClass;

    StatutLogement(String libelle, String cssClass) {
        this.libelle = libelle;
        this.cssClass = cssClass;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getCssClass() {
        return cssClass;
    }

    /**
     * Convertit une chaîne en StatutLogement (insensible à la casse)
     */
    public static StatutLogement fromString(String value) {
        if (value == null || value.isBlank()) {
            return BROUILLON;
        }
        try {
            return StatutLogement.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BROUILLON;
        }
    }
}