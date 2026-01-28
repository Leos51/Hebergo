package training.afpa.cda24060.squatrbnb.model.enums;

public enum StatutReservation {
    EN_ATTENTE("En attente", ""),
    CONFIRME("Confirmé", ""),
    EN_COURS("En cours", ""),
    TERMINE("Terminée", ""),
    ANNULEE("Annulée", ""),
    REFUSEE("Refusée", ""),
    LITIGE("Litige", "");

    private final String libelle;
    private final String cssClass;


    StatutReservation(String libelle, String cssClass) {
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
     * Convertit une chaîne en StatutReservation (insensible à la casse)
     */
    public static StatutReservation fromString(String value) {
        if (value == null || value.isBlank()) {
            return EN_ATTENTE;
        }
        try {
            return StatutReservation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EN_ATTENTE;
        }
    }
}

