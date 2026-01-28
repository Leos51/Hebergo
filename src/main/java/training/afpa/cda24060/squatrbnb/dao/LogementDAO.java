package training.afpa.cda24060.squatrbnb.dao;

import training.afpa.cda24060.squatrbnb.model.*;
import training.afpa.cda24060.squatrbnb.model.enums.StatutLogement;
import training.afpa.cda24060.squatrbnb.utilitaires.DataSourceProvider;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * DAO pour la gestion des logements
 * Utilise les procédures stockées existantes
 */
public class LogementDAO {

    /**
     * Créer un logement via sp_creer_logement
     * @param logement
     * @return ID du  Logement
     */
    public Long save(Logement logement) {
        String sql = "{CALL sp_creer_logement(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DataSourceProvider.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // Paramètres IN
            cs.setLong(1, logement.getHoteId());                              // p_hote_id
            cs.setObject(2, logement.getTypeLogementId());                    // p_type_id
            cs.setString(3, logement.getTitre());                             // p_titre
            cs.setString(4, logement.getDescription());                       // p_description

            // Adresse
            AdresseBien a = logement.getAdresse();
            if (a != null) {
                cs.setString(5, a.getAdresse());
                cs.setString(6, a.getCodePostal());
                cs.setString(7, a.getVille());
                cs.setString(8, a.getRegion());
                cs.setString(9, a.getPays());
                cs.setBigDecimal(10, a.getLatitude());
                cs.setBigDecimal(11, a.getLongitude());
            }

            // Caractéristiques
            cs.setBigDecimal(12, logement.getSuperficie());                   // p_superficie
            cs.setObject(13, logement.getNbChambres());                       // p_chambres
            cs.setObject(14, logement.getNbLits());                           // p_lits
            cs.setObject(15, logement.getNbSallesBain());                     // p_sdb
            cs.setObject(16, logement.getCapaciteMax());                      // p_capacite

            // Tarification
            cs.setBigDecimal(17, logement.getPrixNuit());                     // p_prix
            cs.setBigDecimal(18, logement.getFraisMenage());                  // p_menage

            // Paramètres OUT
            cs.registerOutParameter(19, Types.INTEGER);                       // p_bien_id
            cs.registerOutParameter(20, Types.VARCHAR);                       // p_message

            cs.execute();

            Long bienId = cs.getLong(19);
            String message = cs.getString(20);

            System.out.println("sp_creer_logement: " + message + " (ID: " + bienId + ")");

            if (bienId != null) {
                logement.setId(bienId);

                // Sauvegarder les équipements (pas dans la procédure)
                if (logement.getEquipementIds() != null && !logement.getEquipementIds().isEmpty()) {
                    saveEquipements(bienId, logement.getEquipementIds());
                }

                return bienId;
            }

        } catch (SQLException e) {
            System.err.println("Erreur sp_creer_logement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Créer un logement complet avec adresse et équipements
     * @param logement
     * @param hoteId
     * @return ID du logement
     * @throws SQLException
     */
    public Long createComplete(Logement logement, Long hoteId) throws SQLException {
        Connection conn = null;
        try {
            conn = DataSourceProvider.getConnection();
            conn.setAutoCommit(false);

            // 1. Créer l'adresse
            Long adresseId = createAdresse(conn, logement.getAdresse());

            // 2. Créer le logement
            String sql = """
                INSERT INTO logement (
                    hote_id, type_logement_id, titre, description,
                    adresse_id, nb_chambres, nb_lits, nb_salles_bain, capacite_max,
                    superficie, prix_nuit, frais_menage, heure_arrivee, heure_depart,
                    reglement_interieur, delai_annulation, statut, date_creation
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
            """;

            Long logementId;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                int i = 1;
                ps.setLong(i++, hoteId);
                ps.setObject(i++, logement.getTypeLogementId());
                ps.setString(i++, logement.getTitre());
                ps.setString(i++, logement.getDescription());
                ps.setLong(i++, adresseId);
                ps.setObject(i++, logement.getNbChambres());
                ps.setObject(i++, logement.getNbLits());
                ps.setObject(i++, logement.getNbSallesBain());
                ps.setObject(i++, logement.getCapaciteMax());
                ps.setBigDecimal(i++, logement.getSuperficie());
                ps.setBigDecimal(i++, logement.getPrixNuit());
                ps.setBigDecimal(i++, logement.getFraisMenage());
                ps.setString(i++, logement.getHeureArrivee());
                ps.setString(i++, logement.getHeureDepart());
                ps.setString(i++, logement.getReglementInterieur());
                ps.setObject(i++, logement.getDelaiAnnulation());
                ps.setString(i++, logement.getStatut() != null ? logement.getStatut().name() : StatutLogement.BROUILLON.name());

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        logementId = keys.getLong(1);
                    } else {
                        throw new SQLException("Échec de création du logement");
                    }
                }
            }

            // 3. Ajouter les équipements
            if (logement.getEquipementIds() != null && !logement.getEquipementIds().isEmpty()) {
                saveEquipements(conn, logementId, logement.getEquipementIds());
            }

            conn.commit();
            return logementId;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Créer une adresse de logement
     * @param conn
     * @param adresse
     * @return ID de l'adresse
     * @throws SQLException
     */
    private Long createAdresse(Connection conn, AdresseBien adresse) throws SQLException {
        if (adresse == null) {
            throw new SQLException("Adresse obligatoire");
        }

        String sql = """
            INSERT INTO adresse (
                adresse, code_postal, ville, region, pays,
                latitude, longitude, date_creation
            ) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, adresse.getAdresse());
            ps.setString(2, adresse.getCodePostal());
            ps.setString(3, adresse.getVille());
            ps.setString(4, adresse.getRegion());
            ps.setString(5, adresse.getPays() != null ? adresse.getPays() : "France");
            ps.setBigDecimal(6, adresse.getLatitude());
            ps.setBigDecimal(7, adresse.getLongitude());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Échec de création de l'adresse");
            }
        }
    }

    // ==========================================
    // MISE À JOUR COMPLÈTE
    // ==========================================

    /**
     * Mettre à jour un logement complet (avec vérification du propriétaire)
     * @param logement
     * @param hoteId
     * @return boolean
     * @throws SQLException
     */
    public boolean updateComplete(Logement logement, Long hoteId) throws SQLException {
        // Vérifier que le logement appartient bien à cet hôte
        if (!verifyOwnership(logement.getId(), hoteId)) {
            throw new SQLException("Vous n'êtes pas autorisé à modifier ce logement");
        }

        Connection conn = null;
        try {
            conn = DataSourceProvider.getConnection();
            conn.setAutoCommit(false);

            // 1. Mettre à jour l'adresse si elle existe
            if (logement.getAdresse() != null && logement.getAdresse().getId() != null) {
                updateAdresse(conn, logement.getAdresse());
            }

            // 2. Mettre à jour le logement
            String sql = """
                UPDATE logement SET
                    type_logement_id = ?, titre = ?, description = ?,
                    nb_chambres = ?, nb_lits = ?, nb_salles_bain = ?, capacite_max = ?,
                    superficie = ?, prix_nuit = ?, frais_menage = ?,
                    heure_arrivee = ?, heure_depart = ?, reglement_interieur = ?,
                    delai_annulation = ?, date_modification = NOW()
                WHERE id = ? AND hote_id = ?
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                int i = 1;
                ps.setObject(i++, logement.getTypeLogementId());
                ps.setString(i++, logement.getTitre());
                ps.setString(i++, logement.getDescription());
                ps.setObject(i++, logement.getNbChambres());
                ps.setObject(i++, logement.getNbLits());
                ps.setObject(i++, logement.getNbSallesBain());
                ps.setObject(i++, logement.getCapaciteMax());
                ps.setBigDecimal(i++, logement.getSuperficie());
                ps.setBigDecimal(i++, logement.getPrixNuit());
                ps.setBigDecimal(i++, logement.getFraisMenage());
                ps.setString(i++, logement.getHeureArrivee());
                ps.setString(i++, logement.getHeureDepart());
                ps.setString(i++, logement.getReglementInterieur());
                ps.setObject(i++, logement.getDelaiAnnulation());
                ps.setLong(i++, logement.getId());
                ps.setLong(i++, hoteId);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("Logement non trouvé ou non autorisé");
                }
            }

