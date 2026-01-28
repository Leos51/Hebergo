package training.afpa.cda24060.squatrbnb.model;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe abstraite représentant une adresse
 * Correspond à la table 'adresse' en BDD
 * 
 * Héritée par :
 * - AdresseUtilisateur : adresse liée à un utilisateur (via utilisateur_adresse)
 * - AdresseBien : adresse d'un bien immobilier
 */


public abstract class Adresse {
    
    protected Long id;
    protected String adresse;
//    protected String adresseLigne2;
    protected String codePostal;
    protected String ville;
    protected String region;
    protected String pays;
    protected BigDecimal latitude;
    protected BigDecimal longitude;
    protected LocalDateTime dateCreation;
    protected LocalDateTime dateModification;
    
    // Constructeurs
    protected Adresse() {
        this.pays = "France";
        this.dateCreation = LocalDateTime.now();
    }
    
    protected Adresse(String adresse, String codePostal, String ville) {
        this();
        this.adresse = adresse;
        this.codePostal = codePostal;
        this.ville = ville;
    }
    
    protected Adresse(String adresse, String codePostal, String ville, String pays) {
        this(adresse, codePostal, ville);
        this.pays = (pays != null && !pays.isBlank()) ? pays : "France";
    }
    
    // Méthodes abstraites - à implémenter par les sous-classes
    
    /**
     * Retourne le type d'adresse (pour affichage/logging)
     */
    public abstract String getTypeAdresse();
    
    // Méthodes communes
    
    /**
     * Retourne l'adresse formatée sur une ligne
     * Ex: "12 rue de la Paix, 75001 Paris, France"
     */
    public String getAdresseFormatee() {
        StringBuilder sb = new StringBuilder();
        
        if (adresse != null) {
            sb.append(adresse);
        }
        
//        if (adresseLigne2 != null && !adresseLigne2.isBlank()) {
//            sb.append(", ").append(adresseLigne2);
//        }
        
        if (codePostal != null || ville != null) {
            sb.append(", ");
            if (codePostal != null) sb.append(codePostal).append(" ");
            if (ville != null) sb.append(ville);
        }
        
        if (pays != null && !"France".equalsIgnoreCase(pays)) {
            sb.append(", ").append(pays);
        }
        
        return sb.toString().trim();
    }
    
    /**
     * Retourne l'adresse formatée sur plusieurs lignes
     */
    public String getAdresseMultilignes() {
        StringBuilder sb = new StringBuilder();
        
        if (adresse != null) {
            sb.append(adresse);
        }
        
//        if (adresseLigne2 != null && !adresseLigne2.isBlank()) {
//            sb.append("\n").append(adresseLigne2);
//        }
        
        if (codePostal != null || ville != null) {
            sb.append("\n");
            if (codePostal != null) sb.append(codePostal).append(" ");
            if (ville != null) sb.append(ville);
        }
        
        if (pays != null && !"France".equalsIgnoreCase(pays)) {
            sb.append("\n").append(pays);
        }
        
        return sb.toString().trim();
    }
    
    /**
     * Retourne l'adresse courte (ville uniquement)
     */
    public String getAdresseCourte() {
        if (ville != null) {
            return ville;
        }
        return codePostal != null ? codePostal : "";
    }
    
    /**
     * Vérifie si les coordonnées GPS sont définies
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
    
    /**
     * Retourne les coordonnées sous forme "lat,lng" pour Google Maps
     */
    public String getCoordinatesString() {
        if (!hasCoordinates()) return null;
        return latitude.toPlainString() + "," + longitude.toPlainString();
    }
    
    /**
     * Retourne l'URL Google Maps
     */
    public String getGoogleMapsUrl() {
        if (hasCoordinates()) {
            return "https://www.google.com/maps?q=" + getCoordinatesString();
        }
        return "https://www.google.com/maps/search/" + 
               java.net.URLEncoder.encode(getAdresseFormatee(), java.nio.charset.StandardCharsets.UTF_8);
    }
    
    /**
     * Vérifie si l'adresse est complète (minimum requis)
     */
    public boolean isComplete() {
        return adresse != null && !adresse.isBlank()
            && codePostal != null && !codePostal.isBlank()
            && ville != null && !ville.isBlank();
    }
    
    // Getters et Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresseLigne1) {
        this.adresse = (adresseLigne1 != null) ? adresseLigne1.trim() : null;
    }
    
//    public String getAdresseLigne2() {
//        return adresseLigne2;
//    }
    
//    public void setAdresseLigne2(String adresseLigne2) {
//        this.adresseLigne2 = (adresseLigne2 != null) ? adresseLigne2.trim() : null;
//    }
    
    public String getCodePostal() {
        return codePostal;
    }
    
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = (ville != null) ? ville.trim() : null;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public String getPays() {
        return pays;
    }
    
    public void setPays(String pays) {
        this.pays = (pays != null && !pays.isBlank()) ? pays : "France";
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", adresse='" + getAdresseFormatee() + '\'' +
                ", type='" + getTypeAdresse() + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adresse adresse = (Adresse) o;
        return id != null && Objects.equals(id, adresse.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
