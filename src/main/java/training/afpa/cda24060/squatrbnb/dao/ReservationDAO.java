package training.afpa.cda24060.squatrbnb.dao;

import training.afpa.cda24060.squatrbnb.model.Reservation;
import training.afpa.cda24060.squatrbnb.model.enums.StatutReservation;
import training.afpa.cda24060.squatrbnb.utilitaires.DataSourceProvider;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour les réservations - Version JDBC pure adaptée au schéma réel
 */
public class ReservationDAO {

    // ==========================================
    // MÉTHODES POUR L'HÔTE
    // ==========================================

    /**
     * Récupérer les réservations récentes d'un hôte
     */
    public List<Reservation> findRecentesByHote(Long hoteId, int limit) throws SQLException {
        String sql = """
            SELECT r.*, 
                   l.titre as logement_titre,
                   u.prenom as locataire_prenom, 
                   u.nom as locataire_nom,
                   u.email as locataire_email
            FROM reservation r
            INNER JOIN logement l ON r.logement_id = l.id
            INNER JOIN utilisateur u ON r.locataire_id = u.id
            WHERE l.hote_id = ?
            ORDER BY r.date_reservation DESC
            LIMIT ?
        """;

        List<Reservation> reservations = new ArrayList<>();

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, hoteId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        }

