package training.afpa.cda24060.squatrbnb.model.profil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Profil Hôte - correspond à la table hote_profil
 * Contient les données spécifiques aux utilisateurs avec le rôle HOTE
 */
public class HoteProfil {
    
    private Long utilisateurId;
    private String description;
    private String siret;
    private String raisonSociale;
    private boolean verifie;
    private LocalDateTime dateVerification;
    private Long verifiePar;
    private BigDecimal noteMoyenne;
    private int nbAvis;
    private int nbBiens;
    private BigDecimal revenusTotaux;
    private String iban;
    private String bic;
    
    // Constructeurs
    
    public HoteProfil() {
        this.verifie = false;
        this.noteMoyenne = BigDecimal.ZERO;
        this.nbAvis = 0;
        this.nbBiens = 0;
        this.revenusTotaux = BigDecimal.ZERO;
    }
    
    public HoteProfil(Long utilisateurId) {
        this();
        this.utilisateurId = utilisateurId;
    }
    
    // Méthodes utilitaires
    
    /**
     * Vérifie si l'hôte est un professionnel (a un SIRET)
     */
    public boolean isProfessionnel() {
        return siret != null && !siret.isBlank();
    }
    
    /**
     * Retourne la note formatée (ex: "4.8" ou "Nouveau")
     */
    public String getNoteFormatee() {
        if (noteMoyenne == null || noteMoyenne.compareTo(BigDecimal.ZERO) == 0) {
            return "Nouveau";
        }
        return noteMoyenne.setScale(1, RoundingMode.HALF_UP).toString();
    }

    /**
     * Vérifie si les informations bancaires sont complètes
     */
    public boolean hasInfosBancaires() {
        return iban != null && !iban.isBlank() && bic != null && !bic.isBlank();
    }
    
    /**
     * Vérifie si le profil est complet pour publier des biens
     */
    public boolean isProfilCompletPourPublication() {
        return description != null && !description.isBlank() && hasInfosBancaires();
    }
    
    // Getters et Setters
    
    public Long getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSiret() {
        return siret;
    }
    
    public void setSiret(String siret) {
        this.siret = siret;
    }
    
    public String getRaisonSociale() {
        return raisonSociale;
    }
    
    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public boolean isVerifie() {
        return verifie;
    }
    
    public void setVerifie(boolean verifie) {
        this.verifie = verifie;
    }
    
    public LocalDateTime getDateVerification() {
        return dateVerification;
    }
    
    public void setDateVerification(LocalDateTime dateVerification) {
        this.dateVerification = dateVerification;
    }
    
    public Long getVerifiePar() {
        return verifiePar;
    }
    
    public void setVerifiePar(Long verifiePar) {
        this.verifiePar = verifiePar;
    }
    
    public BigDecimal getNoteMoyenne() {
        return noteMoyenne;
    }
    
    public void setNoteMoyenne(BigDecimal noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }
    
    public int getNbAvis() {
        return nbAvis;
    }
    
    public void setNbAvis(int nbAvis) {
        this.nbAvis = nbAvis;
    }
    
    public int getNbBiens() {
        return nbBiens;
    }
    
    public void setNbBiens(int nbBiens) {
        this.nbBiens = nbBiens;
    }
    
    public BigDecimal getRevenusTotaux() {
        return revenusTotaux;
    }
    
    public void setRevenusTotaux(BigDecimal revenusTotaux) {
        this.revenusTotaux = revenusTotaux;
    }
    
    public String getIban() {
        return iban;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
    
    public String getBic() {
        return bic;
    }
    
    public void setBic(String bic) {
        this.bic = bic;
    }
    
    @Override
    public String toString() {
        return "HoteProfil{" +
                "utilisateurId=" + utilisateurId +
                ", verifie=" + verifie +
                ", nbBiens=" + nbBiens +
                ", noteMoyenne=" + getNoteFormatee() +
                ", professionnel=" + isProfessionnel() +
                '}';
    }
}
