package training.afpa.cda24060.squatrbnb.dao;

import training.afpa.cda24060.squatrbnb.model.*;
import training.afpa.cda24060.squatrbnb.model.enums.Role;
import training.afpa.cda24060.squatrbnb.model.profil.AdminProfil;
import training.afpa.cda24060.squatrbnb.model.profil.HoteProfil;
import training.afpa.cda24060.squatrbnb.model.profil.LocataireProfil;
import training.afpa.cda24060.squatrbnb.utilitaires.DataSourceProvider;
import training.afpa.cda24060.squatrbnb.utilitaires.PasswordUtil;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * DAO pour la gestion des utilisateurs
 * Utilise les procédures stockées et les vues
 * Retourne le bon type d'utilisateur via UtilisateurFactory
 */
public class UtilisateurDAO {
    
    // ==================== AUTHENTIFICATION ====================
    
    /**
     * Résultat d'authentification
     */
    public record AuthResult(boolean success, int utilisateurId, String message, Utilisateur utilisateur) {
        public static AuthResult success(int id, Utilisateur user) {
            return new AuthResult(true, id, "SUCCES", user);
        }
        public static AuthResult failure(String message) {
            return new AuthResult(false, 0, message, null);
        }
    }
    
    /**
     * Authentifie un utilisateur via sp_authentifier
     */
    public AuthResult authentifier(String email, String motDePasseClair) {
        // 5 paramètres : 1 IN + 4 OUT
        String sql = "{CALL sp_authentifier(?, ?, ?, ?, ?)}";

        try (Connection conn = DataSourceProvider.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            // Paramètre 1 : IN
            cs.setString(1, email);

            // Paramètres 2-5 : OUT
            cs.registerOutParameter(2, Types.BIGINT);   // p_utilisateur_id
            cs.registerOutParameter(3, Types.VARCHAR);  // p_mot_de_passe_hash
            cs.registerOutParameter(4, Types.BOOLEAN);  // p_actif
            cs.registerOutParameter(5, Types.VARCHAR);  // p_message

            cs.execute();

            Integer userId = cs.getInt(2);
            if (cs.wasNull()) userId = null;
            String hashStocke = cs.getString(3);
            String message = cs.getString(5);

            // Vérifier le mot de passe avec BCrypt
            if ("VERIFIER_MDP".equals(message) && hashStocke != null) {
                if (PasswordUtil.checkpw(motDePasseClair, hashStocke)) {
                    //updateDerniereConnexion(userId);
                    Optional<Utilisateur> user = findById(userId);
                    if (user.isPresent()) {
                        return AuthResult.success(userId, user.get());
                    }
                    return AuthResult.failure("ERREUR_CHARGEMENT");
                } else {
                    return AuthResult.failure("MOT_DE_PASSE_INCORRECT");
                }
            }

            return AuthResult.failure(message);

        } catch (SQLException e) {
            e.printStackTrace();
            return AuthResult.failure("ERREUR_CHARGEMENT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // ==================== INSCRIPTION ====================
    
    /**
     * Résultat d'inscription
     */
    public record InscriptionResult(boolean success, int utilisateurId, String message) {}
    
    /**
     * Inscrit un nouvel utilisateur via sp_inscrire_utilisateur
     */
    public InscriptionResult inscrire(String email, String motDePasseHash, String nom, String prenom,
                                      String telephone, Role role) {
        String sql = "{CALL sp_inscrire_utilisateur(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DataSourceProvider.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setString(1, email);
            cs.setString(2, motDePasseHash);
            cs.setString(3, nom);
            cs.setString(4, prenom);
            cs.setString(5, telephone);
            cs.setString(6, role.getCode());
            
//            if (adresse != null && adresse.isComplete()) {
//                cs.setString(7, adresse.getAdresseLigne1());
//                cs.setString(8, adresse.getCodePostal());
//                cs.setString(9, adresse.getVille());
//                cs.setString(10, adresse.getPays());
//            } else {
//                cs.setNull(7, Types.VARCHAR);
//                cs.setNull(8, Types.VARCHAR);
//                cs.setNull(9, Types.VARCHAR);
//                cs.setNull(10, Types.VARCHAR);
//            }
//
            cs.registerOutParameter(7, Types.BIGINT);
            cs.registerOutParameter(8, Types.VARCHAR);
            
            cs.execute();
            
            int userId = cs.getInt(7);
            String message = cs.getString(8);
            
            boolean success = message.contains("réussie");
            return new InscriptionResult(success, userId, message);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return new InscriptionResult(false, 0, "Erreur: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // ==================== RECHERCHE ====================
    
    /**
     * Trouve un utilisateur par ID (retourne le bon type)
     */
    public Optional<Utilisateur> findById(int id) {
        String sql = "SELECT * FROM v_utilisateurs_roles WHERE id = ?";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUtilisateur(rs, conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    
    /**
     * Trouve un utilisateur par email
     */
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT * FROM v_utilisateurs_roles WHERE email = ?";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email.toLowerCase().trim());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToUtilisateur(rs, conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    
    /**
     * Vérifie si un email existe
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email.toLowerCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Liste tous les utilisateurs avec pagination
     */
    public List<Utilisateur> findAll(int page, int pageSize) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM v_utilisateurs_roles ORDER BY date_inscription DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(mapToUtilisateur(rs, conn));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return utilisateurs;
    }
    
    // ==================== GESTION DES RÔLES ====================
    
    /**
     * Ajoute un rôle à un utilisateur via sp_ajouter_role
     */
    public boolean ajouterRole(int utilisateurId, Role role, int attribuePar) {
        String sql = "{CALL sp_ajouter_role(?, ?, ?, ?)}";
        
        try (Connection conn = DataSourceProvider.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            
            cs.setInt(1, utilisateurId);
            cs.setString(2, role.getCode());
            if (attribuePar != 0) {
                cs.setInt(3, attribuePar);
            } else {
                cs.setNull(3, Types.BIGINT);
            }
            cs.registerOutParameter(4, Types.VARCHAR);
            
            cs.execute();
            
            String message = cs.getString(4);
            return "Rôle attribué".equals(message);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Vérifie si un utilisateur a un rôle via fn_a_role
     */
    public boolean hasRole(int utilisateurId, Role role) {
        String sql = "SELECT fn_a_role(?, ?)";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, utilisateurId);
            ps.setString(2, role.getCode());
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    
    // ==================== MISE À JOUR ====================
    
    /**
     * Met à jour les infos de base
     */
    public boolean update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, telephone = ?, photo_url = ? WHERE id = ?";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getTelephone());
            ps.setString(4, utilisateur.getPhotoUrl());
            ps.setLong(5, utilisateur.getId());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Met à jour le mot de passe
     */
    public boolean updateMotDePasse(int utilisateurId, String nouveauHash) {
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nouveauHash);
            ps.setInt(2, utilisateurId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Active ou désactive un utilisateur
     */
    public boolean setActif(int utilisateurId, boolean actif) {
        String sql = "UPDATE utilisateur SET actif = ? WHERE id = ?";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, actif);
            ps.setInt(2, utilisateurId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Compte le nombre total d'utilisateurs
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM utilisateur";
        
        try (Connection conn = DataSourceProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            return rs.next() ? rs.getInt(1) : 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // ==================== MAPPING ====================
    
    /**
     * Mappe un ResultSet vers le bon type d'Utilisateur via Factory
     */
    private Utilisateur mapToUtilisateur(ResultSet rs, Connection conn) throws SQLException {
        Long id = rs.getLong("id");
        String rolesStr = rs.getString("roles");
        
        // Parser les rôles
        Set<Role> roles = EnumSet.noneOf(Role.class);
        if (rolesStr != null && !rolesStr.isBlank()) {
            for (String code : rolesStr.split(",")) {
                if (Role.isValid(code.trim())) {
                    roles.add(Role.fromCode(code.trim()));
                }
            }
        }
        
        // Charger les profils selon les rôles
        HoteProfil profilHote = null;
        LocataireProfil profilLocataire = null;
        AdminProfil profilAdmin = null;
        
        if (roles.contains(Role.HOTE)) {
            profilHote = loadHoteProfil(conn, id);
        }
        if (roles.contains(Role.LOCATAIRE)) {
            profilLocataire = loadLocataireProfil(conn, id);
        }
        if (roles.contains(Role.ADMIN)) {
            profilAdmin = loadAdminProfil(conn, id);
        }
        
        // Créer l'utilisateur du bon type
        Utilisateur user = UtilisateurFactory.create(id, roles, profilHote, profilLocataire, profilAdmin);
        
        // Remplir les données de base
        user.setEmail(rs.getString("email"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setTelephone(rs.getString("telephone"));
        user.setPhotoUrl(rs.getString("photo_url"));
        user.setActif(rs.getBoolean("actif"));
        
        Timestamp ts = rs.getTimestamp("date_inscription");
        if (ts != null) user.setDateInscription(ts.toLocalDateTime());
        
        ts = rs.getTimestamp("date_derniere_connexion");
        if (ts != null) user.setDateDerniereConnexion(ts.toLocalDateTime());
        
        return user;
    }
    
    /**
     * Charge le profil hôte
     */
    private HoteProfil loadHoteProfil(Connection conn, Long utilisateurId) throws SQLException {
        String sql = "SELECT * FROM hote_profil WHERE utilisateur_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoteProfil p = new HoteProfil(utilisateurId);
                    p.setDescription(rs.getString("description"));
                    p.setSiret(rs.getString("siret"));
                    p.setRaisonSociale(rs.getString("raison_sociale"));
                    p.setVerifie(rs.getBoolean("verifie"));
                    p.setNoteMoyenne(rs.getBigDecimal("note_moyenne"));
                    p.setNbAvis(rs.getInt("nb_avis"));
                    p.setNbBiens(rs.getInt("nb_logements"));
                    p.setRevenusTotaux(rs.getBigDecimal("revenus_totaux"));
                    p.setIban(rs.getString("iban"));
                    p.setBic(rs.getString("bic"));
                    
                    Timestamp ts = rs.getTimestamp("date_verification");
                    if (ts != null) p.setDateVerification(ts.toLocalDateTime());
                    
                    return p;
                }
            }
        }
        return new HoteProfil(utilisateurId);
    }
    
    /**
     * Charge le profil locataire
     */
    private LocataireProfil loadLocataireProfil(Connection conn, Long utilisateurId) throws SQLException {
        String sql = "SELECT * FROM locataire_profil WHERE utilisateur_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocataireProfil p = new LocataireProfil(utilisateurId);
                    
                    Date dateNaissance = rs.getDate("date_naissance");
                    if (dateNaissance != null) p.setDateNaissance(dateNaissance.toLocalDate());

                    p.setPieceIdentiteNumero(rs.getString("piece_identite_numero"));
                    p.setPieceIdentiteUrl(rs.getString("piece_identite_url"));
                    
                    Date expiration = rs.getDate("piece_identite_expiration");
                    if (expiration != null) p.setPieceIdentiteExpiration(expiration.toLocalDate());
                    
                    p.setVerifie(rs.getBoolean("verifie"));
                    p.setNbReservations(rs.getInt("nb_reservations"));
                    p.setNbAvisDonnes(rs.getInt("nb_avis_donnes"));
                    
                    return p;
                }
            }
        }
        return new LocataireProfil(utilisateurId);
    }
    
    /**
     * Charge le profil admin
     */
    private AdminProfil loadAdminProfil(Connection conn, Long utilisateurId) throws SQLException {
        String sql = "SELECT * FROM admin_profil WHERE utilisateur_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AdminProfil p = new AdminProfil(utilisateurId);
                    p.setNiveauFromString(rs.getString("niveau"));
                    p.setDepartement(rs.getString("departement"));
                    p.setCreePar(rs.getInt("cree_par"));
                    p.setPermissions(rs.getString("permissions"));
                    
                    Timestamp ts = rs.getTimestamp("date_nomination");
                    if (ts != null) p.setDateNomination(ts.toLocalDateTime());
                    
                    return p;
                }
            }
        }
        return new AdminProfil(utilisateurId);
    }
}
