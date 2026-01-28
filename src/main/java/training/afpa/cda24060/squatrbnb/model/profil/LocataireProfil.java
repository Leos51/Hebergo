package training.afpa.cda24060.squatrbnb.model.profil;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Profil Locataire - correspond à la table locataire_profil
 * Contient les données spécifiques aux utilisateurs avec le rôle LOCATAIRE
 */
public class LocataireProfil {
    
    private Long utilisateurId;
    private LocalDate dateNaissance;
    private String nationalite;

    private String pieceIdentiteNumero;
    private String pieceIdentiteUrl;
    private LocalDate pieceIdentiteExpiration;
    private boolean verifie;
    private LocalDateTime dateVerification;
    private int verifiePar;
    private int nbReservations;
    private int nbAvisDonnes;
    
    // Constructeurs
    
    public LocataireProfil() {
        this.verifie = false;
        this.nbReservations = 0;
        this.nbAvisDonnes = 0;
    }
    
    public LocataireProfil(Long utilisateurId) {
        this();
        this.utilisateurId = utilisateurId;
    }
    
    // Méthodes utilitaires
    
    /**
     * Calcule l'âge du locataire
     */
    public Integer getAge() {
        if (dateNaissance == null) return null;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
    
    /**
     * Vérifie si le locataire est majeur (>= 18 ans)
     */
    public boolean isMajeur() {
        Integer age = getAge();
        return age != null && age >= 18;
    }
    
    /**
     * Vérifie si la pièce d'identité est expirée
     */
    public boolean isPieceIdentiteExpiree() {
        if (pieceIdentiteExpiration == null) return false;
        return pieceIdentiteExpiration.isBefore(LocalDate.now());
    }
    
    /**
     * Vérifie si la pièce d'identité expire dans les 3 prochains mois
     */
    public boolean isPieceIdentiteExpireBientot() {
        if (pieceIdentiteExpiration == null) return false;
        LocalDate dansTrisMois = LocalDate.now().plusMonths(3);
        return pieceIdentiteExpiration.isBefore(dansTrisMois) && !isPieceIdentiteExpiree();
    }
    
    /**
     * Vérifie si le profil est complet pour réserver
     */
    public boolean isProfilCompletPourReservation() {
        return dateNaissance != null && isMajeur();
    }
    


    
    // Getters et Setters
    
    public Long getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public String getNationalite() {
        return nationalite;
    }
    
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }
    

    public String getPieceIdentiteNumero() {
        return pieceIdentiteNumero;
    }
    
    public void setPieceIdentiteNumero(String pieceIdentiteNumero) {
        this.pieceIdentiteNumero = pieceIdentiteNumero;
    }
    
    public String getPieceIdentiteUrl() {
        return pieceIdentiteUrl;
    }
    
    public void setPieceIdentiteUrl(String pieceIdentiteUrl) {
        this.pieceIdentiteUrl = pieceIdentiteUrl;
    }
    
    public LocalDate getPieceIdentiteExpiration() {
        return pieceIdentiteExpiration;
    }
    
    public void setPieceIdentiteExpiration(LocalDate pieceIdentiteExpiration) {
        this.pieceIdentiteExpiration = pieceIdentiteExpiration;
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
    
    public int getVerifiePar() {
        return verifiePar;
    }
    
    public void setVerifiePar(int verifiePar) {
        this.verifiePar = verifiePar;
    }
    
    public int getNbReservations() {
        return nbReservations;
    }
    
    public void setNbReservations(int nbReservations) {
        this.nbReservations = nbReservations;
    }
    
    public int getNbAvisDonnes() {
        return nbAvisDonnes;
    }
    
    public void setNbAvisDonnes(int nbAvisDonnes) {
        this.nbAvisDonnes = nbAvisDonnes;
    }
    
    @Override
    public String toString() {
        return "LocataireProfil{" +
                "utilisateurId=" + utilisateurId +
                ", age=" + getAge() +
                ", verifie=" + verifie +
                ", nbReservations=" + nbReservations +
                '}';
    }
}
