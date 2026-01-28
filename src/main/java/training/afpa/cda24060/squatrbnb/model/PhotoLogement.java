package training.afpa.cda24060.squatrbnb.model;

import java.time.LocalDateTime;

/**
 * Photo d'un logement
 */
public class PhotoLogement {

    private Long id;
    private Long logementId;
    private String url;
    private String legende;
    private boolean estPrincipale;
    private int ordre;
    private LocalDateTime dateAjout;

    // Constructeurs
    public PhotoLogement() {}

    public PhotoLogement(Long logementId, String url) {
        this.logementId = logementId;
        this.url = url;
        this.estPrincipale = false;
        this.ordre = 0;
    }

    public PhotoLogement(Long logementId, String url, boolean estPrincipale) {
        this.logementId = logementId;
        this.url = url;
        this.estPrincipale = estPrincipale;
        this.ordre = 0;
    }

    // Getters et Setters
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

    public String getLegende() {
        return legende;
    }

    public void setLegende(String legende) {
        this.legende = legende;
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

    @Override
    public String toString() {
        return "PhotoLogement{id=" + id + ", url='" + url + "', principale=" + estPrincipale + "}";
    }
}