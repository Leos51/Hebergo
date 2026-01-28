package training.afpa.cda24060.squatrbnb.dao;

import training.afpa.cda24060.squatrbnb.model.AdresseUtilisateur;
import training.afpa.cda24060.squatrbnb.model.Hote;
import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.enums.TypeAdresse;
import training.afpa.cda24060.squatrbnb.model.profil.HoteProfil;
import training.afpa.cda24060.squatrbnb.utilitaires.DataSourceProvider;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * DAO pour la gestion des hôtes
 * Utilise la vue v_hotes
 */
public class HoteDAO {

    // ==========================================
    // MÉTHODES DE RECHERCHE
    // ==========================================

    /**
     * Trouve un hôte par ID
     */
    public Optional<Hote> findById(Long id) {
        String sql = "SELECT * FROM v_hotes WHERE id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToHote(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findById: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }



    /**
     * Trouve un hôte par email
     */
    public Optional<Hote> findByEmail(String email) {
        String sql = "SELECT * FROM v_hotes WHERE email = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.toLowerCase().trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToHote(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur findByEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Liste tous les hôtes actifs avec pagination
     */
    public List<Hote> findAll(int page, int pageSize) {
        List<Hote> hotes = new ArrayList<>();
        String sql = "SELECT * FROM v_hotes WHERE actif = TRUE ORDER BY note_moyenne DESC, nb_avis DESC LIMIT ? OFFSET ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotes.add(mapToHote(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur findAll: " + e.getMessage());
            e.printStackTrace();
        }
        return hotes;
    }

    /**
     * Liste tous les hôtes (sans pagination)
     */
    public List<Hote> findAll() {
        return findAll(1, 1000);
    }

