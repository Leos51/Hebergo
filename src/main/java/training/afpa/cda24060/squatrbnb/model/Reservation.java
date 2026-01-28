package training.afpa.cda24060.squatrbnb.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import training.afpa.cda24060.squatrbnb.model.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;



public class Reservation {


    private Long id;

    private String reference;
    private Logement logement;
    private Locataire locataire;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Integer nbVoyageurs;


    private BigDecimal prixNuit;


    private BigDecimal prixTotal;


    private BigDecimal fraisMenage;


    private BigDecimal fraisService;


    private BigDecimal montantHote;

    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    private LocalDateTime dateReservation;

    private LocalDateTime dateConfirmation;

    private LocalDateTime dateAnnulation;

    private String motifAnnulation;

    private String message;

    // Méthodes utilitaires
    public long getNbNuits() {
        if (dateDebut == null || dateFin == null) return 0;
        return ChronoUnit.DAYS.between(dateDebut, dateFin);
    }

    public boolean isAnnulable() {
        if (logement == null || logement.getDelaiAnnulation() == null) return false;
        LocalDate dateLimit = dateDebut.minusDays(logement.getDelaiAnnulation());
        return LocalDate.now().isBefore(dateLimit);
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;

    }
    public void setDateConfirmation( LocalDateTime dateConfirmation) {
        this.dateConfirmation = dateConfirmation;
    }
    public void setDateAnnulation(LocalDateTime dateAnnulation) {
        this.dateAnnulation = dateAnnulation;
    }
    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }


    protected void onCreate() {
        dateReservation = LocalDateTime.now();
        if (reference == null) {
            reference = "RES-" + System.currentTimeMillis();
        }
    }

    public Logement getLogement() {
        return this.logement;
    }
}