package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modèle représentant une réservation (adapté au schéma réel)
 */
public class Reservation {

    // ==========================================
    // ATTRIBUTS PRINCIPAUX (selon BDD)
    // ==========================================

    private Long id;
    private String reference;
    private Long logementId;
    private Long locataireId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Voyageurs
    private Integer nbVoyageurs;
    private Integer nbAdultes;
    private Integer nbEnfants;

    // Tarification
    private BigDecimal prixNuit;
    private Integer nbNuits;
    private BigDecimal prixSousTotal;
    private BigDecimal fraisService;
    private BigDecimal fraisMenage;
    private BigDecimal reduction;
    private String codePromo;
    private BigDecimal prixTotal;
    private String devise;

    // Statut et gestion
    private StatutReservation statut;
    private String messageLocataire;
    private String reponseHote;

    // Dates
    private LocalDateTime dateReservation;
    private LocalDateTime dateConfirmation;
    private LocalDateTime dateAnnulation;

    // Annulation
    private String motifAnnulation;
    private String annuleePar; // LOCATAIRE, HOTE, ADMIN, SYSTEME
    private BigDecimal montantRembourse;

    // ==========================================
    // ATTRIBUTS SUPPLÉMENTAIRES (non en BDD)
    // ==========================================

    private String logementTitre;
    private String locataireNom;
    private String locatairePrenom;
    private String locataireEmail;
    private String hoteNom;
    private String hotePrenom;

    // ==========================================
    // CONSTRUCTEURS
    // ==========================================

    public Reservation() {
        this.devise = "EUR";
        this.statut = StatutReservation.EN_ATTENTE;
    }

    public Reservation(Long logementId, Long locataireId, LocalDate dateDebut,
                       LocalDate dateFin, Integer nbVoyageurs) {
        this();
        this.logementId = logementId;
        this.locataireId = locataireId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nbVoyageurs = nbVoyageurs;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Long getLogementId() {
        return logementId;
    }

    public void setLogementId(Long logementId) {
        this.logementId = logementId;
    }

    public Long getLocataireId() {
        return locataireId;
    }

    public void setLocataireId(Long locataireId) {
        this.locataireId = locataireId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getNbVoyageurs() {
        return nbVoyageurs;
    }

    public void setNbVoyageurs(Integer nbVoyageurs) {
        this.nbVoyageurs = nbVoyageurs;
    }

    public Integer getNbAdultes() {
        return nbAdultes;
    }

    public void setNbAdultes(Integer nbAdultes) {
        this.nbAdultes = nbAdultes;
    }

    public Integer getNbEnfants() {
        return nbEnfants;
    }

    public void setNbEnfants(Integer nbEnfants) {
        this.nbEnfants = nbEnfants;
    }

    public BigDecimal getPrixNuit() {
        return prixNuit;
    }

    public void setPrixNuit(BigDecimal prixNuit) {
        this.prixNuit = prixNuit;
    }

    public Integer getNbNuits() {
        return nbNuits;
    }

    public void setNbNuits(Integer nbNuits) {
        this.nbNuits = nbNuits;
    }

    public BigDecimal getPrixSousTotal() {
        return prixSousTotal;
    }

    public void setPrixSousTotal(BigDecimal prixSousTotal) {
        this.prixSousTotal = prixSousTotal;
    }

    public BigDecimal getFraisService() {
        return fraisService;
    }

    public void setFraisService(BigDecimal fraisService) {
        this.fraisService = fraisService;
    }

    public BigDecimal getFraisMenage() {
        return fraisMenage;
    }

    public void setFraisMenage(BigDecimal fraisMenage) {
        this.fraisMenage = fraisMenage;
    }

    public BigDecimal getReduction() {
        return reduction;
    }

    public void setReduction(BigDecimal reduction) {
        this.reduction = reduction;
    }

    public String getCodePromo() {
        return codePromo;
    }

    public void setCodePromo(String codePromo) {
        this.codePromo = codePromo;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public String getMessageLocataire() {
        return messageLocataire;
    }

    public void setMessageLocataire(String messageLocataire) {
        this.messageLocataire = messageLocataire;
    }

    public String getReponseHote() {
        return reponseHote;
    }

    public void setReponseHote(String reponseHote) {
        this.reponseHote = reponseHote;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public LocalDateTime getDateConfirmation() {
        return dateConfirmation;
    }

    public void setDateConfirmation(LocalDateTime dateConfirmation) {
        this.dateConfirmation = dateConfirmation;
    }

    public LocalDateTime getDateAnnulation() {
        return dateAnnulation;
    }

    public void setDateAnnulation(LocalDateTime dateAnnulation) {
        this.dateAnnulation = dateAnnulation;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }

    public String getAnnuleePar() {
        return annuleePar;
    }

    public void setAnnuleePar(String annuleePar) {
        this.annuleePar = annuleePar;
    }

    public BigDecimal getMontantRembourse() {
        return montantRembourse;
    }

    public void setMontantRembourse(BigDecimal montantRembourse) {
        this.montantRembourse = montantRembourse;
    }

    // Attributs supplémentaires

    public String getLogementTitre() {
        return logementTitre;
    }

    public void setLogementTitre(String logementTitre) {
        this.logementTitre = logementTitre;
    }

    public String getLocataireNom() {
        return locataireNom;
    }

    public void setLocataireNom(String locataireNom) {
        this.locataireNom = locataireNom;
    }

    public String getLocatairePrenom() {
        return locatairePrenom;
    }

    public void setLocatairePrenom(String locatairePrenom) {
        this.locatairePrenom = locatairePrenom;
    }

    public String getLocataireEmail() {
        return locataireEmail;
    }

    public void setLocataireEmail(String locataireEmail) {
        this.locataireEmail = locataireEmail;
    }

    public String getHoteNom() {
        return hoteNom;
    }

    public void setHoteNom(String hoteNom) {
        this.hoteNom = hoteNom;
    }

    public String getHotePrenom() {
        return hotePrenom;
    }

    public void setHotePrenom(String hotePrenom) {
        this.hotePrenom = hotePrenom;
    }

    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Obtenir le nom complet du locataire
     */
    public String getLocataireNomComplet() {
        if (locatairePrenom != null && locataireNom != null) {
            return locatairePrenom + " " + locataireNom;
        }
        return "";
    }

    /**
     * Obtenir le nom complet de l'hôte
     */
    public String getHoteNomComplet() {
        if (hotePrenom != null && hoteNom != null) {
            return hotePrenom + " " + hoteNom;
        }
        return "";
    }

    /**
     * Vérifier si la réservation est annulable
     */
    public boolean isAnnulable() {
        return statut == StatutReservation.EN_ATTENTE ||
                statut == StatutReservation.CONFIRMEE;
    }

    /**
     * Vérifier si la réservation est en cours
     */
    public boolean isEnCours() {
        return statut == StatutReservation.EN_COURS;
    }

    /**
     * Vérifier si la réservation est terminée
     */
    public boolean isTerminee() {
        return statut == StatutReservation.TERMINEE;
    }

    /**
     * Vérifier si la réservation est annulée
     */
    public boolean isAnnulee() {
        return statut == StatutReservation.ANNULEE;
    }

    /**
     * Obtenir le montant total formaté
     */
    public String getPrixTotalFormate() {
        if (prixTotal == null) return "0,00";
        return String.format("%,.2f", prixTotal);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", logementId=" + logementId +
                ", locataireId=" + locataireId +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", nbVoyageurs=" + nbVoyageurs +
                ", prixTotal=" + prixTotal +
                ", statut=" + statut +
                '}';
    }
}