    /**
     * Liste les hôtes vérifiés
     */
    public List<Hote> findVerifies() {
        List<Hote> hotes = new ArrayList<>();
        String sql = "SELECT * FROM v_hotes WHERE actif = TRUE AND verifie = TRUE ORDER BY note_moyenne DESC";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                hotes.add(mapToHote(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur findVerifies: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return hotes;
    }

    /**
     * Liste les hôtes par ville
     */
    public List<Hote> findByVille(String ville) {
        List<Hote> hotes = new ArrayList<>();
        String sql = "SELECT * FROM v_hotes WHERE actif = TRUE AND ville LIKE ? ORDER BY note_moyenne DESC";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + ville + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotes.add(mapToHote(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur findByVille: " + e.getMessage());
            e.printStackTrace();
        }
        return hotes;
    }

    /**
     * Recherche avec filtres
     */
    public List<Hote> search(String terme, Boolean verifie, String ville, int page, int pageSize) {
        List<Hote> hotes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM v_hotes WHERE actif = TRUE");
        List<Object> params = new ArrayList<>();

        if (terme != null && !terme.isBlank()) {
            sql.append(" AND (nom_complet LIKE ? OR email LIKE ? OR description LIKE ?)");
            String pattern = "%" + terme + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (verifie != null) {
            sql.append(" AND verifie = ?");
            params.add(verifie);
        }

        if (ville != null && !ville.isBlank()) {
            sql.append(" AND ville LIKE ?");
            params.add("%" + ville + "%");
        }

        sql.append(" ORDER BY note_moyenne DESC, nb_avis DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hotes.add(mapToHote(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur search: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return hotes;
    }

    // ==========================================
    // STATISTIQUES
    // ==========================================

    /**
     * Statistiques d'un hôte via procédure stockée
     * @param hoteId ID de l'hôte
     * @param periode "semaine", "mois", "trimestre", "annee"
     * @return Map avec les statistiques
     */
    public static Map<String, Object> getStats(Long hoteId, String periode) {
        Map<String, Object> stats = new HashMap<>();

        // Valeurs par défaut
        stats.put("nbLogements", 0);
        stats.put("noteMoyenne", BigDecimal.ZERO);
        stats.put("nbAvis", 0);
        stats.put("revenusTotaux", BigDecimal.ZERO);
        stats.put("reservationsEnAttente", 0);
        stats.put("reservationsAVenir", 0);
        stats.put("nouvellesReservationsPeriode", 0);
        stats.put("revenusPeriode", BigDecimal.ZERO);

        if (hoteId == null) {
            return stats;
        }

        // Période par défaut
        if (periode == null || periode.isBlank()) {
            periode = "mois";
        }

        String sql = "{CALL sp_stats_hote(?, ?)}";

        try (Connection conn = DataSourceProvider.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setLong(1, hoteId);
            cs.setString(2, periode);

            System.out.println("=== DEBUG sp_stats_hote ===");
            System.out.println("hoteId: " + hoteId + ", periode: " + periode);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    stats.put("nbLogements", rs.getInt("nb_logements"));
                    stats.put("noteMoyenne", rs.getBigDecimal("note_moyenne") != null
                            ? rs.getBigDecimal("note_moyenne") : BigDecimal.ZERO);
                    stats.put("nbAvis", rs.getInt("nb_avis"));
                    stats.put("revenusTotaux", rs.getBigDecimal("revenus_totaux") != null
                            ? rs.getBigDecimal("revenus_totaux") : BigDecimal.ZERO);
                    stats.put("reservationsEnAttente", rs.getInt("reservations_en_attente"));
                    stats.put("reservationsAVenir", rs.getInt("reservations_a_venir"));
                    stats.put("nouvellesReservationsPeriode", rs.getInt("nouvelles_reservations_periode"));
                    stats.put("revenusPeriode", rs.getBigDecimal("revenus_periode") != null
                            ? rs.getBigDecimal("revenus_periode") : BigDecimal.ZERO);

                    System.out.println("Stats chargées: " + stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL getStats: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur getStats: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Surcharge avec période par défaut (mois)
     */
    public static Map<String, Object> getStats(Long hoteId) {
        return getStats(hoteId, "mois");
    }

    /**
     * Alternative : Stats via requête directe (si procédure stockée n'existe pas)
     */
    public static Map<String, Object> getStatsDirectQuery(Long hoteId) {
        Map<String, Object> stats = new HashMap<>();

        // Valeurs par défaut
        stats.put("nbLogements", 0);
        stats.put("noteMoyenne", BigDecimal.ZERO);
        stats.put("nbAvis", 0);
        stats.put("revenusTotaux", BigDecimal.ZERO);
        stats.put("reservationsEnAttente", 0);
        stats.put("reservationsAVenir", 0);
        stats.put("revenusPeriode", BigDecimal.ZERO);

        if (hoteId == null) {
            return stats;
        }

        String sql = """
            SELECT 
                (SELECT COUNT(*) FROM bien WHERE hote_id = ? AND statut = 'DISPONIBLE') AS nb_logements,
                (SELECT AVG(a.note_globale) FROM avis a JOIN bien b ON a.bien_id = b.id WHERE b.hote_id = ?) AS note_moyenne,
                (SELECT COUNT(*) FROM avis a JOIN bien b ON a.bien_id = b.id WHERE b.hote_id = ?) AS nb_avis,
                (SELECT COALESCE(SUM(p.montant_hote), 0) FROM paiement p JOIN reservation r ON p.reservation_id = r.id JOIN bien b ON r.bien_id = b.id WHERE b.hote_id = ?) AS revenus_totaux,
                (SELECT COUNT(*) FROM reservation r JOIN bien b ON r.bien_id = b.id WHERE b.hote_id = ? AND r.statut = 'EN_ATTENTE') AS reservations_en_attente,
                (SELECT COUNT(*) FROM reservation r JOIN bien b ON r.bien_id = b.id WHERE b.hote_id = ? AND r.statut = 'CONFIRMEE' AND r.date_debut > NOW()) AS reservations_a_venir,
                (SELECT COALESCE(SUM(p.montant_hote), 0) FROM paiement p JOIN reservation r ON p.reservation_id = r.id JOIN bien b ON r.bien_id = b.id WHERE b.hote_id = ? AND MONTH(p.date_paiement) = MONTH(NOW()) AND YEAR(p.date_paiement) = YEAR(NOW())) AS revenus_periode
            """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 7 paramètres
            for (int i = 1; i <= 7; i++) {
                ps.setLong(i, hoteId);
            }

            System.out.println("=== DEBUG requête directe stats ===");
            System.out.println("Exécution pour hoteId: " + hoteId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("nbLogements", rs.getInt("nb_logements"));
                    stats.put("noteMoyenne", rs.getBigDecimal("note_moyenne") != null ? rs.getBigDecimal("note_moyenne") : BigDecimal.ZERO);
                    stats.put("nbAvis", rs.getInt("nb_avis"));
                    stats.put("revenusTotaux", rs.getBigDecimal("revenus_totaux") != null ? rs.getBigDecimal("revenus_totaux") : BigDecimal.ZERO);
                    stats.put("reservationsEnAttente", rs.getInt("reservations_en_attente"));
                    stats.put("reservationsAVenir", rs.getInt("reservations_a_venir"));
                    stats.put("revenusPeriode", rs.getBigDecimal("revenus_periode") != null ? rs.getBigDecimal("revenus_periode") : BigDecimal.ZERO);

                    System.out.println("Stats directes chargées: " + stats);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL getStatsDirectQuery: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur getStatsDirectQuery: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Stats simplifiées (version minimale si les tables n'existent pas encore)
     */
    public static Map<String, Object> getStatsSimple(Long hoteId) {
        Map<String, Object> stats = new HashMap<>();

        // Valeurs par défaut
        stats.put("nbLogements", 0);
        stats.put("noteMoyenne", BigDecimal.ZERO);
        stats.put("nbAvis", 0);
        stats.put("revenusTotaux", BigDecimal.ZERO);
        stats.put("reservationsEnAttente", 0);
        stats.put("reservationsAVenir", 0);
        stats.put("revenusPeriode", BigDecimal.ZERO);

        if (hoteId == null) {
            return stats;
        }

        // Compter les biens
        String sqlBiens = "SELECT COUNT(*) AS nb FROM bien WHERE hote_id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBiens)) {

            ps.setLong(1, hoteId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("nbLogements", rs.getInt("nb"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Table 'bien' non trouvée ou erreur: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Compter les réservations
        String sqlReservations = """
            SELECT 
                COUNT(CASE WHEN r.statut = 'EN_ATTENTE' THEN 1 END) AS en_attente,
                COUNT(CASE WHEN r.statut = 'CONFIRMEE' AND r.date_debut > NOW() THEN 1 END) AS a_venir
            FROM reservation r 
            JOIN bien b ON r.bien_id = b.id 
            WHERE b.hote_id = ?
            """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlReservations)) {

            ps.setLong(1, hoteId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("reservationsEnAttente", rs.getInt("en_attente"));
                    stats.put("reservationsAVenir", rs.getInt("a_venir"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Table 'reservation' non trouvée ou erreur: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Stats simples: " + stats);
        return stats;
    }

    // ==========================================
    // MISE À JOUR
    // ==========================================

    /**
     * Met à jour le profil hôte
     */
    public boolean updateProfil(HoteProfil profil) {
        String sql = """
            UPDATE hote_profil SET 
                description = ?, siret = ?, raison_sociale = ?, iban = ?, bic = ?
            WHERE utilisateur_id = ?
            """;

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, profil.getDescription());
            ps.setString(2, profil.getSiret());
            ps.setString(3, profil.getRaisonSociale());
            ps.setString(4, profil.getIban());
            ps.setString(5, profil.getBic());
            ps.setLong(6, profil.getUtilisateurId());

            int rows = ps.executeUpdate();
            System.out.println("updateProfil: " + rows + " ligne(s) mise(s) à jour");
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur updateProfil: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Vérifie un hôte (action admin)
     */
    public boolean verifier(Long utilisateurId, Long adminId) {
        String sql = "UPDATE hote_profil SET verifie = TRUE, date_verification = NOW(), verifie_par = ? WHERE utilisateur_id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, adminId);
            ps.setLong(2, utilisateurId);

            int rows = ps.executeUpdate();
            System.out.println("verifier: " + rows + " ligne(s) mise(s) à jour");
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur verifier: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Désactive un hôte
     */
    public boolean desactiver(Long utilisateurId) {
        String sql = "UPDATE utilisateur SET actif = FALSE WHERE id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur desactiver: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Réactive un hôte
     */
    public boolean reactiver(Long utilisateurId) {
        String sql = "UPDATE utilisateur SET actif = TRUE WHERE id = ?";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur reactiver: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==========================================
    // COMPTAGE
    // ==========================================

    /**
     * Compte le nombre total d'hôtes actifs
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM v_hotes WHERE actif = TRUE";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.err.println("Erreur count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compte les hôtes avec filtres
     */
    public int count(String terme, Boolean verifie, String ville) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM v_hotes WHERE actif = TRUE");
        List<Object> params = new ArrayList<>();

        if (terme != null && !terme.isBlank()) {
            sql.append(" AND (nom_complet LIKE ? OR email LIKE ? OR description LIKE ?)");
            String pattern = "%" + terme + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (verifie != null) {
            sql.append(" AND verifie = ?");
            params.add(verifie);
        }

        if (ville != null && !ville.isBlank()) {
            sql.append(" AND ville LIKE ?");
            params.add("%" + ville + "%");
        }

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur count avec filtres: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compte les hôtes vérifiés
     */
    public int countVerifies() {
        String sql = "SELECT COUNT(*) FROM v_hotes WHERE actif = TRUE AND verifie = TRUE";

        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            System.err.println("Erreur countVerifies: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ==========================================
    // MAPPING
    // ==========================================

    /**
     * Mappe un ResultSet vers un objet Hote
     */
    private Hote mapToHote(ResultSet rs) throws SQLException {
        Hote hote = new Hote();

        // Données utilisateur de base
        hote.setId(rs.getLong("id"));
        hote.setEmail(rs.getString("email"));
        hote.setNom(rs.getString("nom"));
        hote.setPrenom(rs.getString("prenom"));
        hote.setTelephone(rs.getString("telephone"));
        hote.setPhotoUrl(rs.getString("photo_url"));
        hote.setActif(rs.getBoolean("actif"));
        hote.addRole(Role.HOTE);

        // Dates
        Timestamp ts = rs.getTimestamp("date_inscription");
        if (ts != null) hote.setDateInscription(ts.toLocalDateTime());

        ts = rs.getTimestamp("date_derniere_connexion");
        if (ts != null) hote.setDateDerniereConnexion(ts.toLocalDateTime());

        // Profil hôte
        HoteProfil profil = new HoteProfil(hote.getId());
        profil.setDescription(rs.getString("description"));
        profil.setSiret(rs.getString("siret"));
        profil.setRaisonSociale(rs.getString("raison_sociale"));
        profil.setVerifie(rs.getBoolean("verifie"));

        // Stats du profil (peuvent être null)
        BigDecimal noteMoyenne = rs.getBigDecimal("note_moyenne");
        profil.setNoteMoyenne(noteMoyenne != null ? noteMoyenne : BigDecimal.ZERO);

        profil.setNbAvis(rs.getInt("nb_avis"));
        profil.setNbBiens(rs.getInt("nb_biens"));

        BigDecimal revenus = rs.getBigDecimal("revenus_totaux");
        profil.setRevenusTotaux(revenus != null ? revenus : BigDecimal.ZERO);

        ts = rs.getTimestamp("date_verification");
        if (ts != null) profil.setDateVerification(ts.toLocalDateTime());

        hote.setProfil(profil);

        // Adresse principale (si présente dans la vue)
        try {
            String adresseLigne1 = rs.getString("adresse");
            if (adresseLigne1 != null) {
                AdresseUtilisateur adresseUtilisateur = new AdresseUtilisateur();
                adresseUtilisateur.setId(rs.getLong("adresse_id"));
                adresseUtilisateur.setUtilisateurId(hote.getId());
                adresseUtilisateur.setAdresse(rs.getString("adresse"));
//                adresseUtilisateur.setAdresseLigne2(rs.getString("adresse_ligne2"));
                adresseUtilisateur.setCodePostal(rs.getString("code_postal"));
                adresseUtilisateur.setVille(rs.getString("ville"));
                adresseUtilisateur.setRegion(rs.getString("region"));
                adresseUtilisateur.setPays(rs.getString("pays"));
                adresseUtilisateur.setType(TypeAdresse.DOMICILE);
                adresseUtilisateur.setPrincipale(true);
                hote.setAdressePrincipale(adresseUtilisateur);
            }
        } catch (SQLException e) {
            // Colonnes d'adresse non présentes, on ignore
        }

        return hote;
    }
}