            // 3. Mettre à jour les équipements
            if (logement.getEquipementIds() != null) {
                deleteEquipements(logement.getId());
                if (!logement.getEquipementIds().isEmpty()) {
                    saveEquipements(conn, logement.getId(), logement.getEquipementIds());
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Mettre à jour une adresse
     * @param conn
     * @param adresse
     * @throws SQLException
     */
    private void updateAdresse(Connection conn, AdresseBien adresse) throws SQLException {
        String sql = """
            UPDATE adresse SET
                adresse = ?, code_postal = ?, ville = ?, region = ?, pays = ?,
                latitude = ?, longitude = ?, date_modification = NOW()
            WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adresse.getAdresse());
            ps.setString(2, adresse.getCodePostal());
            ps.setString(3, adresse.getVille());
            ps.setString(4, adresse.getRegion());
            ps.setString(5, adresse.getPays());
            ps.setBigDecimal(6, adresse.getLatitude());
            ps.setBigDecimal(7, adresse.getLongitude());
            ps.setLong(8, adresse.getId());

            ps.executeUpdate();
        }
    }

    // ==========================================
    // SUPPRESSION SÉCURISÉE
    // ==========================================

    /**
     * Supprimer un logement (vérifie qu'il n'y a pas de réservations actives)
     * @param logementId
     * @param hoteId
     * @return
     * @throws SQLException
     */
    public boolean deleteSecure(Long logementId, Long hoteId) throws SQLException {
        // Vérifier que le logement appartient bien à cet hôte
        if (!verifyOwnership(logementId, hoteId)) {
            throw new SQLException("Vous n'êtes pas autorisé à supprimer ce logement");
        }

        // Vérifier qu'il n'y a pas de réservations actives
        if (hasActiveReservations(logementId)) {
            throw new SQLException("Impossible de supprimer : des réservations actives existent");
        }

        Connection conn = null;
        try {
            conn = DataSourceProvider.getConnection();
            conn.setAutoCommit(false);

            // 1. Supprimer les équipements
            deleteEquipements(logementId);

            // 2. Supprimer les photos
            deletePhotos(conn, logementId);

            // 3. Récupérer l'ID de l'adresse
            Long adresseId = getAdresseId(conn, logementId);

            // 4. Supprimer le logement
            String sql = "DELETE FROM logement WHERE id = ? AND hote_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, logementId);
                ps.setLong(2, hoteId);
                int rows = ps.executeUpdate();

                if (rows == 0) {
                    throw new SQLException("Logement non trouvé");
                }
            }

            // 5. Supprimer l'adresse
            if (adresseId != null) {
                deleteAdresse(conn, adresseId);
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Archiver un logement (soft delete)
     * @param logementId
     * @param hoteId
     * @return boolean
     * @throws SQLException
     */
    public boolean archive(Long logementId, Long hoteId) throws SQLException {
        if (!verifyOwnership(logementId, hoteId)) {
            throw new SQLException("Vous n'êtes pas autorisé à archiver ce logement");
        }

        String sql = "UPDATE logement SET statut = 'ARCHIVE', date_modification = NOW() WHERE id = ? AND hote_id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);
            ps.setLong(2, hoteId);

            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // MÉTHODES UTILITAIRES PRIVÉES
    // ==========================================

    /**
     * Vérifier que le logement appartient à l'hôte
     * @param logementId
     * @param hoteId
     * @return boolean
     * @throws SQLException
     */
    private boolean verifyOwnership(Long logementId, Long hoteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM logement WHERE id = ? AND hote_id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);
            ps.setLong(2, hoteId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Vérifier s'il y a des réservations actives
     * @param logementId
     * @return boolean
     * @throws SQLException
     */
    private boolean hasActiveReservations(Long logementId) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM reservation 
            WHERE logement_id = ? 
            AND statut IN ('EN_ATTENTE', 'CONFIRMEE', 'EN_COURS')
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Récupérer l'ID de l'adresse d'un logement
     * @param conn
     * @param logementId
     * @return ID adresse
     * @throws SQLException
     */
    private Long getAdresseId(Connection conn, Long logementId) throws SQLException {
        String sql = "SELECT adresse_id FROM logement WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, logementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject("adresse_id", Long.class);
                }
                return null;
            }
        }
    }

    /**
     * Supprimer une adresse
     * @param conn
     * @param adresseId
     * @throws SQLException
     */
    private void deleteAdresse(Connection conn, Long adresseId) throws SQLException {
        String sql = "DELETE FROM adresse WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, adresseId);
            ps.executeUpdate();
        }
    }


    /**
     * Supprimer les photos d'un logement
     * @param conn
     * @param logementId
     * @throws SQLException
     */
    private void deletePhotos(Connection conn, Long logementId) throws SQLException {
        String sql = "DELETE FROM photo_logement WHERE logement_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, logementId);
            ps.executeUpdate();
        }
    }


    /**
     * Sauvegarder les équipements (version avec connexion existante)
     * @param conn
     * @param logementId
     * @param equipementIds
     * @throws SQLException
     */
    private void saveEquipements(Connection conn, Long logementId, List<Long> equipementIds) throws SQLException {
        String sql = "INSERT INTO logement_equipement (logement_id, equipement_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Long equipementId : equipementIds) {
                ps.setLong(1, logementId);
                ps.setLong(2, equipementId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Sauvegarder les équipements (version avec connexion existante)
     * @param logementId
     * @param equipementIds
     */
    private void saveEquipements(Long logementId, List<Long> equipementIds) {
        String sql = "INSERT INTO logement_equipement (logement_id, equipement_id) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Long equipementId : equipementIds) {
                ps.setLong(1, logementId);
                ps.setLong(2, equipementId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprimer les équipements (version avec connexion existante)
     * @param logementId
     * @throws SQLException
     */
    private void deleteEquipements(Long logementId) {
        String sql = "DELETE FROM logement_equipement WHERE logement_id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // GESTION DES PHOTOS
    // ==========================================

    /**
     * Ajouter une photo
     * @param photo
     * @param hoteId
     * @return ID photo
     * @throws SQLException
     */
    public Long addPhoto(PhotoLogement photo, Long hoteId) throws SQLException {
        // Vérifier que le logement appartient à l'hôte
        if (!verifyOwnership(photo.getLogementId(), hoteId)) {
            throw new SQLException("Non autorisé");
        }

        String sql = "INSERT INTO photo_logement (logement_id, url, description, est_principale, ordre, date_ajout) VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, photo.getLogementId());
            ps.setString(2, photo.getUrl());
            ps.setString(3, photo.getDescription());
            ps.setBoolean(4, photo.isEstPrincipale());
            ps.setInt(5, photo.getOrdre());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Échec d'ajout de la photo");
            }
        }
    }

    /**
     * Supprimer une photo
     * @param photoId
     * @param hoteId
     * @return boolean
     * @throws SQLException
     */
    public boolean deletePhoto(Long photoId, Long hoteId) throws SQLException {
        String sql = """
            DELETE FROM photo_logement 
            WHERE id = ? 
            AND logement_id IN (SELECT id FROM logement WHERE hote_id = ?)
        """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, photoId);
            ps.setLong(2, hoteId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Définir une photo comme principale
     * @param photoId
     * @param logementId
     * @param hoteId
     * @return boolean
     * @throws SQLException
     */
    public boolean setPrincipalPhoto(Long photoId, Long logementId, Long hoteId) throws SQLException {
        if (!verifyOwnership(logementId, hoteId)) {
            throw new SQLException("Non autorisé");
        }

        Connection conn = null;
        try {
            conn = DataSourceProvider.getConnection();
            conn.setAutoCommit(false);

            // 1. Retirer le statut principal de toutes les photos
            String sql1 = "UPDATE photo_logement SET est_principale = FALSE WHERE logement_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setLong(1, logementId);
                ps.executeUpdate();
            }

            // 2. Définir la nouvelle photo principale
            String sql2 = "UPDATE photo_logement SET est_principale = TRUE WHERE id = ? AND logement_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setLong(1, photoId);
                ps.setLong(2, logementId);
                int rows = ps.executeUpdate();

                if (rows == 0) {
                    throw new SQLException("Photo non trouvée");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ==========================================
    // RECHERCHE avec PROCÉDURE STOCKÉE
    // ==========================================

    /**
     * Recherche via sp_rechercher_logements
     * @param ville
     * @param dateDebut
     * @param dateFin
     * @param nbVoyageurs
     * @param prixMin
     * @param prixMax
     * @param typeLogementId
     * @return Liste de Logements
     */
    public List<Logement> searchWithProcedure(String ville, LocalDate dateDebut, LocalDate dateFin,
                                              Integer nbVoyageurs, BigDecimal prixMin, BigDecimal prixMax,
                                              Long typeLogementId) {

        List<Logement> logements = new ArrayList<>();
        String sql = "{CALL sp_rechercher_logements(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DataSourceProvider.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, ville);                                           // p_ville
            cs.setDate(2, dateDebut != null ? Date.valueOf(dateDebut) : null);// p_date_debut
            cs.setDate(3, dateFin != null ? Date.valueOf(dateFin) : null);    // p_date_fin
            cs.setObject(4, nbVoyageurs);                                     // p_nb_voyageurs
            cs.setBigDecimal(5, prixMin);                                     // p_prix_min
            cs.setBigDecimal(6, prixMax);                                     // p_prix_max
            cs.setObject(7, typeLogementId);                                  // p_type_logement_id

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    logements.add(mapToLogementFromProcedure(rs));
                }
            }

            System.out.println("sp_rechercher_logements: " + logements.size() + " résultat(s)");

        } catch (SQLException e) {
            System.err.println("Erreur sp_rechercher_logements: " + e.getMessage());
            e.printStackTrace();
        }
        return logements;
    }

    /**
     * Mapping spécifique pour sp_rechercher_logements
     * @param rs
     * @return
     * @throws SQLException
     */
    private Logement mapToLogementFromProcedure(ResultSet rs) throws SQLException {
        Logement logement = new Logement();

        logement.setId(rs.getLong("id"));
        logement.setHoteId(rs.getLong("hote_id"));
        logement.setTitre(rs.getString("titre"));
        logement.setDescription(rs.getString("description"));
        logement.setPrixNuit(rs.getBigDecimal("prix_nuit"));
        logement.setCapaciteMax(rs.getInt("capacite_max"));
        logement.setNbChambres(rs.getInt("nb_chambres"));
        logement.setNbLits(rs.getInt("nb_lits"));
        logement.setNbSallesBain(rs.getInt("nb_salles_bain"));

        // Statut
        String statut = rs.getString("statut");
        if (statut != null) {
            logement.setStatut(StatutLogement.valueOf(statut));
        }

        // Type logement
        try {
            String typeNom = rs.getString("type_logement");
            if (typeNom != null) {
                TypeLogement type = new TypeLogement();
                type.setId(rs.getLong("type_logement_id"));
                type.setLibelle(typeNom);
                logement.setTypeLogement(type);
            }
        } catch (SQLException ignored) {}

        // Hôte
        try {
            logement.setHotePrenom(rs.getString("hote_prenom"));
            logement.setHoteNom(rs.getString("hote_nom"));
        } catch (SQLException ignored) {}

        // Photo principale
        try {
            logement.setPhotoPrincipale(rs.getString("photo_principale"));
        } catch (SQLException ignored) {}

        // Note
        try {
            logement.setNoteMoyenne(rs.getBigDecimal("note_moyenne"));
        } catch (SQLException ignored) {}

        return logement;
    }

    // ==========================================
    // Afficher tout les logements (pas de procédure)
    // ==========================================
    public List<Logement> findAllLogements() {
        List<Logement> logements = new ArrayList<>();
        String sql = "SELECT * FROM v_logements";
        try(Connection conn = DataSourceProvider.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logements.add(mapToLogementFromProcedure(rs));
            }
        }catch(SQLException e){
            System.err.println("Erreur sp_findAllLogements: " + e.getMessage());
        };
        return logements;
    }

    // ==========================================
    // Afficher tout les logements disponibles
    // ==========================================
    public List<Logement> findAllDisponibles() throws SQLException {
        List<Logement> logements = new ArrayList<>();
        String sql = "SELECT * FROM v_logements " +
                "WHERE statut = 'DISPONIBLE'";

        try(Connection conn = DataSourceProvider.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Logement logement = mapResultSetToLogement(rs);
                logements.add(logement);
            }


        }catch(SQLException e){
            System.err.println("Erreur sp_findAllDispo: " + e.getMessage());
        }
        // Charger les photos pour chaque logement
        for (Logement logement : logements) {
            logement.setPhotos(findPhotosByLogementId(logement.getId()));
        }

        return logements;
    }


    /**
     * Afficher les logements par ville
     * @param ville
     * @return logements
     */
    public List<Logement> findByVille(String ville) {
        List<Logement> logements = new ArrayList<>();

        String sql = "SELECT * FROM v_logements WHERE ville LIKE ?";

        try(Connection conn = DataSourceProvider.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + ville + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Logement logement = mapResultSetToLogement(rs);
                    logement.setPhotos(findPhotosByLogementId(logement.getId()));
                    logements.add(logement);
                }
            }
        }catch(SQLException e){
            System.err.println("Erreur sp_findAllDispo: " + e.getMessage());
        };
        return logements;
    }



    /**
     * Trouve un logement par ID (utilise la vue v_logements)
     * @param id
     * @return logement
     */
    public Optional<Logement> findById(Long id) {
        String sql = "SELECT * FROM v_logements WHERE id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Logement logement = mapToLogement(rs);
                    // Charger les photos et équipements
                    logement.setPhotos(findPhotosByLogementId(id));
                    logement.setEquipements(findEquipementsByLogementId(id));
                    return Optional.of(logement);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }



    /**
     * Tous les logements d'un hôte
     * @param hoteId
     * @return Liste logements d'un hote
     */
    public List<Logement> findByHoteId(Long hoteId) {
        List<Logement> logements = new ArrayList<>();
        String sql = "SELECT * FROM v_logements WHERE hote_id = ? AND statut != 'ARCHIVE' ORDER BY date_creation DESC";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, hoteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logements.add(mapToLogement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByHoteId: " + e.getMessage());
            e.printStackTrace();
        }
        return logements;
    }


    /**
     * Logements d'un hôte avec filtres
     * @param hoteId
     * @param statut
     * @param typeId
     * @param search
     * @return
     */
    public List<Logement> findByHoteWithFilters(Long hoteId, String statut, String typeId, String search) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT * FROM v_logements WHERE hote_id = ?");

        List<Logement> logements = new ArrayList<>();

        List<Object> params = new ArrayList<>();
        params.add(hoteId);

        // Filtre par statut
        if (statut != null && !statut.trim().isEmpty() && !"TOUS".equals(statut)) {
            sql.append(" AND statut = ?");
            params.add(statut);
        } else {
            // Par défaut, exclure les archivés
            sql.append(" AND l.statut != 'ARCHIVE'");
        }

        // Filtre par type
        if (typeId != null && !typeId.trim().isEmpty()) {
            try{
            Long type = Long.parseLong(typeId);
            sql.append(" AND type_logement_id = ?");
            params.add(type);
            } catch (NumberFormatException ignored) {}
        }

        // Recherche textuelle
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (LOWER(titre) LIKE LOWER(?) OR LOWER(ville) LIKE LOWER(?))");
            String searchParam = "%" + search.trim() + "%";
            params.add(searchParam);
            params.add(searchParam);
        }

        sql.append(" ORDER BY date_creation DESC");

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Définir les paramètres
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Logement logement = mapResultSetToLogement(rs);
                    logement.setPhotos(findPhotosByLogementId(logement.getId()));
                    logements.add(logement);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByHoteWithFilters: " + e.getMessage());
            e.printStackTrace();
        }
        return logements;
    }

    // ==========================================
    // RECHERCHE PUBLIQUE AVANCÉE (sans procédure)
    // Pour les filtres avancés non supportés par sp_rechercher_logements
    // ==========================================


    /**
     * Recherche avancée avec tous les filtres
     * Utilise la vue v_logements
     * @param ville
     * @param dateDebut
     * @param dateFin
     * @param nbVoyageurs
     * @param prixMin
     * @param prixMax
     * @param typeIds
     * @param nbChambresMin
     * @param equipementIds
     * @param noteMin
     * @param tri
     * @param page
     * @param pageSize
     * @return Liste de logements filtré
     */
    public List<Logement> search(String ville, LocalDate dateDebut, LocalDate dateFin,
                                 Integer nbVoyageurs, BigDecimal prixMin, BigDecimal prixMax,
                                 List<Long> typeIds, Integer nbChambresMin, List<Long> equipementIds,
                                 BigDecimal noteMin, String tri, int page, int pageSize) {

        List<Logement> logements = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT v.* FROM v_logements v WHERE v.statut = 'DISPONIBLE'");

        List<Object> params = new ArrayList<>();

        // Filtre ville
        if (ville != null && !ville.isBlank()) {
            sql.append(" AND (LOWER(v.ville) LIKE LOWER(?) OR LOWER(v.region) LIKE LOWER(?))");
            params.add("%" + ville + "%");
            params.add("%" + ville + "%");
        }

        // Filtre capacité
        if (nbVoyageurs != null && nbVoyageurs > 0) {
            sql.append(" AND v.capacite_max >= ?");
            params.add(nbVoyageurs);
        }

        // Filtre prix
        if (prixMin != null) {
            sql.append(" AND v.prix_nuit >= ?");
            params.add(prixMin);
        }
        if (prixMax != null) {
            sql.append(" AND v.prix_nuit <= ?");
            params.add(prixMax);
        }

        // Filtre types
        if (typeIds != null && !typeIds.isEmpty()) {
            sql.append(" AND v.type_logement_id IN (");
            sql.append(String.join(",", Collections.nCopies(typeIds.size(), "?")));
            sql.append(")");
            params.addAll(typeIds);
        }

        // Filtre chambres
        if (nbChambresMin != null && nbChambresMin > 0) {
            sql.append(" AND v.nb_chambres >= ?");
            params.add(nbChambresMin);
        }

        // Filtre équipements
        if (equipementIds != null && !equipementIds.isEmpty()) {
            sql.append(" AND v.id IN (SELECT le.logement_id FROM logement_equipement le WHERE le.equipement_id IN (");
            sql.append(String.join(",", Collections.nCopies(equipementIds.size(), "?")));
            sql.append(") GROUP BY le.logement_id HAVING COUNT(DISTINCT le.equipement_id) = ?)");
            params.addAll(equipementIds);
            params.add(equipementIds.size());
        }

        // Filtre note minimum
        if (noteMin != null) {
            sql.append(" AND v.note_moyenne >= ?");
            params.add(noteMin);
        }

        // Filtre disponibilité (dates)
        if (dateDebut != null && dateFin != null) {
            sql.append(" AND v.id NOT IN (SELECT r.logement_id FROM reservation r WHERE r.statut IN ('CONFIRMEE', 'EN_COURS') AND r.date_debut < ? AND r.date_fin > ?)");
            params.add(dateFin);
            params.add(dateDebut);
        }

        // Tri
        sql.append(" ORDER BY ");
        switch (tri != null ? tri : "pertinence") {
            case "prix_asc" -> sql.append("v.prix_nuit ASC");
            case "prix_desc" -> sql.append("v.prix_nuit DESC");
            case "note" -> sql.append("v.note_moyenne DESC");
            case "recent" -> sql.append("v.date_creation DESC");
            default -> sql.append("v.date_creation DESC");
        }

        // Pagination
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logements.add(mapToLogement(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur search: " + e.getMessage());
            e.printStackTrace();
        }
        return logements;
    }


    /**
     * Rechercher des logements avec filtres
     * @param ville
     * @param typeId
     * @param prixMax
     * @param capaciteMin
     * @return Liste de logements
     * @throws SQLException
     */
    public List<Logement> searchLogements(String ville, String typeId,
                                          String prixMax, String capaciteMin)
            throws SQLException {

        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT l.*, a.*
            FROM logement l
            INNER JOIN adresse a ON l.adresse_id = a.id
            WHERE l.statut = 'DISPONIBLE'
        """);

        List<Object> params = new ArrayList<>();

        // Filtre par ville
        if (ville != null && !ville.trim().isEmpty()) {
            sql.append(" AND LOWER(a.ville) LIKE LOWER(?)");
            params.add("%" + ville.trim() + "%");
        }

        // Filtre par type
        if (typeId != null && !typeId.trim().isEmpty()) {
            try {
                Long type = Long.parseLong(typeId);
                sql.append(" AND l.type_logement_id = ?");
                params.add(type);
            } catch (NumberFormatException ignored) {}
        }

        // Filtre par prix maximum
        if (prixMax != null && !prixMax.trim().isEmpty()) {
            try {
                BigDecimal prix = new BigDecimal(prixMax);
                sql.append(" AND l.prix_nuit <= ?");
                params.add(prix);
            } catch (NumberFormatException ignored) {}
        }

        // Filtre par capacité minimale
        if (capaciteMin != null && !capaciteMin.trim().isEmpty()) {
            try {
                Integer capacite = Integer.parseInt(capaciteMin);
                sql.append(" AND l.capacite_max >= ?");
                params.add(capacite);
            } catch (NumberFormatException ignored) {}
        }

        sql.append(" ORDER BY l.date_creation DESC");

        List<Logement> logements = new ArrayList<>();

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Définir les paramètres
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Logement logement = mapResultSetToLogement(rs);
                    logement.setPhotos(findPhotosByLogementId(logement.getId()));
                    logements.add(logement);
                }
            }
        }
        return logements;
    }



    /**
     * Changer le statut d'un logement
     * @param logementId
     * @param statut
     * @return boolean
     */
    public boolean updateStatut(Long logementId, String statut) throws SQLException {
        String sql = "UPDATE logement SET statut = ?, date_modification = NOW() WHERE id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            ps.setLong(2, logementId);

            return ps.executeUpdate() > 0;
        }
    }


    // ==========================================
    // PHOTOS
    // ==========================================

    /**
     * Récupérer les photos d'un logement
     * @param logementId
     * @return Liste de photos pour le logement
     * @throws SQLException
     */
    public List<PhotoLogement> findPhotosByLogementId(Long logementId) throws SQLException {

        String sql = "SELECT * FROM photo_logement " +
                "WHERE logement_id = ? " +
                "ORDER BY est_principale DESC, ordre ASC";

        List<PhotoLogement> photos = new ArrayList<>();

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhotoLogement photo = new PhotoLogement();
                    photo.setId(rs.getLong("id"));
                    photo.setLogementId(rs.getLong("logement_id"));
                    photo.setUrl(rs.getString("url"));
                    photo.setDescription(rs.getString("description"));
                    photo.setEstPrincipale(rs.getBoolean("est_principale"));
                    photo.setOrdre(rs.getInt("ordre"));
                    photos.add(photo);
                }
            }
        }
        return photos;
    }

    // ==========================================
    // ÉQUIPEMENTS
    // ==========================================

    public List<Equipement> findEquipementsByLogementId(Long logementId) {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT e.* FROM equipement e JOIN logement_equipement le ON e.id = le.equipement_id WHERE le.logement_id = ? ORDER BY e.categorie, e.nom";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipement eq = new Equipement();
                    eq.setId(rs.getLong("id"));
                    eq.setNom(rs.getString("nom"));
                    eq.setIcone(rs.getString("icone"));
                    eq.setCategorie(rs.getString("categorie"));
                    equipements.add(eq);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipements;
    }

    public List<Equipement> findAllEquipements() {
        List<Equipement> equipements = new ArrayList<>();
        String sql = "SELECT * FROM equipement ORDER BY categorie, nom";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipement eq = new Equipement();
                eq.setId(rs.getLong("id"));
                eq.setNom(rs.getString("nom"));
                eq.setIcone(rs.getString("icone"));
                eq.setCategorie(rs.getString("categorie"));
                equipements.add(eq);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipements;
    }





    // ==========================================
    // TYPES DE LOGEMENT
    // ==========================================

    public List<TypeLogement> findAllTypes() {
        List<TypeLogement> types = new ArrayList<>();
        String sql = "SELECT * FROM type_logement ORDER BY ordre";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TypeLogement type = new TypeLogement();
                type.setId(rs.getLong("id"));
                type.setLibelle(rs.getString("libelle"));
                type.setIcone(rs.getString("icone"));
                type.setActif(rs.getBoolean("actif"));
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return types;
    }

    // ==========================================
    // MAPPING depuis la VUE v_logements
    // ==========================================

    private Logement mapToLogement(ResultSet rs) throws SQLException {
        Logement logement = new Logement();

        logement.setId(rs.getLong("id"));
        logement.setHoteId(rs.getLong("hote_id"));
        logement.setTypeLogementId(rs.getObject("type_logement_id", Long.class));
        logement.setTitre(rs.getString("titre"));
        logement.setDescription(rs.getString("description"));
        logement.setNbChambres(rs.getObject("nb_chambres", Integer.class));
        logement.setNbLits(rs.getObject("nb_lits", Integer.class));
        logement.setNbSallesBain(rs.getObject("nb_salles_bain", Integer.class));
        logement.setCapaciteMax(rs.getObject("capacite_max", Integer.class));
        logement.setSuperficie(rs.getBigDecimal("superficie"));
        logement.setPrixNuit(rs.getBigDecimal("prix_nuit"));
        logement.setFraisMenage(rs.getBigDecimal("frais_menage"));

        String statut = rs.getString("statut");
        if (statut != null) {
            logement.setStatut(StatutLogement.valueOf(statut));
        }

        // Règlement
        try {
            logement.setHeureArrivee(rs.getString("heure_arrivee"));
            logement.setHeureDepart(rs.getString("heure_depart"));
            logement.setReglementInterieur(rs.getString("reglement_interieur"));
            logement.setDelaiAnnulation(rs.getObject("delai_annulation", Integer.class));
        } catch (SQLException ignored) {}

        // Dates
        try {
            Timestamp ts = rs.getTimestamp("date_creation");
            if (ts != null) logement.setDateCreation(ts.toLocalDateTime());

            ts = rs.getTimestamp("date_modification");
            if (ts != null) logement.setDateModification(ts.toLocalDateTime());
        } catch (SQLException ignored) {}


        // ✅ MAPPER L'ADRESSE depuis la vue v_logements
        try {
            AdresseBien adresse = new AdresseBien();
            adresse.setId(rs.getObject("adresseId", Long.class));  // ou "adresse_id" selon ta vue
            adresse.setAdresse(rs.getString("adresse"));
            adresse.setCodePostal(rs.getString("code_postal"));
            adresse.setVille(rs.getString("ville"));
            adresse.setRegion(rs.getString("region"));
            adresse.setPays(rs.getString("pays"));
            adresse.setLatitude(rs.getBigDecimal("latitude"));
            adresse.setLongitude(rs.getBigDecimal("longitude"));
            logement.setAdresse(adresse);
        } catch (SQLException e) {
            System.err.println("Erreur mapping adresse: " + e.getMessage());
        }

        // Type de logement (depuis la vue)
        try {
            String typeNom = rs.getString("type_logement");
            if (typeNom != null) {
                TypeLogement type = new TypeLogement();
                type.setId(logement.getTypeLogementId());
                type.setLibelle(typeNom);
                try {
                    type.setIcone(rs.getString("type_icone"));
                } catch (SQLException ignored) {}
                logement.setTypeLogement(type);
            }
        } catch (SQLException ignored) {}

        // Photo principale (depuis la vue)
        try {
            logement.setPhotoPrincipale(rs.getString("photo_principale"));
        } catch (SQLException ignored) {}

        // Stats (depuis la vue)
        try {
            logement.setNoteMoyenne(rs.getBigDecimal("note_moyenne"));
            logement.setNbAvis(rs.getInt("nb_avis"));
        } catch (SQLException ignored) {}

        try {
            logement.setNbReservations(rs.getInt("nb_reservations"));
        } catch (SQLException ignored) {}

        // Infos hôte (depuis la vue)
        try {
            logement.setHoteNom(rs.getString("hote_nom"));
            logement.setHotePrenom(rs.getString("hote_prenom"));
            logement.setHotePhoto(rs.getString("hote_photo"));
            logement.setHoteVerifie(rs.getBoolean("hote_verifie"));
        } catch (SQLException ignored) {}

        return logement;
    }

    private AdresseBien mapToAdresseBien(ResultSet rs) throws SQLException {
        // Vérifier si l'adresse existe
        Long adresseId = rs.getObject("adresse_id", Long.class);
        if (adresseId == null) {
            return null;
        }

        AdresseBien a = new AdresseBien();
        a.setId(adresseId);
        a.setAdresse(rs.getString("adresse"));
        a.setCodePostal(rs.getString("code_postal"));
        a.setVille(rs.getString("ville"));
        a.setRegion(rs.getString("region"));
        a.setPays(rs.getString("pays"));
        a.setLatitude(rs.getBigDecimal("latitude"));
        a.setLongitude(rs.getBigDecimal("longitude"));

        // Champs spécifiques AdresseBien
        try {
            a.setInstructionsAcces(rs.getString("instructions_acces"));
            a.setCodePortail(rs.getString("code_portail"));
            a.setCodeImmeuble(rs.getString("code_immeuble"));
            a.setEtage(rs.getObject("etage", Integer.class));
            a.setNumeroAppartement(rs.getString("numero_appartement"));
        } catch (SQLException ignored) {}

        return a;
    }





    // ==========================================
    // MÉTHODES UTILITAIRES
    // ==========================================

    /**
     * Mapper un ResultSet vers un objet Logement
     * @param rs
     * @return logement
     * @throws SQLException
     */
    private Logement mapResultSetToLogement(ResultSet rs) throws SQLException {
        Logement logement = new Logement();

        // Données du logement
        logement.setId(rs.getLong("id"));
        logement.setHoteId(rs.getLong("hote_id"));
        logement.setTypeLogementId(rs.getLong("type_logement_id"));
        logement.setTitre(rs.getString("titre"));
        logement.setDescription(rs.getString("description"));
        logement.setNbChambres(rs.getObject("nb_chambres", Integer.class));
        logement.setNbLits(rs.getObject("nb_lits", Integer.class));
        logement.setNbSallesBain(rs.getObject("nb_salles_bain", Integer.class));
        logement.setCapaciteMax(rs.getObject("capacite_max", Integer.class));
        logement.setSuperficie(rs.getBigDecimal("superficie"));
        logement.setPrixNuit(rs.getBigDecimal("prix_nuit"));
        logement.setFraisMenage(rs.getBigDecimal("frais_menage"));
        logement.setHeureArrivee(rs.getString("heure_arrivee"));
        logement.setHeureDepart(rs.getString("heure_depart"));
        logement.setReglementInterieur(rs.getString("reglement_interieur"));
        logement.setDelaiAnnulation(rs.getObject("delai_annulation", Integer.class));

        // Statut
        String statutStr = rs.getString("statut");
        if (statutStr != null) {
            logement.setStatut(StatutLogement.valueOf(statutStr));
        }

        // Dates
        logement.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        if (rs.getTimestamp("date_modification") != null) {
            logement.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());
        }

        // Adresse
        AdresseBien adresse = new AdresseBien();
        adresse.setId(rs.getLong("adresse_id"));
        adresse.setAdresse(rs.getString("adresse"));
        adresse.setCodePostal(rs.getString("code_postal"));
        adresse.setVille(rs.getString("ville"));
        adresse.setRegion(rs.getString("region"));
        adresse.setPays(rs.getString("pays"));
        adresse.setLatitude(rs.getBigDecimal("latitude"));
        adresse.setLongitude(rs.getBigDecimal("longitude"));
        logement.setAdresse(adresse);

        return logement;
    }

    /**
     * Trouver des logements similaires (même ville, même type)
     */
    public List<Logement> findSimilaires(Long logementId, int limit) throws SQLException {
        String sql = """
            SELECT l2.*, a2.*
            FROM logement l1
            INNER JOIN adresse a1 ON l1.adresse_id = a1.id
            INNER JOIN logement l2 ON l1.type_logement_id = l2.type_logement_id
            INNER JOIN adresse a2 ON l2.adresse_id = a2.id
            WHERE l1.id = ?
            AND l2.id != ?
            AND l2.statut = 'DISPONIBLE'
            AND a1.ville = a2.ville
            ORDER BY ABS(l1.prix_nuit - l2.prix_nuit)
            LIMIT ?
        """;

        List<Logement> logements = new ArrayList<>();

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);
            ps.setLong(2, logementId);
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Logement logement = mapResultSetToLogement(rs);
                    logement.setPhotos(findPhotosByLogementId(logement.getId()));
                    logements.add(logement);
                }
            }
        }

        return logements;
    }
}