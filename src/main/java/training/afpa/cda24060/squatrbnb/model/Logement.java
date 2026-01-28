package training.afpa.cda24060.squatrbnb.model;

import training.afpa.cda24060.squatrbnb.model.enums.StatutLogement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Logement {

    private Long id;
    private Long hoteId;
    private Long typeLogementId;
    private String titre;
    private String description;
    private Integer nbChambres;
    private Integer nbLits;
    private Integer nbSallesBain;
    private Integer capaciteMax;
    private BigDecimal superficie;
    private BigDecimal prixNuit;
    private BigDecimal fraisMenage;
    private StatutLogement statut;


    // Règlement
    private String heureArrivee;
    private String heureDepart;
    private String reglementInterieur;
    private Integer delaiAnnulation;

    // Dates
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Relations (chargées à la demande)
    private TypeLogement typeLogement;
    private List<PhotoLogement> photos = new ArrayList<>();
    private List<Equipement> equipements = new ArrayList<>();
    private List<Long> equipementIds = new ArrayList<>(); // Pour formulaires

    // Champs calculés
    private String photoPrincipale;
    private BigDecimal noteMoyenne;
    private int nbAvis;
    private int nbReservations;

    // Infos hôte (pour affichage)
    private String hoteNom;
    private String hotePrenom;
    private String hotePhoto;
    private boolean hoteVerifie;

    //  Adresse
    private AdresseBien adresse;
    public AdresseBien getAdresse() {
        return adresse;
    }
    public void setAdresse(AdresseBien adresse) { this.adresse = adresse; }
    // Méthodes utilitaires (délégation vers Adresse)
    public String getAdresseComplete() {
        return adresse != null ? adresse.getAdresseFormatee() : "";
    }

    public String getVille() {
        return adresse != null ? adresse.getVille() : null;
    }

    public boolean hasCoordinates() {
        return adresse != null && adresse.hasCoordinates();
    }



    public String getHoteNomComplet() {
        return (hotePrenom != null ? hotePrenom : "") + " " + (hoteNom != null ? hoteNom : "");
    }

    public boolean isDisponible() {
        return statut == StatutLogement.DISPONIBLE;
    }

    public boolean isBrouillon() {
        return statut == StatutLogement.BROUILLON;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHoteId() { return hoteId; }
    public void setHoteId(Long hoteId) { this.hoteId = hoteId; }

//    public Optional<Hote> getHote() {
//        HoteDAO hoteDAO = new HoteDAO();
//       Optional<Hote> hote = hoteDAO.findById(this.getHoteId());
//        return hote;
//    }

    public Long getTypeLogementId() { return typeLogementId; }
    public void setTypeLogementId(Long typeLogementId) { this.typeLogementId = typeLogementId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getNbChambres() { return nbChambres; }
    public void setNbChambres(Integer nbChambres) { this.nbChambres = nbChambres; }

    public Integer getNbLits() { return nbLits; }
    public void setNbLits(Integer nbLits) { this.nbLits = nbLits; }

    public Integer getNbSallesBain() { return nbSallesBain; }
    public void setNbSallesBain(Integer nbSallesBain) { this.nbSallesBain = nbSallesBain; }

    public Integer getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(Integer capaciteMax) { this.capaciteMax = capaciteMax; }

    public BigDecimal getSuperficie() { return superficie; }
    public void setSuperficie(BigDecimal superficie) { this.superficie = superficie; }

    public BigDecimal getPrixNuit() { return prixNuit; }
    public void setPrixNuit(BigDecimal prixNuit) { this.prixNuit = prixNuit; }

    public BigDecimal getFraisMenage() { return fraisMenage; }
    public void setFraisMenage(BigDecimal fraisMenage) { this.fraisMenage = fraisMenage; }

    public StatutLogement getStatut() { return statut; }
    public void setStatut(StatutLogement statut) { this.statut = statut; }

    public String getHeureArrivee() { return heureArrivee; }
    public void setHeureArrivee(String heureArrivee) { this.heureArrivee = heureArrivee; }

    public String getHeureDepart() { return heureDepart; }
    public void setHeureDepart(String heureDepart) { this.heureDepart = heureDepart; }

    public String getReglementInterieur() { return reglementInterieur; }
    public void setReglementInterieur(String reglementInterieur) { this.reglementInterieur = reglementInterieur; }

    public Integer getDelaiAnnulation() { return delaiAnnulation; }
    public void setDelaiAnnulation(Integer delaiAnnulation) { this.delaiAnnulation = delaiAnnulation; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDateTime getDateModification() { return dateModification; }
    public void setDateModification(LocalDateTime dateModification) { this.dateModification = dateModification; }

    public TypeLogement getTypeLogement() { return typeLogement; }
    public void setTypeLogement(TypeLogement typeLogement) { this.typeLogement = typeLogement; }

    public List<PhotoLogement> getPhotos() { return photos; }
    public void setPhotos(List<PhotoLogement> photos) { this.photos = photos; }

    public List<Equipement> getEquipements() { return equipements; }
    public void setEquipements(List<Equipement> equipements) { this.equipements = equipements; }

    public List<Long> getEquipementIds() { return equipementIds; }
    public void setEquipementIds(List<Long> equipementIds) { this.equipementIds = equipementIds; }

    public String getPhotoPrincipale() { return photoPrincipale; }
    public void setPhotoPrincipale(String photoPrincipale) { this.photoPrincipale = photoPrincipale; }

    public BigDecimal getNoteMoyenne() { return noteMoyenne; }
    public void setNoteMoyenne(BigDecimal noteMoyenne) { this.noteMoyenne = noteMoyenne; }

    public int getNbAvis() { return nbAvis; }
    public void setNbAvis(int nbAvis) { this.nbAvis = nbAvis; }

    public int getNbReservations() { return nbReservations; }
    public void setNbReservations(int nbReservations) { this.nbReservations = nbReservations; }

    public String getHoteNom() { return hoteNom; }
    public void setHoteNom(String hoteNom) { this.hoteNom = hoteNom; }

    public String getHotePrenom() { return hotePrenom; }
    public void setHotePrenom(String hotePrenom) { this.hotePrenom = hotePrenom; }

    public String getHotePhoto() { return hotePhoto; }
    public void setHotePhoto(String hotePhoto) { this.hotePhoto = hotePhoto; }

    public boolean isHoteVerifie() { return hoteVerifie; }
    public void setHoteVerifie(boolean hoteVerifie) { this.hoteVerifie = hoteVerifie; }



}