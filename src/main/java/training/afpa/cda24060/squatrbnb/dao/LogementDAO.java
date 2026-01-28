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

    // ==========================================
    // CRÉATION avec PROCÉDURE STOCKÉE
    // ==========================================

    /**
     * Créer un logement via sp_creer_logement
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

    // ==========================================
    // RECHERCHE avec PROCÉDURE STOCKÉE
    // ==========================================

    /**
     * Recherche via sp_rechercher_logements
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
     */
    private Logement mapToLogementFromProcedure(ResultSet rs) throws SQLException {
        Logement logement = new Logement();

        logement.setId(rs.getLong("id"));
        logement.setHoteId(rs.getLong("hote_id"));
        logement.setTitre(rs.getString("titre"));
        logement.setDescription(rs.getString("description"));
//        logement.setVille(rs.getString("ville"));
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
    public List<Logement> findAllDisponibles() {
        List<Logement> logements = new ArrayList<>();
        String sql = "SELECT * FROM v_logements WHERE statut = 'DISPONIBLE'";
        try(Connection conn = DataSourceProvider.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                logements.add(mapToLogement(rs));
            }
        }catch(SQLException e){
            System.err.println("Erreur sp_findAllDispo: " + e.getMessage());
        };
        return logements;
    }

    // ==========================================
    // Afficher tout les logements disponibles
    // ==========================================
    public List<Logement> findByVille(String ville) {
        List<Logement> logements = new ArrayList<>();
        String sql = "SELECT * FROM v_logements WHERE ville = ?";
        try(Connection conn = DataSourceProvider.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, ville);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logements.add(mapToLogement(rs));
                }
            }
        }catch(SQLException e){
            System.err.println("Erreur sp_findAllDispo: " + e.getMessage());
        };
        return logements;
    }

    // ==========================================
    // RECHERCHE PAR ID (pas de procédure)
    // ==========================================

    /**
     * Trouve un logement par ID (utilise la vue v_logements)
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

    // ==========================================
    // RECHERCHE PAR HÔTE (utilise la vue)
    // ==========================================

    /**
     * Tous les logements d'un hôte
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
     */
    public List<Logement> findByHoteWithFilters(Long hoteId, String statut, String typeId, String search) {
        List<Logement> logements = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM v_logements WHERE hote_id = ? AND statut != 'ARCHIVE'");

        List<Object> params = new ArrayList<>();
        params.add(hoteId);

        if (statut != null && !statut.isBlank()) {
            sql.append(" AND statut = ?");
            params.add(statut);
        }

        if (typeId != null && !typeId.isBlank()) {
            sql.append(" AND type_logement_id = ?");
            params.add(Long.parseLong(typeId));
        }

        if (search != null && !search.isBlank()) {
            sql.append(" AND (LOWER(titre) LIKE LOWER(?) OR LOWER(ville) LIKE LOWER(?))");
            params.add("%" + search + "%");
            params.add("%" + search + "%");
        }

        sql.append(" ORDER BY date_creation DESC");

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

    // ==========================================
    // MISE À JOUR (pas de procédure existante)
    // ==========================================

    /**
     * Mettre à jour un logement
     */
    public boolean update(Logement logement) {
        String sql = """
            UPDATE logement SET
                type_logement_id = ?, titre = ?, description = ?,
                nb_chambres = ?, nb_lits = ?, nb_salles_bain = ?, capacite_max = ?, superficie = ?,
                prix_nuit = ?, frais_menage = ?,
                heure_arrivee = ?, heure_depart = ?, reglement_interieur = ?, delai_annulation = ?,
                date_modification = NOW()
            WHERE id = ? AND hote_id = ?
            """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
            ps.setLong(i++, logement.getHoteId());

            int rows = ps.executeUpdate();

            if (rows > 0 && logement.getEquipementIds() != null) {
                deleteEquipements(logement.getId());
                if (!logement.getEquipementIds().isEmpty()) {
                    saveEquipements(logement.getId(), logement.getEquipementIds());
                }
            }

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur update: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Changer le statut d'un logement
     */
    public boolean updateStatut(Long logementId, String statut) {
        String sql = "UPDATE logement SET statut = ?, date_modification = NOW() WHERE id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            ps.setLong(2, logementId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur updateStatut: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Archiver un logement (soft delete)
     */
    public boolean archiver(Long logementId) {
        return updateStatut(logementId, "ARCHIVE");
    }


    /**
     * Supprimer definitivement un logement
     */
    public boolean delete(Long logementId) {
        String sql = "DELETE FROM logement WHERE id = ? AND statut = ARCHIVE";
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, logementId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    // ==========================================
    // PHOTOS
    // ==========================================

    public List<PhotoLogement> findPhotosByLogementId(Long logementId) {
        List<PhotoLogement> photos = new ArrayList<>();
        String sql = "SELECT * FROM photo_logement WHERE logement_id = ? ORDER BY est_principale DESC, ordre ASC";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, logementId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhotoLogement photo = new PhotoLogement();
                    photo.setId(rs.getLong("id"));
                    photo.setLogementId(rs.getLong("logement_id"));
                    photo.setUrl(rs.getString("url"));
                    photo.setLegende(rs.getString("legende"));
                    photo.setEstPrincipale(rs.getBoolean("est_principale"));
                    photo.setOrdre(rs.getInt("ordre"));
                    photos.add(photo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photos;
    }

    public Long savePhoto(PhotoLogement photo) {
        String sql = "INSERT INTO photo_logement (logement_id, url, legende, est_principale, ordre) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, photo.getLogementId());
            ps.setString(2, photo.getUrl());
            ps.setString(3, photo.getLegende());
            ps.setBoolean(4, photo.isEstPrincipale());
            ps.setInt(5, photo.getOrdre());

            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deletePhoto(Long photoId, Long hoteId) {
        String sql = "DELETE FROM photo_logement WHERE id = ? AND logement_id IN (SELECT id FROM logement WHERE hote_id = ?)";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, photoId);
            ps.setLong(2, hoteId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

}