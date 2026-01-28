package training.afpa.cda24060.squatrbnb.model;

import java.time.LocalDateTime;

/**
 * Modèle représentant une photo de logement
 */
public class PhotoLogement {

    private Long id;
    private Long logementId;
    private String url;
    private String description;
    private boolean estPrincipale;
    private int ordre;
    private LocalDateTime dateAjout;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================

    public PhotoLogement() {
    }

    public PhotoLogement(Long logementId, String url) {
        this.logementId = logementId;
        this.url = url;
        this.estPrincipale = false;
        this.ordre = 0;
    }

    public PhotoLogement(Long logementId, String url, boolean estPrincipale, int ordre) {
        this.logementId = logementId;
        this.url = url;
        this.estPrincipale = estPrincipale;
        this.ordre = ordre;
    }

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLogementId() {
        return logementId;
    }

    public void setLogementId(Long logementId) {
        this.logementId = logementId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public boolean isEstPrincipale() {
        return estPrincipale;
    }

    public void setEstPrincipale(boolean estPrincipale) {
        this.estPrincipale = estPrincipale;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Obtenir le nom du fichier depuis l'URL
     */
    public String getNomFichier() {
        if (url == null) return null;
        int lastSlash = url.lastIndexOf('/');
        return lastSlash >= 0 ? url.substring(lastSlash + 1) : url;
    }

    @Override
    public String toString() {
        return "PhotoLogement{" +
                "id=" + id +
                ", logementId=" + logementId +
                ", url='" + url + '\'' +
                ", estPrincipale=" + estPrincipale +
                ", ordre=" + ordre +
                '}';
    }


}