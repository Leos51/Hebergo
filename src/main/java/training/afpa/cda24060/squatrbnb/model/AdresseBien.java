package training.afpa.cda24060.squatrbnb.model;

/**
 * Adresse d'un bien immobilier
 * Liée directement au bien via bien.adresse_id
 * Contient des informations spécifiques comme les instructions d'accès
 */
public class AdresseBien extends Adresse {
    
    private Long bienId;
    private String instructionsAcces;
    private String codePortail;
    private String codeImmeuble;
    private Integer etage;
    private String numeroAppartement;
    private String pointsInteret;  // Métro, commerces à proximité
    
    // Constructeurs
    
    public AdresseBien() {
        super();
    }
    
    public AdresseBien(String adresseLigne1, String codePostal, String ville) {
        super(adresseLigne1, codePostal, ville);
    }
    
    public AdresseBien(String adresseLigne1, String codePostal, String ville, String pays) {
        super(adresseLigne1, codePostal, ville, pays);
    }
    
    // Implémentation méthode abstraite
    
    @Override
    public String getTypeAdresse() {
        return "Adresse du bien";
    }
    
    // Méthodes spécifiques
    
    /**
     * Retourne les instructions d'accès formatées
     */
    public String getInstructionsCompletes() {
        StringBuilder sb = new StringBuilder();
        
        if (instructionsAcces != null && !instructionsAcces.isBlank()) {
            sb.append(instructionsAcces);
        }
        
        if (codePortail != null && !codePortail.isBlank()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("Code portail : ").append(codePortail);
        }
        
        if (codeImmeuble != null && !codeImmeuble.isBlank()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("Code immeuble : ").append(codeImmeuble);
        }
        
        if (etage != null) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("Étage : ").append(etage == 0 ? "RDC" : etage);
        }
        
        if (numeroAppartement != null && !numeroAppartement.isBlank()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append("Appartement : ").append(numeroAppartement);
        }
        
        return sb.toString();
    }
    
    /**
     * Vérifie si des codes d'accès sont définis
     */
    public boolean hasCodesAcces() {
        return (codePortail != null && !codePortail.isBlank()) 
            || (codeImmeuble != null && !codeImmeuble.isBlank());
    }
    
    /**
     * Retourne l'adresse avec l'étage/appartement
     */
    @Override
    public String getAdresseFormatee() {
        String base = super.getAdresseFormatee();
        
        StringBuilder complement = new StringBuilder();
        if (etage != null) {
            complement.append(etage == 0 ? "RDC" : "Étage " + etage);
        }
        if (numeroAppartement != null && !numeroAppartement.isBlank()) {
            if (complement.length() > 0) complement.append(", ");
            complement.append("Apt ").append(numeroAppartement);
        }
        
        if (complement.length() > 0) {
            return base + " (" + complement + ")";
        }
        return base;
    }
    
    // Getters et Setters
    
    public Long getBienId() {
        return bienId;
    }
    
    public void setBienId(Long bienId) {
        this.bienId = bienId;
    }
    
    public String getInstructionsAcces() {
        return instructionsAcces;
    }
    
    public void setInstructionsAcces(String instructionsAcces) {
        this.instructionsAcces = instructionsAcces;
    }
    
    public String getCodePortail() {
        return codePortail;
    }
    
    public void setCodePortail(String codePortail) {
        this.codePortail = codePortail;
    }
    
    public String getCodeImmeuble() {
        return codeImmeuble;
    }
    
    public void setCodeImmeuble(String codeImmeuble) {
        this.codeImmeuble = codeImmeuble;
    }
    
    public Integer getEtage() {
        return etage;
    }
    
    public void setEtage(Integer etage) {
        this.etage = etage;
    }
    
    public String getNumeroAppartement() {
        return numeroAppartement;
    }
    
    public void setNumeroAppartement(String numeroAppartement) {
        this.numeroAppartement = numeroAppartement;
    }
    
    public String getPointsInteret() {
        return pointsInteret;
    }
    
    public void setPointsInteret(String pointsInteret) {
        this.pointsInteret = pointsInteret;
    }
    
    @Override
    public String toString() {
        return "AdresseBien{" +
                "id=" + id +
                ", bienId=" + bienId +
                ", adresse='" + getAdresseFormatee() + '\'' +
                ", hasCodesAcces=" + hasCodesAcces() +
                '}';
    }
}
