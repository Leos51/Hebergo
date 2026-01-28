package training.afpa.cda24060.squatrbnb.model.enums;

/**
 * Énumération des statuts possibles d'une réservation
 */
public enum StatutReservation {

    /**
     * Réservation en attente de confirmation par l'hôte
     */
    EN_ATTENTE("En attente", "warning"),

    /**
     * Réservation confirmée par l'hôte
     */
    CONFIRMEE("Confirmée", "success"),

    /**
     * Réservation en cours (dates actuelles)
     */
    EN_COURS("En cours", "info"),

    /**
     * Réservation terminée
     */
    TERMINEE("Terminée", "secondary"),

    /**
     * Réservation annulée
     */
    ANNULEE("Annulée", "danger"),

    /**
     * Réservation refusée par l'hôte
     */
    REFUSEE("Refusée", "danger");

    private final String libelle;
    private final String bootstrapClass;

    StatutReservation(String libelle, String bootstrapClass) {
        this.libelle = libelle;
        this.bootstrapClass = bootstrapClass;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getBootstrapClass() {
        return bootstrapClass;
    }

    /**
     * Obtenir la classe CSS Bootstrap pour le badge
     */
    public String getBadgeClass() {
        return "badge bg-" + bootstrapClass;
    }

    /**
     * Vérifier si le statut est actif (pas terminé ni annulé)
     */
    public boolean isActif() {
        return this == EN_ATTENTE || this == CONFIRMEE || this == EN_COURS;
    }

    /**
     * Vérifier si le statut est final (terminé ou annulé)
     */
    public boolean isFinal() {
        return this == TERMINEE || this == ANNULEE || this == REFUSEE;
    }
}