        return reservations;
    }

    /**
     * Récupérer les réservations d'un hôte avec filtres et pagination
     */
    public List<Reservation> findByHote(Long hoteId, String statut, Long logementId,
                                        int limit, int offset) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT r.*, 
                   l.titre as logement_titre,
                   u.prenom as locataire_prenom, 
                   u.nom as locataire_nom,
                   u.email as locataire_email
            FROM reservation r
            INNER JOIN logement l ON r.logement_id = l.id
            INNER JOIN utilisateur u ON r.locataire_id = u.id
            WHERE l.hote_id = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(hoteId);

        // Filtre par statut
        if (statut != null && !statut.trim().isEmpty()) {
            sql.append(" AND r.statut = ?");
            params.add(statut);
        }

        // Filtre par logement
        if (logementId != null) {
            sql.append(" AND r.logement_id = ?");
            params.add(logementId);
        }

        sql.append(" ORDER BY r.date_reservation DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        List<Reservation> reservations = new ArrayList<>();

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        }

        return reservations;
    }

    /**
     * Compter les réservations d'un hôte
     */
    public int countByHote(Long hoteId, String statut, Long logementId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) 
            FROM reservation r
            INNER JOIN logement l ON r.logement_id = l.id
            WHERE l.hote_id = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(hoteId);

        if (statut != null && !statut.trim().isEmpty()) {
            sql.append(" AND r.statut = ?");
            params.add(statut);
        }

        if (logementId != null) {
            sql.append(" AND r.logement_id = ?");
            params.add(logementId);
        }

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Compter les réservations d'un hôte par statut
     */
    public int countByHoteAndStatut(Long hoteId, String statut) throws SQLException {
        String sql = """
            SELECT COUNT(*) 
            FROM reservation r
            INNER JOIN logement l ON r.logement_id = l.id
            WHERE l.hote_id = ? AND r.statut = ?
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, hoteId);
            ps.setString(2, statut);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Compter les réservations actives d'un logement
     */
    public int countReservationsActives(Long logementId) throws SQLException {
        String sql = """
            SELECT COUNT(*) 
            FROM reservation 
            WHERE logement_id = ? 
            AND statut IN ('EN_ATTENTE', 'CONFIRMEE', 'EN_COURS')
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // ==========================================
    // MÉTHODES POUR LE LOCATAIRE
    // ==========================================

    /**
     * Récupérer les réservations d'un locataire
     */
    public List<Reservation> findByLocataire(Long locataireId) throws SQLException {
        String sql = """
            SELECT r.*, 
                   l.titre as logement_titre,
                   u.prenom as hote_prenom, 
                   u.nom as hote_nom
            FROM reservation r
            INNER JOIN logement l ON r.logement_id = l.id
            INNER JOIN utilisateur u ON l.hote_id = u.id
            WHERE r.locataire_id = ?
            ORDER BY r.date_reservation DESC
        """;

        List<Reservation> reservations = new ArrayList<>();

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, locataireId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        }

        return reservations;
    }

    // ==========================================
    // CRUD BASIQUE
    // ==========================================

    /**
     * Créer une réservation
     */
    public Long create(Reservation reservation) throws SQLException {
        String sql = """
            INSERT INTO reservation (
                reference, logement_id, locataire_id, date_debut, date_fin,
                nb_voyageurs, nb_adultes, nb_enfants, 
                prix_nuit, nb_nuits, prix_sous_total,
                frais_service, frais_menage, reduction, code_promo,
                prix_total, devise, statut, message_locataire,
                date_reservation
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, reservation.getReference());
            ps.setLong(2, reservation.getLogementId());
            ps.setLong(3, reservation.getLocataireId());
            ps.setDate(4, Date.valueOf(reservation.getDateDebut()));
            ps.setDate(5, Date.valueOf(reservation.getDateFin()));
            ps.setInt(6, reservation.getNbVoyageurs());
            ps.setInt(7, reservation.getNbAdultes());
            ps.setInt(8, reservation.getNbEnfants());
            ps.setBigDecimal(9, reservation.getPrixNuit());
            ps.setInt(10, reservation.getNbNuits());
            ps.setBigDecimal(11, reservation.getPrixSousTotal());
            ps.setBigDecimal(12, reservation.getFraisService());
            ps.setBigDecimal(13, reservation.getFraisMenage());
            ps.setBigDecimal(14, reservation.getReduction());
            ps.setString(15, reservation.getCodePromo());
            ps.setBigDecimal(16, reservation.getPrixTotal());
            ps.setString(17, reservation.getDevise() != null ? reservation.getDevise() : "EUR");
            ps.setString(18, reservation.getStatut() != null ?
                    reservation.getStatut().name() : "EN_ATTENTE");
            ps.setString(19, reservation.getMessageLocataire());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Échec de création de la réservation");
            }
        }
    }

    /**
     * Récupérer une réservation par ID
     */
    public Reservation findById(Long id) throws SQLException {
        String sql = """
            SELECT r.*, 
                   l.titre as logement_titre,
                   u.prenom as locataire_prenom, 
                   u.nom as locataire_nom
            FROM reservation r
            INNER JOIN logement l ON r.logement_id = l.id
            INNER JOIN utilisateur u ON r.locataire_id = u.id
            WHERE r.id = ?
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
                return null;
            }
        }
    }

    /**
     * Mettre à jour le statut d'une réservation
     */
    public boolean updateStatut(Long reservationId, String statut) throws SQLException {
        String sql = """
            UPDATE reservation 
            SET statut = ?
            WHERE id = ?
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            ps.setLong(2, reservationId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Confirmer une réservation
     */
    public boolean confirmer(Long reservationId) throws SQLException {
        String sql = """
            UPDATE reservation 
            SET statut = 'CONFIRMEE',
                date_confirmation = NOW()
            WHERE id = ?
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, reservationId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Annuler une réservation
     */
    public boolean annuler(Long reservationId, String motif, String annuleePar) throws SQLException {
        String sql = """
            UPDATE reservation 
            SET statut = 'ANNULEE', 
                motif_annulation = ?,
                annulee_par = ?,
                date_annulation = NOW()
            WHERE id = ?
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, motif);
            ps.setString(2, annuleePar);
            ps.setLong(3, reservationId);

            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // VÉRIFICATIONS
    // ==========================================

    /**
     * Vérifier si un logement est disponible pour une période
     */
    public boolean isLogementDisponible(Long logementId, LocalDate dateDebut, LocalDate dateFin)
            throws SQLException {
        String sql = """
            SELECT COUNT(*) 
            FROM reservation 
            WHERE logement_id = ? 
            AND statut IN ('CONFIRMEE', 'EN_COURS')
            AND (
                (date_debut BETWEEN ? AND ?) OR
                (date_fin BETWEEN ? AND ?) OR
                (date_debut <= ? AND date_fin >= ?)
            )
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);
            ps.setDate(2, Date.valueOf(dateDebut));
            ps.setDate(3, Date.valueOf(dateFin));
            ps.setDate(4, Date.valueOf(dateDebut));
            ps.setDate(5, Date.valueOf(dateFin));
            ps.setDate(6, Date.valueOf(dateDebut));
            ps.setDate(7, Date.valueOf(dateFin));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        }
    }

    // ==========================================
    // MAPPING
    // ==========================================

    /**
     * Mapper un ResultSet vers un objet Reservation (adapté au schéma réel)
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();

        reservation.setId(rs.getLong("id"));
        reservation.setReference(rs.getString("reference"));
        reservation.setLogementId(rs.getLong("logement_id"));
        reservation.setLocataireId(rs.getLong("locataire_id"));

        // Dates
        Date dateDebut = rs.getDate("date_debut");
        if (dateDebut != null) {
            reservation.setDateDebut(dateDebut.toLocalDate());
        }

        Date dateFin = rs.getDate("date_fin");
        if (dateFin != null) {
            reservation.setDateFin(dateFin.toLocalDate());
        }

        // Nombres de voyageurs
        reservation.setNbVoyageurs(rs.getInt("nb_voyageurs"));
        reservation.setNbAdultes(rs.getInt("nb_adultes"));
        reservation.setNbEnfants(rs.getInt("nb_enfants"));

        // Prix et montants
        reservation.setPrixNuit(rs.getBigDecimal("prix_nuit"));
        reservation.setNbNuits(rs.getInt("nb_nuits"));
        reservation.setPrixSousTotal(rs.getBigDecimal("prix_sous_total"));
        reservation.setFraisService(rs.getBigDecimal("frais_service"));
        reservation.setFraisMenage(rs.getBigDecimal("frais_menage"));
        reservation.setReduction(rs.getBigDecimal("reduction"));
        reservation.setCodePromo(rs.getString("code_promo"));
        reservation.setPrixTotal(rs.getBigDecimal("prix_total"));
        reservation.setDevise(rs.getString("devise"));

        // Statut
        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            try {
                reservation.setStatut(StatutReservation.valueOf(statutStr));
            } catch (IllegalArgumentException e) {
                reservation.setStatut(StatutReservation.EN_ATTENTE);
            }
        }

        // Messages
        reservation.setMessageLocataire(rs.getString("message_locataire"));
        reservation.setReponseHote(rs.getString("reponse_hote"));

        // Dates de gestion
        Timestamp dateReservation = rs.getTimestamp("date_reservation");
        if (dateReservation != null) {
            reservation.setDateReservation(dateReservation.toLocalDateTime());
        }

        Timestamp dateConfirmation = rs.getTimestamp("date_confirmation");
        if (dateConfirmation != null) {
            reservation.setDateConfirmation(dateConfirmation.toLocalDateTime());
        }

        Timestamp dateAnnulation = rs.getTimestamp("date_annulation");
        if (dateAnnulation != null) {
            reservation.setDateAnnulation(dateAnnulation.toLocalDateTime());
        }

        reservation.setMotifAnnulation(rs.getString("motif_annulation"));
        reservation.setAnnuleePar(rs.getString("annulee_par"));
        reservation.setMontantRembourse(rs.getBigDecimal("montant_rembourse"));

        // Informations supplémentaires (si présentes dans le JOIN)
        try {
            reservation.setLogementTitre(rs.getString("logement_titre"));
        } catch (SQLException ignored) {}

        try {
            reservation.setLocataireNom(rs.getString("locataire_nom"));
            reservation.setLocatairePrenom(rs.getString("locataire_prenom"));
        } catch (SQLException ignored) {}

        try {
            reservation.setLocataireEmail(rs.getString("locataire_email"));
        } catch (SQLException ignored) {}

        return reservation;
    }
}