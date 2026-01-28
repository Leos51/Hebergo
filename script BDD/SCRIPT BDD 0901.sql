-- ============================================================================
-- HÉBERGO - Script de création de base de données
-- Version: 2.0 - Approche hybride (utilisateur unique + profils par rôle)
-- Description: Base de données pour application de location de biens
-- ============================================================================

-- Suppression de la base si elle existe
DROP DATABASE IF EXISTS squatrbnb;

-- Création de la base de données
CREATE DATABASE squatrbnb 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE squatrbnb;

-- ============================================================================
-- SECTION 1: TABLES DE BASE - UTILISATEURS ET RÔLES
-- ============================================================================

-- Table principale des utilisateurs (commune à tous les rôles)
CREATE TABLE utilisateur (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20),
    actif BOOLEAN DEFAULT TRUE,
    date_inscription DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_derniere_connexion DATETIME,
    date_modification DATETIME,
    
    INDEX idx_utilisateur_email (email),
    INDEX idx_utilisateur_actif (actif)
) ENGINE=InnoDB;

-- Table des rôles disponibles
CREATE TABLE role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    libelle VARCHAR(50) NOT NULL,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Table d'association utilisateur-rôles (un utilisateur peut avoir plusieurs rôles)
CREATE TABLE utilisateur_role (
    utilisateur_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    date_attribution DATETIME DEFAULT CURRENT_TIMESTAMP,
    attribue_par BIGINT,
    PRIMARY KEY (utilisateur_id, role_id),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (attribue_par) REFERENCES utilisateur(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================================
-- SECTION 2: TABLES DE PROFILS SPÉCIFIQUES PAR RÔLE
-- ============================================================================

-- Profil spécifique pour les hôtes
CREATE TABLE hote_profil (
    utilisateur_id BIGINT PRIMARY KEY,
    description TEXT,
    adresse VARCHAR(255),
    ville VARCHAR(100),
    code_postal VARCHAR(10),
    pays VARCHAR(100) DEFAULT 'France',
    siret VARCHAR(14),
    photo_url VARCHAR(500),
    verifie BOOLEAN DEFAULT FALSE,
    date_verification DATETIME,
    verifie_par BIGINT,
    note_moyenne DECIMAL(3, 2) DEFAULT 0,
    nb_avis INT DEFAULT 0,
    nb_biens INT DEFAULT 0,
    revenus_totaux DECIMAL(15, 2) DEFAULT 0,
    iban VARCHAR(34),
    
    INDEX idx_hote_ville (ville),
    INDEX idx_hote_verifie (verifie),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (verifie_par) REFERENCES utilisateur(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Profil spécifique pour les locataires
CREATE TABLE locataire_profil (
    utilisateur_id BIGINT PRIMARY KEY,
    date_naissance DATE,
    adresse VARCHAR(255),
    ville VARCHAR(100),
    code_postal VARCHAR(10),
    pays VARCHAR(100) DEFAULT 'France',
    piece_identite_type ENUM('CNI', 'PASSEPORT', 'PERMIS') DEFAULT NULL,
    piece_identite_numero VARCHAR(50),
    piece_identite_url VARCHAR(500),
    verifie BOOLEAN DEFAULT FALSE,
    date_verification DATETIME,
    verifie_par BIGINT,
    nb_reservations INT DEFAULT 0,
    nb_avis_donnes INT DEFAULT 0,
    
    INDEX idx_locataire_verifie (verifie),
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (verifie_par) REFERENCES utilisateur(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Profil spécifique pour les administrateurs
CREATE TABLE admin_profil (
    utilisateur_id BIGINT PRIMARY KEY,
    niveau ENUM('SUPER_ADMIN', 'ADMIN', 'MODERATEUR') DEFAULT 'MODERATEUR',
    departement VARCHAR(50),
    cree_par BIGINT,
    date_nomination DATETIME DEFAULT CURRENT_TIMESTAMP,
    permissions TEXT,
    
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (cree_par) REFERENCES utilisateur(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================================
-- SECTION 3: TABLES MÉTIER - BIENS ET LOCATIONS
-- ============================================================================

-- Types de biens
CREATE TABLE type_logement (
    id INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    icone VARCHAR(50),
    actif BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB ;

-- Table des biens
CREATE TABLE logement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hote_id BIGINT NOT NULL,
    type_logement_id INT NOT NULL DEFAULT 1,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    adresse VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10),
    superficie DECIMAL(10, 2),
    capacite INT DEFAULT 2,
    disponible BOOLEAN,
	statut ENUM('BROUILLON', 'EN_ATTENTE', 'DISPONIBLE', 'INDISPONIBLE', 'ARCHIVE') DEFAULT 'BROUILLON',
    prix_nuit DECIMAL(10, 2) NOT NULL DEFAULT 30.0,
    note_moyenne DECIMAL(3, 2) DEFAULT 0,
    nb_avis INT DEFAULT 0,
    nb_reservations INT DEFAULT 0,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_modification DATETIME,
    date_publication DATETIME,
    
    INDEX idx_bien_hote (hote_id),
    INDEX idx_bien_prix (prix_nuit),
    INDEX idx_bien_capacite (capacite),

    FOREIGN KEY (hote_id) REFERENCES utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (type_logement_id) REFERENCES type_logement(id)
) ENGINE=InnoDB;


-- INSERT INTO logement 
-- (titre, description, adresse, ville, prix_nuit, capacite, disponible, hote_id)
-- VALUES
-- ('Studio cosy centre-ville', 
--  'Petit studio lumineux idéal pour voyageurs solo ou couples. Proche commerces et transports.', 
--  '12 rue des Tilleuls', 'Nancy', 48.00, 2, TRUE, 1),

-- ('Appartement moderne avec balcon', 
--  'Appartement rénové avec cuisine équipée, balcon et vue dégagée. Parfait pour séjours professionnels.', 
--  '5 avenue du Général Leclerc', 'Metz', 72.50, 4, TRUE, 2),

-- ('Maison familiale 3 chambres', 
--  'Grande maison avec jardin, idéale pour familles. Quartier calme et proche écoles.', 
--  '18 rue des Acacias', 'Toul', 120.00, 6, FALSE, 3),

-- ('Loft industriel spacieux', 
--  'Loft style industriel avec grande hauteur sous plafond, décoration moderne et espace bureau.', 
--  '22 rue Saint-Nicolas', 'Nancy', 95.00, 3, TRUE, 1),

-- ('Chambre privée chez l’habitant', 
--  'Chambre simple mais confortable, accès salle de bain partagée et cuisine.', 
--  '3 impasse des Jardins', 'Villers-lès-Nancy', 30.00, 1, TRUE, 4);
 
-- Photos des biens
CREATE TABLE photo_logement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    logement_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    description VARCHAR(255),
    est_principale BOOLEAN DEFAULT FALSE,
    date_ajout DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_photo_bien (logement_id),
    FOREIGN KEY (logement_id) REFERENCES logement(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Équipements disponibles
CREATE TABLE equipement (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    icone VARCHAR(50),
    categorie ENUM('ESSENTIEL', 'CONFORT', 'CUISINE', 'SALLE_BAIN', 'CHAMBRE', 'EXTERIEUR', 'SECURITE', 'MULTIMEDIA', 'FAMILLE', 'ACCESSIBILITE', 'AUTRE') DEFAULT 'AUTRE',
    actif BOOLEAN DEFAULT TRUE,
    ordre INT DEFAULT 0
) ENGINE=InnoDB;

-- Association logement-équipement
CREATE TABLE logement_equipement (
    logement_id BIGINT NOT NULL,
    equipement_id INT NOT NULL,
    PRIMARY KEY (logement_id, equipement_id),
    FOREIGN KEY (logement_id) REFERENCES logement(id) ON DELETE CASCADE,
    FOREIGN KEY (equipement_id) REFERENCES equipement(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Disponibilités et tarifs spéciaux
CREATE TABLE disponibilite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    logement_id BIGINT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    disponible BOOLEAN DEFAULT TRUE,
    prix_special DECIMAL(10, 2),
    duree_min_sejour INT DEFAULT 1,
    note VARCHAR(255),
    
    INDEX idx_dispo_bien (logement_id),
    INDEX idx_dispo_dates (date_debut, date_fin),
    FOREIGN KEY (logement_id) REFERENCES logement(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================================
-- SECTION 4: TABLES MÉTIER - RÉSERVATIONS ET PAIEMENTS
-- ============================================================================

-- Réservations (locations)
CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    logement_id BIGINT NOT NULL,
    locataire_id BIGINT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    nb_voyageurs INT DEFAULT 1,
    nb_adultes INT DEFAULT 1,
    nb_enfants INT DEFAULT 0,
    prix_nuit DECIMAL(10, 2) NOT NULL,
    prix_biensous_total DECIMAL(10, 2) NOT NULL,
    reduction DECIMAL(10, 2) DEFAULT 0,
    code_promo VARCHAR(50),
    prix_total DECIMAL(10, 2) NOT NULL,
    devise VARCHAR(3) DEFAULT 'EUR',
    date_reservation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_confirmation DATETIME,
    
    INDEX idx_reservation_bien (logement_id),
    INDEX idx_reservation_locataire (locataire_id),
    INDEX idx_reservation_dates (date_debut, date_fin),
    FOREIGN KEY (logement_id) REFERENCES logement(id),
    FOREIGN KEY (locataire_id) REFERENCES utilisateur(id)
) ENGINE=InnoDB;

-- Paiements
CREATE TABLE paiement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    montant DECIMAL(10, 2) NOT NULL,
    devise VARCHAR(3) DEFAULT 'EUR',
    type_paiement ENUM('RESERVATION', 'CAUTION', 'SUPPLEMENT', 'REMBOURSEMENT') DEFAULT 'RESERVATION',
    methode ENUM('CARTE', 'PAYPAL', 'VIREMENT', 'STRIPE', 'APPLE_PAY', 'GOOGLE_PAY') DEFAULT 'CARTE',
    statut ENUM('EN_ATTENTE', 'EN_COURS', 'VALIDE', 'ECHOUE', 'REMBOURSE', 'PARTIELLEMENT_REMBOURSE', 'ANNULE') DEFAULT 'EN_ATTENTE',
    reference_externe VARCHAR(255),
    details_methode TEXT,
    date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_validation DATETIME,
    date_remboursement DATETIME,
    motif_echec TEXT,
    
    INDEX idx_paiement_reservation (reservation_id),
    INDEX idx_paiement_statut (statut),
    FOREIGN KEY (reservation_id) REFERENCES reservation(id)
) ENGINE=InnoDB;


-- ============================================================================
-- SECTION 5: TABLES MÉTIER - AVIS ET COMMUNICATIONS
-- ============================================================================

-- Avis sur les biens
CREATE TABLE avis (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL UNIQUE,
    note_globale INT NOT NULL CHECK (note_globale BETWEEN 1 AND 5),
    note_proprete INT CHECK (note_proprete BETWEEN 1 AND 5),
    note_communication INT CHECK (note_communication BETWEEN 1 AND 5),
    note_emplacement INT CHECK (note_emplacement BETWEEN 1 AND 5),
    note_arrivee INT CHECK (note_arrivee BETWEEN 1 AND 5),
    note_rapport_qualite_prix INT CHECK (note_rapport_qualite_prix BETWEEN 1 AND 5),
    commentaire TEXT,
    points_positifs TEXT,
    points_negatifs TEXT,
    reponse_hote TEXT,
    date_avis DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_reponse DATETIME,
    visible BOOLEAN DEFAULT TRUE,
    
    INDEX idx_avis_reservation (reservation_id),
    INDEX idx_avis_visible (visible),
    FOREIGN KEY (reservation_id) REFERENCES reservation(id)
) ENGINE=InnoDB;


-- Logs d'activité
CREATE TABLE log_activite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id BIGINT,
    action VARCHAR(100) NOT NULL,
    table_concernee VARCHAR(50),
    enregistrement_id BIGINT,
    anciennes_valeurs JSON,
    nouvelles_valeurs JSON,
    details TEXT,
    adresse_ip VARCHAR(45),
    user_agent VARCHAR(500),
    date_action DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_log_utilisateur (utilisateur_id),
    INDEX idx_log_date (date_action),
    INDEX idx_log_action (action),
    INDEX idx_log_table (table_concernee)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Statistiques journalières
CREATE TABLE statistique_journaliere (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_stat DATE NOT NULL UNIQUE,
    nb_nouveaux_utilisateurs INT DEFAULT 0,
    nb_nouveaux_hotes INT DEFAULT 0,
    nb_nouveaux_locataires INT DEFAULT 0,
    nb_nouvelles_reservations INT DEFAULT 0,
    nb_reservations_confirmees INT DEFAULT 0,
    nb_reservations_annulees INT DEFAULT 0,
    montant_total_reservations DECIMAL(15, 2) DEFAULT 0,
    nb_nouveaux_biens INT DEFAULT 0,
    nb_avis INT DEFAULT 0,
    nb_messages INT DEFAULT 0,
    nb_visites INT DEFAULT 0,
    
    INDEX idx_stat_date (date_stat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Paramètres système
CREATE TABLE parametre (
    cle VARCHAR(100) PRIMARY KEY,
    valeur TEXT NOT NULL,
    description TEXT,
    type_valeur ENUM('STRING', 'INT', 'DECIMAL', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    modifiable BOOLEAN DEFAULT TRUE,
    date_modification DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- SECTION 7: VUES
-- ============================================================================

-- Vue: Utilisateurs avec leurs rôles
CREATE VIEW v_utilisateurs_roles AS
SELECT 
    u.id,
    u.email,
    u.nom,
    u.prenom,
    u.telephone,
    u.actif,
    u.date_inscription,
    u.date_derniere_connexion,
    GROUP_CONCAT(r.code ORDER BY r.code SEPARATOR ', ') AS roles,
    GROUP_CONCAT(r.libelle ORDER BY r.libelle SEPARATOR ', ') AS roles_libelles,
    (SELECT COUNT(*) FROM utilisateur_role WHERE utilisateur_id = u.id AND role_id = 1) > 0 AS est_admin,
    (SELECT COUNT(*) FROM utilisateur_role WHERE utilisateur_id = u.id AND role_id = 2) > 0 AS est_hote,
    (SELECT COUNT(*) FROM utilisateur_role WHERE utilisateur_id = u.id AND role_id = 3) > 0 AS est_locataire
FROM utilisateur u
LEFT JOIN utilisateur_role ur ON u.id = ur.utilisateur_id
LEFT JOIN role r ON ur.role_id = r.id
GROUP BY u.id;

-- Vue: Hôtes complets (utilisateur + profil hôte)
CREATE VIEW v_hotes AS
SELECT 
    u.id,
    u.email,
    u.nom,
    u.prenom,
    u.telephone,
    u.actif,
    u.date_inscription,
    u.date_derniere_connexion,
    hp.description,
    hp.adresse,
    hp.ville,
    hp.code_postal,
    hp.pays,
    hp.siret,
    hp.photo_url,
    hp.verifie,
    hp.date_verification,
    hp.note_moyenne,
    hp.nb_avis,
    hp.nb_biens,
    hp.revenus_totaux
FROM utilisateur u
INNER JOIN utilisateur_role ur ON u.id = ur.utilisateur_id
INNER JOIN role r ON ur.role_id = r.id AND r.code = 'HOTE'
LEFT JOIN hote_profil hp ON u.id = hp.utilisateur_id;

-- Vue: Locataires complets (utilisateur + profil locataire)
CREATE VIEW v_locataires AS
SELECT 
    u.id,
    u.email,
    u.nom,
    u.prenom,
    u.telephone,
    u.actif,
    u.date_inscription,
    u.date_derniere_connexion,
    lp.date_naissance,
    lp.adresse,
    lp.ville,
    lp.code_postal,
    lp.pays,
    lp.piece_identite_type,
    lp.verifie,
    lp.date_verification,
    lp.nb_reservations,
    lp.nb_avis_donnes
FROM utilisateur u
INNER JOIN utilisateur_role ur ON u.id = ur.utilisateur_id
INNER JOIN role r ON ur.role_id = r.id AND r.code = 'LOCATAIRE'
LEFT JOIN locataire_profil lp ON u.id = lp.utilisateur_id;

-- Vue: Administrateurs complets
CREATE VIEW v_admins AS
SELECT 
    u.id,
    u.email,
    u.nom,
    u.prenom,
    u.telephone,
    u.actif,
    u.date_inscription,
    u.date_derniere_connexion,
    ap.niveau,
    ap.departement,
    ap.cree_par,
    ap.date_nomination,
    createur.prenom AS createur_prenom,
    createur.nom AS createur_nom
FROM utilisateur u
INNER JOIN utilisateur_role ur ON u.id = ur.utilisateur_id
INNER JOIN role r ON ur.role_id = r.id AND r.code = 'ADMIN'
LEFT JOIN admin_profil ap ON u.id = ap.utilisateur_id
LEFT JOIN utilisateur createur ON ap.cree_par = createur.id;

-- Vue: Biens avec détails complets
CREATE VIEW v_logements_complets AS
SELECT 
    l.*,
    tl.libelle AS type_logement_libelle,
    tl.icone AS type_logement_icone,
    u.nom AS hote_nom,
    u.prenom AS hote_prenom,
    u.email AS hote_email,
    u.telephone AS hote_telephone,
    hp.verifie AS hote_verifie,
    hp.photo_url AS hote_photo,
    hp.note_moyenne AS hote_note,
    (SELECT url FROM photo_logement WHERE logement_id = l.id AND est_principale = TRUE LIMIT 1) AS photo_principale,
    (SELECT COUNT(*) FROM photo_logement WHERE logement_id = l.id) AS nb_photos
FROM logement l
JOIN type_logement tl ON l.type_logement_id = tl.id
JOIN utilisateur u ON l.hote_id = u.id
LEFT JOIN hote_profil hp ON u.id = hp.utilisateur_id;

-- Vue: Réservations avec détails
-- CREATE VIEW v_reservations_details AS
-- SELECT 
--     r.*,
--     l.titre AS logement_titre,
--     l.ville AS logement_ville,
--     l.adresse AS bien_adresse,
--     l.photo_principale,
--     l.hote_id,
--     hote.nom AS hote_nom,
--     hote.prenom AS hote_prenom,
--     hote.email AS hote_email,
--     loc.nom AS locataire_nom,
--     loc.prenom AS locataire_prenom,
--     loc.email AS locataire_email,
--     loc.telephone AS locataire_telephone,
--     DATEDIFF(r.date_fin, r.date_debut) AS nb_nuits
-- FROM reservation r
-- JOIN logement l ON r.logement_id = l.id
-- JOIN utilisateur hote ON l.hote_id = hote.id
-- JOIN utilisateur loc ON r.locataire_id = loc.id;

-- Vue: Avis avec détails
CREATE VIEW v_avis_details AS
SELECT 
    a.*,
    r.date_debut AS sejour_debut,
    r.date_fin AS sejour_fin,
    r.logement_id,
    l.titre AS logement_titre,
    l.ville AS logement_ville,
    loc.id AS locataire_id,
    loc.prenom AS locataire_prenom,
    loc.nom AS locataire_nom
FROM avis a
JOIN reservation r ON a.reservation_id = r.id
JOIN logement l ON r.logement_id = l.id
JOIN utilisateur loc ON r.locataire_id = loc.id;

-- Vue: Statistiques par ville
-- CREATE VIEW v_stats_par_ville AS
-- SELECT 
--     l.ville,
--     COUNT(DISTINCT b.id) AS nb_biens,
--     ROUND(AVG(b.prix_nuit), 2) AS prix_moyen_nuit,
--     ROUND(AVG(b.note_moyenne), 2) AS note_moyenne,
--     SUM(b.nb_avis) AS total_avis,
--     COUNT(DISTINCT r.id) AS total_reservations,
--     COALESCE(SUM(CASE WHEN r.statut IN ('CONFIRMEE', 'TERMINEE') THEN r.prix_total ELSE 0 END), 0) AS revenus_totaux
-- FROM logement l
-- LEFT JOIN reservation r ON l.id = r.logement_id
-- WHERE l.statut IN ('DISPONIBLE', 'INDISPONIBLE')
-- GROUP BY l.ville
-- ORDER BY nb_logements DESC;


-- ============================================================================
-- SECTION 8: TRIGGERS
-- ============================================================================

DELIMITER //

-- Trigger: Mise à jour date modification utilisateur
CREATE TRIGGER tr_utilisateur_before_update
BEFORE UPDATE ON utilisateur
FOR EACH ROW
BEGIN
    SET NEW.date_modification = NOW();
END//

-- Trigger: Création automatique du profil hôte lors de l'attribution du rôle
CREATE TRIGGER tr_utilisateur_role_after_insert
AFTER INSERT ON utilisateur_role
FOR EACH ROW
BEGIN
    DECLARE role_code VARCHAR(20);
    
    SELECT code INTO role_code FROM role WHERE id = NEW.role_id;
    
    IF role_code = 'HOTE' THEN
        INSERT IGNORE INTO hote_profil (utilisateur_id) VALUES (NEW.utilisateur_id);
        
        INSERT INTO statistique_journaliere (date_stat, nb_nouveaux_hotes)
        VALUES (CURDATE(), 1)
        ON DUPLICATE KEY UPDATE nb_nouveaux_hotes = nb_nouveaux_hotes + 1;
        
    ELSEIF role_code = 'LOCATAIRE' THEN
        INSERT IGNORE INTO locataire_profil (utilisateur_id) VALUES (NEW.utilisateur_id);
        
        INSERT INTO statistique_journaliere (date_stat, nb_nouveaux_locataires)
        VALUES (CURDATE(), 1)
        ON DUPLICATE KEY UPDATE nb_nouveaux_locataires = nb_nouveaux_locataires + 1;
        
    ELSEIF role_code = 'ADMIN' THEN
        INSERT IGNORE INTO admin_profil (utilisateur_id, cree_par) VALUES (NEW.utilisateur_id, NEW.attribue_par);
    END IF;
    
    -- Log
    INSERT INTO log_activite (utilisateur_id, action, table_concernee, enregistrement_id, details)
    VALUES (NEW.utilisateur_id, 'ATTRIBUTION_ROLE', 'utilisateur_role', NEW.utilisateur_id, CONCAT('Rôle attribué: ', role_code));
END//

-- Trigger: Log inscription utilisateur
CREATE TRIGGER tr_utilisateur_after_insert
AFTER INSERT ON utilisateur
FOR EACH ROW
BEGIN
    INSERT INTO log_activite (utilisateur_id, action, table_concernee, enregistrement_id, details)
    VALUES (NEW.id, 'INSCRIPTION', 'utilisateur', NEW.id, CONCAT('Nouvel utilisateur: ', NEW.prenom, ' ', NEW.nom));
    
    INSERT INTO statistique_journaliere (date_stat, nb_nouveaux_utilisateurs)
    VALUES (CURDATE(), 1)
    ON DUPLICATE KEY UPDATE nb_nouveaux_utilisateurs = nb_nouveaux_utilisateurs + 1;
END//

-- Trigger: Mise à jour date modification bien
CREATE TRIGGER tr_logement_before_update
BEFORE UPDATE ON logement
FOR EACH ROW
BEGIN
    SET NEW.date_modification = NOW();
    
    -- Si le bien passe en disponible, enregistrer la date de publication
    IF OLD.statut != 'DISPONIBLE' AND NEW.statut = 'DISPONIBLE' AND NEW.date_publication IS NULL THEN
        SET NEW.date_publication = NOW();
    END IF;
END//

-- Trigger: Mise à jour compteur biens hôte après création
CREATE TRIGGER tr_logement_after_insert
AFTER INSERT ON logement
FOR EACH ROW
BEGIN
    UPDATE hote_profil SET nb_biens = nb_biens + 1 WHERE utilisateur_id = NEW.hote_id;
    
    INSERT INTO log_activite (utilisateur_id, action, table_concernee, enregistrement_id, details)
    VALUES (NEW.hote_id, 'CREATION_BIEN', 'bien', NEW.id, CONCAT('Nouveau bien: ', NEW.titre));
    
    INSERT INTO statistique_journaliere (date_stat, nb_nouveaux_biens)
    VALUES (CURDATE(), 1)
    ON DUPLICATE KEY UPDATE nb_nouveaux_biens = nb_nouveaux_biens + 1;
END//

-- Trigger: Mise à jour compteur biens hôte après suppression
CREATE TRIGGER tr_logement_after_delete
AFTER DELETE ON logement
FOR EACH ROW
BEGIN
    UPDATE hote_profil SET nb_logements = GREATEST(nb_logements - 1, 0) WHERE utilisateur_id = OLD.hote_id;
END//

-- Trigger: Génération référence réservation et vérifications
CREATE TRIGGER tr_reservation_before_insert
BEFORE INSERT ON reservation
FOR EACH ROW
BEGIN
    DECLARE conflit INT;
    DECLARE logement_statut VARCHAR(20);
    DECLARE logement_capacite INT;
    
     
    -- Vérifier les dates
    IF NEW.date_fin <= NEW.date_debut THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La date de fin doit être postérieure à la date de début';
    END IF;
    
    -- Vérifier que le bien est disponible
    SELECT statut, capacite_max INTO logement_statut, logement_capacite FROM logement WHERE id = NEW.logement_id;
    
    IF logement_statut != 'DISPONIBLE' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ce bien n''est pas disponible à la réservation';
    END IF;
    
    -- Vérifier la capacité
    IF NEW.nb_voyageurs > bien_capacite THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Le nombre de voyageurs dépasse la capacité du bien';
    END IF;
    
    -- Vérifier les conflits de dates
    SELECT COUNT(*) INTO conflit FROM reservation 
    WHERE logement_id = NEW.logement_id 
    AND statut IN ('CONFIRMEE', 'EN_COURS')
    AND ((NEW.date_debut BETWEEN date_debut AND DATE_SUB(date_fin, INTERVAL 1 DAY)) 
         OR (NEW.date_fin BETWEEN DATE_ADD(date_debut, INTERVAL 1 DAY) AND date_fin)
         OR (date_debut BETWEEN NEW.date_debut AND NEW.date_fin));
    
    IF conflit > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ce bien est déjà réservé pour ces dates';
    END IF;
END//



-- Trigger: Actions après mise à jour réservation
-- CREATE TRIGGER tr_reservation_after_update
-- AFTER UPDATE ON reservation
-- FOR EACH ROW
-- BEGIN
--     DECLARE hote_id BIGINT;
--     
--     SELECT l.hote_id INTO hote_id FROM bien b WHERE l.id = NEW._id;
--     
--     -- Réservation confirmée
--     IF OLD.statut != 'CONFIRMEE' AND NEW.statut = 'CONFIRMEE' THEN
--         INSERT INTO notification (utilisateur_id, type_notification, titre, contenu, lien)
--         VALUES (NEW.locataire_id, 'RESERVATION', 'Réservation confirmée !',
--                 CONCAT('Votre réservation ', NEW.reference, ' a été confirmée'),
--                 CONCAT('/locataire/reservations/', NEW.id));
--         
--         UPDATE statistique_journaliere SET nb_reservations_confirmees = nb_reservations_confirmees + 1
--         WHERE date_stat = CURDATE();
--     END IF;
--     
--     -- Réservation annulée
--     IF OLD.statut NOT IN ('ANNULEE', 'REFUSEE') AND NEW.statut IN ('ANNULEE', 'REFUSEE') THEN
--         -- Notifier le locataire
--         INSERT INTO notification (utilisateur_id, type_notification, titre, contenu, lien)
--         VALUES (NEW.locataire_id, 'RESERVATION', 
--                 CASE NEW.statut WHEN 'ANNULEE' THEN 'Réservation annulée' ELSE 'Réservation refusée' END,
--                 CONCAT('La réservation ', NEW.reference, ' a été ', LOWER(NEW.statut)),
--                 CONCAT('/locataire/reservations/', NEW.id));
--         
--         -- Notifier l'hôte si annulée par le locataire
--         IF NEW.annulee_par = 'LOCATAIRE' THEN
--             INSERT INTO notification (utilisateur_id, type_notification, titre, contenu, lien)
--             VALUES (hote_id, 'RESERVATION', 'Réservation annulée par le locataire',
--                     CONCAT('La réservation ', NEW.reference, ' a été annulée'),
--                     CONCAT('/hote/reservations/', NEW.id));
--         END IF;
--         
--         UPDATE statistique_journaliere SET nb_reservations_annulees = nb_reservations_annulees + 1
--         WHERE date_stat = CURDATE();
--     END IF;
-- END//

-- Trigger: Mise à jour notes après avis
CREATE TRIGGER tr_avis_after_insert
AFTER INSERT ON avis
FOR EACH ROW
BEGIN
    DECLARE v_logement_id BIGINT;
    DECLARE v_hote_id BIGINT;
    DECLARE v_locataire_id BIGINT;
    
    -- Récupérer les IDs
    SELECT r.logement_id, r.locataire_id, l.hote_id 
    INTO v_logement_id, v_locataire_id, v_hote_id
    FROM reservation r
    JOIN logement l ON r.logement_id = l.id
    WHERE r.id = NEW.reservation_id;
    
    -- Mettre à jour la note du bien
    UPDATE logement SET 
        note_moyenne = (
            SELECT AVG(a.note_globale) 
            FROM avis a 
            JOIN reservation r ON a.reservation_id = r.id 
            WHERE r.logement_id = v_logement_id AND a.visible = TRUE
        ),
        nb_avis = (
            SELECT COUNT(*) 
            FROM avis a 
            JOIN reservation r ON a.reservation_id = r.id 
            WHERE r.logement_id = v_logement_id AND a.visible = TRUE
        )
    WHERE id = v_logement_id;
    
    -- Mettre à jour la note de l'hôte
    UPDATE hote_profil SET 
        note_moyenne = (
            SELECT AVG(l.note_moyenne) 
            FROM logement l 
            WHERE l.hote_id = v_hote_id AND l.nb_avis > 0
        ),
        nb_avis = (
            SELECT SUM(l.nb_avis) 
            FROM logement l
            WHERE l.hote_id = v_hote_id
        )
    WHERE utilisateur_id = v_hote_id;
    
    -- Mettre à jour le compteur du locataire
    UPDATE locataire_profil SET nb_avis_donnes = nb_avis_donnes + 1 
    WHERE utilisateur_id = v_locataire_id;
    
    -- Notifier l'hôte
    INSERT INTO notification (utilisateur_id, type_notification, titre, contenu, lien)
    VALUES (v_hote_id, 'AVIS', 'Nouvel avis reçu',
            CONCAT('Vous avez reçu un avis de ', NEW.note_globale, ' étoiles'),
            CONCAT('/hote/avis/', NEW.id));
    
    -- Stats
    INSERT INTO statistique_journaliere (date_stat, nb_avis)
    VALUES (CURDATE(), 1)
    ON DUPLICATE KEY UPDATE nb_avis = nb_avis + 1;
END//

-- Trigger: Confirmation automatique après paiement validé
CREATE TRIGGER tr_paiement_after_update
AFTER UPDATE ON paiement
FOR EACH ROW
BEGIN
    IF OLD.statut != 'VALIDE' AND NEW.statut = 'VALIDE' AND NEW.type_paiement = 'RESERVATION' THEN
        UPDATE reservation SET 
            statut = 'CONFIRMEE',
            date_confirmation = NOW()
        WHERE id = NEW.reservation_id AND statut = 'EN_ATTENTE';
    END IF;
END//

-

-- ============================================================================
-- SECTION 9: PROCÉDURES STOCKÉES
-- ============================================================================

DELIMITER //

-- Procédure: Inscription d'un utilisateur avec rôle
CREATE PROCEDURE sp_inscrire_utilisateur(
    IN p_email VARCHAR(255),
    IN p_mot_de_passe VARCHAR(255),
    IN p_nom VARCHAR(100),
    IN p_prenom VARCHAR(100),
    IN p_telephone VARCHAR(20),
    IN p_role VARCHAR(20),
    OUT p_utilisateur_id BIGINT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_role_id INT;
    DECLARE v_email_existe INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_utilisateur_id = NULL;
        SET p_message = 'Erreur lors de l''inscription';
    END;
    
    START TRANSACTION;
    
    -- Vérifier si l'email existe
    SELECT COUNT(*) INTO v_email_existe FROM utilisateur WHERE email = LOWER(p_email);
    
    IF v_email_existe > 0 THEN
        SET p_utilisateur_id = NULL;
        SET p_message = 'Cet email est déjà utilisé';
        ROLLBACK;
    ELSE
        -- Récupérer l'ID du rôle
        SELECT id INTO v_role_id FROM role WHERE code = p_role;
        
        IF v_role_id IS NULL THEN
            SET p_utilisateur_id = NULL;
            SET p_message = 'Rôle invalide';
            ROLLBACK;
        ELSE
            -- Créer l'utilisateur
            INSERT INTO utilisateur (email, mot_de_passe, nom, prenom, telephone)
            VALUES (LOWER(p_email), p_mot_de_passe, p_nom, p_prenom, p_telephone);
            
            SET p_utilisateur_id = LAST_INSERT_ID();
            
            -- Attribuer le rôle
            INSERT INTO utilisateur_role (utilisateur_id, role_id)
            VALUES (p_utilisateur_id, v_role_id);
            
            SET p_message = 'Inscription réussie';
            COMMIT;
        END IF;
    END IF;
END//

-- Procédure: Authentification
CREATE PROCEDURE sp_authentifier(
    IN p_email VARCHAR(255),
    IN p_mot_de_passe VARCHAR(255),
    OUT p_utilisateur_id BIGINT,
    OUT p_resultat VARCHAR(50)
)
BEGIN
    DECLARE v_id BIGINT;
    DECLARE v_actif BOOLEAN;
    DECLARE v_hash VARCHAR(255);
    
    SELECT id, actif, mot_de_passe INTO v_id, v_actif, v_hash
    FROM utilisateur 
    WHERE email = LOWER(p_email);
    
    IF v_id IS NULL THEN
        SET p_utilisateur_id = NULL;
        SET p_resultat = 'EMAIL_INCONNU';
    ELSEIF v_hash != p_mot_de_passe THEN
        SET p_utilisateur_id = NULL;
        SET p_resultat = 'MOT_DE_PASSE_INCORRECT';
    ELSEIF v_actif = FALSE THEN
        SET p_utilisateur_id = NULL;
        SET p_resultat = 'COMPTE_DESACTIVE';
    ELSE
        SET p_utilisateur_id = v_id;
        SET p_resultat = 'SUCCES';
        
        UPDATE utilisateur SET date_derniere_connexion = NOW() WHERE id = v_id;
    END IF;
END//

-- Procédure: Ajouter un rôle à un utilisateur
CREATE PROCEDURE sp_ajouter_role(
    IN p_utilisateur_id BIGINT,
    IN p_role_code VARCHAR(20),
    IN p_attribue_par BIGINT,
    OUT p_resultat VARCHAR(255)
)
BEGIN
    DECLARE v_role_id INT;
    DECLARE v_existe INT DEFAULT 0;
    
    -- Vérifier que le rôle existe
    SELECT id INTO v_role_id FROM role WHERE code = p_role_code AND actif = TRUE;
    
    IF v_role_id IS NULL THEN
        SET p_resultat = 'Rôle invalide ou inactif';
    ELSE
        -- Vérifier si l'utilisateur a déjà ce rôle
        SELECT COUNT(*) INTO v_existe FROM utilisateur_role 
        WHERE utilisateur_id = p_utilisateur_id AND role_id = v_role_id;
        
        IF v_existe > 0 THEN
            SET p_resultat = 'L''utilisateur possède déjà ce rôle';
        ELSE
            INSERT INTO utilisateur_role (utilisateur_id, role_id, attribue_par)
            VALUES (p_utilisateur_id, v_role_id, p_attribue_par);
            
            SET p_resultat = 'Rôle attribué avec succès';
        END IF;
    END IF;
END//

-- Procédure: Créer une réservation avec transaction
CREATE PROCEDURE sp_creer_reservation(
    IN p_bien_id BIGINT,
    IN p_locataire_id BIGINT,
    IN p_date_debut DATE,
    IN p_date_fin DATE,
    IN p_nb_voyageurs INT,
    IN p_nb_adultes INT,
    IN p_nb_enfants INT,
    IN p_message TEXT,
    IN p_code_promo VARCHAR(50),
    OUT p_reservation_id BIGINT,
    OUT p_prix_total DECIMAL(10,2),
    OUT p_resultat VARCHAR(255)
)
BEGIN
    DECLARE v_prix_nuit DECIMAL(10,2);
    DECLARE v_frais_menage DECIMAL(10,2);
    DECLARE v_nb_nuits INT;
    DECLARE v_prix_sous_total DECIMAL(10,2);
    DECLARE v_frais_service DECIMAL(10,2);
    DECLARE v_taxe_sejour DECIMAL(10,2);
    DECLARE v_reduction DECIMAL(10,2) DEFAULT 0;
    DECLARE v_capacite INT;
    DECLARE v_statut VARCHAR(20);
    DECLARE v_promo_valide BOOLEAN DEFAULT FALSE;
    DECLARE v_promo_type VARCHAR(20);
    DECLARE v_promo_valeur DECIMAL(10,2);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_reservation_id = NULL;
        SET p_prix_total = NULL;
        GET DIAGNOSTICS CONDITION 1 p_resultat = MESSAGE_TEXT;
    END;
    
    START TRANSACTION;
    
    -- Récupérer les infos du bien (avec verrouillage)
    SELECT prix_nuit, frais_menage, capacite_max, statut 
    INTO v_prix_nuit, v_frais_menage, v_capacite, v_statut
    FROM bien WHERE id = p_bien_id FOR UPDATE;
    
    IF v_statut IS NULL THEN
        SET p_resultat = 'Bien non trouvé';
        ROLLBACK;
    ELSEIF v_statut != 'DISPONIBLE' THEN
        SET p_resultat = 'Ce bien n''est pas disponible';
        ROLLBACK;
    ELSEIF p_nb_voyageurs > v_capacite THEN
        SET p_resultat = CONCAT('Capacité maximale dépassée (max: ', v_capacite, ')');
        ROLLBACK;
    ELSE
        -- Vérifier le code promo
        IF p_code_promo IS NOT NULL AND p_code_promo != '' THEN
            SELECT TRUE, type_reduction, valeur INTO v_promo_valide, v_promo_type, v_promo_valeur
            FROM code_promo
            WHERE code = p_code_promo 
            AND actif = TRUE
            AND (date_debut IS NULL OR date_debut <= NOW())
            AND (date_fin IS NULL OR date_fin >= NOW())
            AND (usage_max IS NULL OR usage_actuel < usage_max);
        END IF;
        
        -- Calculer les prix
        SET v_nb_nuits = DATEDIFF(p_date_fin, p_date_debut);
        SET v_prix_sous_total = v_prix_nuit * v_nb_nuits;
        SET v_frais_service = v_prix_sous_total * 0.10; -- 10% de frais de service
        SET v_taxe_sejour = v_nb_nuits * p_nb_adultes * 1.50; -- 1.50€ par nuit par adulte
        
        -- Appliquer la réduction
        IF v_promo_valide THEN
            IF v_promo_type = 'POURCENTAGE' THEN
                SET v_reduction = v_prix_sous_total * (v_promo_valeur / 100);
            ELSE
                SET v_reduction = v_promo_valeur;
            END IF;
            
            -- Mettre à jour l'usage du code promo
            UPDATE code_promo SET usage_actuel = usage_actuel + 1 WHERE code = p_code_promo;
        END IF;
        
        SET p_prix_total = v_prix_sous_total + v_frais_menage + v_frais_service + v_taxe_sejour - v_reduction;
        
        -- Créer la réservation
        INSERT INTO reservation (
            bien_id, locataire_id, date_debut, date_fin, 
            nb_voyageurs, nb_adultes, nb_enfants,
            prix_nuit, prix_sous_total, frais_service, frais_menage, taxe_sejour,
            reduction, code_promo, prix_total, message_locataire
        ) VALUES (
            p_bien_id, p_locataire_id, p_date_debut, p_date_fin,
            p_nb_voyageurs, p_nb_adultes, p_nb_enfants,
            v_prix_nuit, v_prix_sous_total, v_frais_service, v_frais_menage, v_taxe_sejour,
            v_reduction, p_code_promo, p_prix_total, p_message
        );
        
        SET p_reservation_id = LAST_INSERT_ID();
        SET p_resultat = 'Réservation créée avec succès';
        
        COMMIT;
    END IF;
END//

-- Procédure: Annuler une réservation avec calcul de remboursement
CREATE PROCEDURE sp_annuler_reservation(
    IN p_reservation_id BIGINT,
    IN p_utilisateur_id BIGINT,
    IN p_type_utilisateur ENUM('LOCATAIRE', 'HOTE', 'ADMIN'),
    IN p_motif TEXT,
    OUT p_resultat VARCHAR(255),
    OUT p_remboursement DECIMAL(10,2)
)
BEGIN
    DECLARE v_statut VARCHAR(20);
    DECLARE v_date_debut DATE;
    DECLARE v_prix_total DECIMAL(10,2);
    DECLARE v_delai_annulation INT;
    DECLARE v_jours_avant INT;
    DECLARE v_locataire_id BIGINT;
    DECLARE v_hote_id BIGINT;
    
    -- Récupérer les infos de la réservation
    SELECT r.statut, r.date_debut, r.prix_total, r.locataire_id, b.hote_id, b.delai_annulation
    INTO v_statut, v_date_debut, v_prix_total, v_locataire_id, v_hote_id, v_delai_annulation
    FROM reservation r
    JOIN logement l ON r.logement_id = l.id
    WHERE r.id = p_reservation_id;
    
    IF v_statut IS NULL THEN
        SET p_resultat = 'Réservation non trouvée';
        SET p_remboursement = 0;
    ELSEIF v_statut IN ('ANNULEE', 'REFUSEE', 'TERMINEE') THEN
        SET p_resultat = 'Cette réservation ne peut pas être annulée';
        SET p_remboursement = 0;
    ELSEIF p_type_utilisateur = 'LOCATAIRE' AND v_locataire_id != p_utilisateur_id THEN
        SET p_resultat = 'Vous n''êtes pas autorisé à annuler cette réservation';
        SET p_remboursement = 0;
    ELSEIF p_type_utilisateur = 'HOTE' AND v_hote_id != p_utilisateur_id THEN
        SET p_resultat = 'Vous n''êtes pas autorisé à annuler cette réservation';
        SET p_remboursement = 0;
    ELSE
        SET v_jours_avant = DATEDIFF(v_date_debut, CURDATE());
        
        -- Calcul du remboursement selon les conditions
        IF p_type_utilisateur = 'HOTE' OR p_type_utilisateur = 'ADMIN' THEN
            SET p_remboursement = v_prix_total;
        ELSEIF v_jours_avant >= v_delai_annulation THEN
            SET p_remboursement = v_prix_total;
        ELSEIF v_jours_avant >= 3 THEN
            SET p_remboursement = v_prix_total * 0.50;
        ELSEIF v_jours_avant >= 1 THEN
            SET p_remboursement = v_prix_total * 0.25;
        ELSE
            SET p_remboursement = 0;
        END IF;
        
        UPDATE reservation SET 
            statut = 'ANNULEE',
            date_annulation = NOW(),
            motif_annulation = p_motif,
            annulee_par = p_type_utilisateur,
            montant_rembourse = p_remboursement
        WHERE id = p_reservation_id;
        
        SET p_resultat = 'Réservation annulée';
    END IF;
END//

-- Procédure: Rechercher des biens
-- CREATE PROCEDURE sp_rechercher_logements(
--     IN p_ville VARCHAR(100),
--     IN p_date_debut DATE,
--     IN p_date_fin DATE,
--     IN p_nb_voyageurs INT,
--     IN p_prix_min DECIMAL(10,2),
--     IN p_prix_max DECIMAL(10,2),
--     IN p_type_bien_id INT,
--     IN p_equipements TEXT,
--     IN p_note_min DECIMAL(3,2),
--     IN p_tri VARCHAR(20),
--     IN p_page INT,
--     IN p_par_page INT
-- )
-- BEGIN
--     DECLARE v_offset INT;
--     SET v_offset = (COALESCE(p_page, 1) - 1) * COALESCE(p_par_page, 20);
--     
--     SELECT SQL_CALC_FOUND_ROWS
--         l.id, b.titre, b.description, b.ville, b.code_postal, b.pays,
--         l.prix_nuit, b.capacite_max, l.nb_chambres, b.nb_lits, b.superficie,
--         l.note_moyenne, b.nb_avis,
--         tb.libelle AS type_bien,
--         u.prenom AS hote_prenom, u.nom AS hote_nom,
--         hp.verifie AS hote_verifie, hp.photo_url AS hote_photo,
--         (SELECT url FROM photo_bien WHERE bien_id = b.id AND est_principale = TRUE LIMIT 1) AS photo_principale
--     FROM bien b
--     JOIN type_bien tb ON b.type_bien_id = tb.id
--     JOIN utilisateur u ON b.hote_id = u.id
--     LEFT JOIN hote_profil hp ON u.id = hp.utilisateur_id
--     WHERE b.statut = 'DISPONIBLE'
--     AND (p_ville IS NULL OR b.ville LIKE CONCAT('%', p_ville, '%'))
--     AND (p_nb_voyageurs IS NULL OR b.capacite_max >= p_nb_voyageurs)
--     AND (p_prix_min IS NULL OR b.prix_nuit >= p_prix_min)
--     AND (p_prix_max IS NULL OR b.prix_nuit <= p_prix_max)
--     AND (p_type_bien_id IS NULL OR b.type_bien_id = p_type_bien_id)
--     AND (p_note_min IS NULL OR b.note_moyenne >= p_note_min)
--     AND (p_date_debut IS NULL OR p_date_fin IS NULL OR NOT EXISTS (
--         SELECT 1 FROM reservation r 
--         WHERE r.bien_id = b.id 
--         AND r.statut IN ('CONFIRMEE', 'EN_COURS')
--         AND ((p_date_debut BETWEEN r.date_debut AND DATE_SUB(r.date_fin, INTERVAL 1 DAY)) 
--              OR (p_date_fin BETWEEN DATE_ADD(r.date_debut, INTERVAL 1 DAY) AND r.date_fin)
--              OR (r.date_debut BETWEEN p_date_debut AND p_date_fin))
--     ))
--     ORDER BY 
--         CASE WHEN p_tri = 'prix_asc' THEN b.prix_nuit END ASC,
--         CASE WHEN p_tri = 'prix_desc' THEN b.prix_nuit END DESC,
--         CASE WHEN p_tri = 'note' THEN b.note_moyenne END DESC,
--         CASE WHEN p_tri = 'avis' THEN b.nb_avis END DESC,
--         CASE ELSE b.date_creation END DESC
--     LIMIT v_offset, COALESCE(p_par_page, 20);
-- END//

-- Procédure: Statistiques tableau de bord hôte
-- CREATE PROCEDURE sp_stats_hote(
--     IN p_hote_id BIGINT,
--     IN p_periode VARCHAR(20)
-- )
-- BEGIN
--     DECLARE v_date_debut DATE;
--     
--     SET v_date_debut = CASE p_periode
--         WHEN 'semaine' THEN DATE_SUB(CURDATE(), INTERVAL 7 DAY)
--         WHEN 'mois' THEN DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
--         WHEN 'trimestre' THEN DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
--         WHEN 'annee' THEN DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
--         ELSE DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
--     END;
--     
--     -- Stats générales
--     SELECT 
--         hp.nb_biens,
--         hp.note_moyenne,
--         hp.nb_avis,
--         hp.revenus_totaux,
--         (SELECT COUNT(*) FROM reservation r JOIN bien b ON r.bien_id = b.id 
--          WHERE b.hote_id = p_hote_id AND r.statut = 'EN_ATTENTE') AS reservations_en_attente,
--         (SELECT COUNT(*) FROM reservation r JOIN bien b ON r.bien_id = b.id 
--          WHERE b.hote_id = p_hote_id AND r.statut = 'CONFIRMEE' AND r.date_debut >= CURDATE()) AS reservations_a_venir,
--         (SELECT COUNT(*) FROM reservation r JOIN bien b ON r.bien_id = b.id 
--          WHERE b.hote_id = p_hote_id AND r.date_reservation >= v_date_debut) AS nouvelles_reservations_periode,
--         (SELECT COALESCE(SUM(r.prix_total), 0) FROM reservation r JOIN bien b ON r.bien_id = b.id 
--          WHERE b.hote_id = p_hote_id AND r.statut IN ('CONFIRMEE', 'TERMINEE') 
--          AND r.date_reservation >= v_date_debut) AS revenus_periode,
--         (SELECT COUNT(*) FROM message m JOIN conversation c ON m.conversation_id = c.id
--          WHERE (c.participant1_id = p_hote_id OR c.participant2_id = p_hote_id) 
--          AND m.lu = FALSE AND m.expediteur_id != p_hote_id) AS messages_non_lus
--     FROM hote_profil hp
--     WHERE hp.utilisateur_id = p_hote_id;
-- END//

-- -- Procédure: Statistiques globales admin
-- CREATE PROCEDURE sp_stats_admin(
--     IN p_date_debut DATE,
--     IN p_date_fin DATE
-- )
-- BEGIN
--     SELECT 
--         (SELECT COUNT(*) FROM utilisateur WHERE date_inscription BETWEEN p_date_debut AND p_date_fin) AS nouveaux_utilisateurs,
--         (SELECT COUNT(*) FROM v_hotes WHERE date_inscription BETWEEN p_date_debut AND p_date_fin) AS nouveaux_hotes,
--         (SELECT COUNT(*) FROM v_locataires WHERE date_inscription BETWEEN p_date_debut AND p_date_fin) AS nouveaux_locataires,
--         (SELECT COUNT(*) FROM bien WHERE date_creation BETWEEN p_date_debut AND p_date_fin) AS nouveaux_biens,
--         (SELECT COUNT(*) FROM reservation WHERE date_reservation BETWEEN p_date_debut AND p_date_fin) AS nouvelles_reservations,
--         (SELECT COUNT(*) FROM reservation WHERE statut IN ('CONFIRMEE', 'TERMINEE') AND date_confirmation BETWEEN p_date_debut AND p_date_fin) AS reservations_confirmees,
--         (SELECT COALESCE(SUM(prix_total), 0) FROM reservation WHERE statut IN ('CONFIRMEE', 'TERMINEE') AND date_reservation BETWEEN p_date_debut AND p_date_fin) AS chiffre_affaires,
--         (SELECT COUNT(*) FROM avis WHERE date_avis BETWEEN p_date_debut AND p_date_fin) AS nouveaux_avis,
--         (SELECT ROUND(AVG(note_globale), 2) FROM avis WHERE date_avis BETWEEN p_date_debut AND p_date_fin) AS note_moyenne_avis;
-- END//

-- DELIMITER ;


-- ============================================================================
-- SECTION 10: FONCTIONS
-- ============================================================================

DELIMITER //

-- Fonction: Vérifier si un utilisateur a un rôle
CREATE FUNCTION fn_a_role(
    p_utilisateur_id BIGINT,
    p_role_code VARCHAR(20)
) RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE v_existe INT DEFAULT 0;
    
    SELECT COUNT(*) INTO v_existe
    FROM utilisateur_role ur
    JOIN role r ON ur.role_id = r.id
    WHERE ur.utilisateur_id = p_utilisateur_id AND r.code = p_role_code;
    
    RETURN v_existe > 0;
END//

-- Fonction: Calculer le prix d'une réservation
CREATE FUNCTION fn_calculer_prix(
    p_logement_id BIGINT,
    p_date_debut DATE,
    p_date_fin DATE,
    p_nb_adultes INT
) RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE v_prix_nuit DECIMAL(10,2);
    DECLARE v_frais_menage DECIMAL(10,2);
    DECLARE v_nb_nuits INT;
    DECLARE v_prix_sous_total DECIMAL(10,2);
    DECLARE v_frais_service DECIMAL(10,2);
    DECLARE v_taxe_sejour DECIMAL(10,2);
    
    SELECT prix_nuit, frais_menage INTO v_prix_nuit, v_frais_menage
    FROM logement WHERE id = p_logement_id;
    
    IF v_prix_nuit IS NULL THEN
        RETURN NULL;
    END IF;
    
    SET v_nb_nuits = DATEDIFF(p_date_fin, p_date_debut);
    SET v_prix_sous_total = v_prix_nuit * v_nb_nuits;
    SET v_frais_service = v_prix_sous_total * 0.10;
    SET v_taxe_sejour = v_nb_nuits * p_nb_adultes * 1.50;
    
    RETURN v_prix_sous_total + COALESCE(v_frais_menage, 0) + v_frais_service + v_taxe_sejour;
END//

-- Fonction: Vérifier disponibilité d'un logement
CREATE FUNCTION fn_est_disponible(
    p_logement_id BIGINT,
    p_date_debut DATE,
    p_date_fin DATE
) RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE v_conflit INT DEFAULT 0;
    DECLARE v_statut VARCHAR(20);
    
    SELECT statut INTO v_statut FROM logement WHERE id = p_logement_id;
    
    IF v_statut != 'DISPONIBLE' THEN
        RETURN FALSE;
    END IF;
    
    SELECT COUNT(*) INTO v_conflit 
    FROM reservation 
    WHERE logement_id = p_logement_id 
    AND statut IN ('CONFIRMEE', 'EN_COURS')
    AND ((p_date_debut BETWEEN date_debut AND DATE_SUB(date_fin, INTERVAL 1 DAY)) 
         OR (p_date_fin BETWEEN DATE_ADD(date_debut, INTERVAL 1 DAY) AND date_fin)
         OR (date_debut BETWEEN p_date_debut AND p_date_fin));
    
    RETURN v_conflit = 0;
END//

DELIMITER ;


-- ============================================================================
-- SECTION 11: GESTION DES DROITS
-- ============================================================================

-- Suppression des utilisateurs s'ils existent
DROP USER IF EXISTS 'hebergo_admin'@'localhost';
DROP USER IF EXISTS 'hebergo_app'@'localhost';
DROP USER IF EXISTS 'hebergo_readonly'@'localhost';

-- Création des utilisateurs MySQL
CREATE USER 'hebergo_admin'@'localhost' IDENTIFIED BY 'Admin@Hebergo2024!';
CREATE USER 'hebergo_app'@'localhost' IDENTIFIED BY 'App@Hebergo2024!';
CREATE USER 'hebergo_readonly'@'localhost' IDENTIFIED BY 'Read@Hebergo2024!';

-- Droits administrateur (tous les droits)
GRANT ALL PRIVILEGES ON hebergo.* TO 'hebergo_admin'@'localhost' WITH GRANT OPTION;

-- Droits application (CRUD + procédures)
GRANT SELECT, INSERT, UPDATE, DELETE ON hebergo.* TO 'hebergo_app'@'localhost';
GRANT EXECUTE ON hebergo.* TO 'hebergo_app'@'localhost';

-- Droits lecture seule (reporting)
GRANT SELECT ON hebergo.* TO 'hebergo_readonly'@'localhost';

FLUSH PRIVILEGES;


-- ============================================================================
-- SECTION 12: DONNÉES DE BASE ET DE TEST
-- ============================================================================

-- Rôles de base
INSERT INTO role (code, libelle, description) VALUES
('ADMIN', 'Administrateur', 'Administrateur de la plateforme'),
('HOTE', 'Hôte', 'Propriétaire de biens en location'),
('LOCATAIRE', 'Locataire', 'Personne qui loue des biens');

-- Types de biens
INSERT INTO type_logement (libelle, description, icone) VALUES
('Appartement', 'Logement dans un immeuble collectif', 'fa-building'),
('Maison', 'Maison individuelle', 'fa-home'),
('Studio', 'Petit logement d''une pièce', 'fa-door-open'),
('Villa', 'Grande maison avec jardin et/ou piscine', 'fa-umbrella-beach'),
('Chalet', 'Maison de montagne en bois', 'fa-mountain'),
('Loft', 'Grand espace ouvert style industriel', 'fa-warehouse'),
('Chambre', 'Chambre chez l''habitant', 'fa-bed');

-- Équipements
INSERT INTO equipement (nom, icone, categorie, ordre) VALUES
('WiFi', 'fa-wifi', 'ESSENTIEL', 1),
('Climatisation', 'fa-snowflake', 'CONFORT', 10),
('Chauffage', 'fa-fire', 'CONFORT', 11),
('Cuisine équipée', 'fa-utensils', 'CUISINE', 20),
('Lave-linge', 'fa-tshirt', 'CONFORT', 12),
('Sèche-linge', 'fa-wind', 'CONFORT', 13),
('Lave-vaisselle', 'fa-sink', 'CUISINE', 21),
('Réfrigérateur', 'fa-snowflake', 'CUISINE', 22),
('Four', 'fa-fire', 'CUISINE', 23),
('Micro-ondes', 'fa-clock', 'CUISINE', 24),
('Cafetière', 'fa-coffee', 'CUISINE', 25),
('Télévision', 'fa-tv', 'MULTIMEDIA', 30),
('Netflix', 'fa-film', 'MULTIMEDIA', 31),
('Parking gratuit', 'fa-car', 'EXTERIEUR', 40),
('Parking payant', 'fa-parking', 'EXTERIEUR', 41),
('Piscine', 'fa-swimming-pool', 'EXTERIEUR', 42),
('Jacuzzi', 'fa-hot-tub', 'EXTERIEUR', 43),
('Jardin', 'fa-tree', 'EXTERIEUR', 44),
('Balcon', 'fa-door-open', 'EXTERIEUR', 45),
('Terrasse', 'fa-umbrella-beach', 'EXTERIEUR', 46),
('Barbecue', 'fa-fire-alt', 'EXTERIEUR', 47),
('Sèche-cheveux', 'fa-wind', 'SALLE_BAIN', 50),
('Fer à repasser', 'fa-iron', 'CONFORT', 14),
('Coffre-fort', 'fa-lock', 'SECURITE', 60),
('Détecteur de fumée', 'fa-bell', 'SECURITE', 61),
('Extincteur', 'fa-fire-extinguisher', 'SECURITE', 62),
('Trousse de secours', 'fa-first-aid', 'SECURITE', 63),
('Lit bébé', 'fa-baby', 'FAMILLE', 70),
('Chaise haute', 'fa-chair', 'FAMILLE', 71),
('Accessible PMR', 'fa-wheelchair', 'ACCESSIBILITE', 80);

-- Paramètres système
INSERT INTO parametre (cle, valeur, description, type_valeur) VALUES
('commission_plateforme', '10', 'Commission de la plateforme en pourcentage', 'DECIMAL'),
('taxe_sejour_par_nuit', '1.50', 'Taxe de séjour par nuit par adulte', 'DECIMAL'),
('delai_annulation_defaut', '7', 'Délai d''annulation par défaut en jours', 'INT'),
('nb_photos_max_bien', '20', 'Nombre maximum de photos par bien', 'INT'),
('duree_session_minutes', '120', 'Durée de session en minutes', 'INT'),
('email_contact', 'contact@hebergo.fr', 'Email de contact', 'STRING'),
('maintenance_mode', 'false', 'Mode maintenance activé', 'BOOLEAN');

-- ============================================================================
-- DONNÉES DE TEST
-- ============================================================================

-- Création du super admin
INSERT INTO utilisateur (email, mot_de_passe, nom, prenom, telephone) VALUES
('admin@hebergo.fr', 'hashed_admin_password', 'Admin', 'Super', '0100000000');

INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (1, 1);

UPDATE admin_profil SET niveau = 'SUPER_ADMIN' WHERE utilisateur_id = 1;

-- Création des hôtes de test
INSERT INTO utilisateur (email, mot_de_passe, nom, prenom, telephone) VALUES
('jean.dupont@email.com', 'hashed_password', 'Dupont', 'Jean', '0601020304'),
('marie.martin@email.com', 'hashed_password', 'Martin', 'Marie', '0611223344'),
('pierre.bernard@email.com', 'hashed_password', 'Bernard', 'Pierre', '0622334455'),
('sophie.petit@email.com', 'hashed_password', 'Petit', 'Sophie', '0633445566');

-- Attribuer le rôle hôte
INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES 
(2, 2), (3, 2), (4, 2), (5, 2);

-- Compléter les profils hôtes
UPDATE hote_profil SET 
    description = 'Passionné de voyages, j''aime accueillir des visiteurs du monde entier.',
    adresse = '12 rue de la Paix', ville = 'Paris', code_postal = '75001', pays = 'France',
    verifie = TRUE, date_verification = NOW()
WHERE utilisateur_id = 2;

UPDATE hote_profil SET 
    description = 'Propriétaire de plusieurs biens à Lyon, je propose des logements confortables.',
    adresse = '45 avenue Victor Hugo', ville = 'Lyon', code_postal = '69002', pays = 'France',
    verifie = TRUE, date_verification = NOW()
WHERE utilisateur_id = 3;

UPDATE hote_profil SET 
    description = 'Amateur de bonne cuisine et de convivialité.',
    adresse = '8 place Bellecour', ville = 'Marseille', code_postal = '13001', pays = 'France',
    verifie = FALSE
WHERE utilisateur_id = 4;

UPDATE hote_profil SET 
    description = 'Je loue ma maison de vacances au cœur du vignoble bordelais.',
    adresse = '23 rue des Fleurs', ville = 'Bordeaux', code_postal = '33000', pays = 'France',
    verifie = TRUE, date_verification = NOW()
WHERE utilisateur_id = 5;

-- Création des locataires de test
INSERT INTO utilisateur (email, mot_de_passe, nom, prenom, telephone) VALUES
('emma.leclerc@email.com', 'hashed_password', 'Leclerc', 'Emma', '0655667788'),
('lucas.moreau@email.com', 'hashed_password', 'Moreau', 'Lucas', '0666778899'),
('lea.garcia@email.com', 'hashed_password', 'Garcia', 'Léa', '0677889900');

-- Attribuer le rôle locataire
INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES 
(6, 3), (7, 3), (8, 3);

-- Compléter les profils locataires
UPDATE locataire_profil SET 
    date_naissance = '1990-05-15', ville = 'Paris', pays = 'France', verifie = TRUE
WHERE utilisateur_id = 6;

UPDATE locataire_profil SET 
    date_naissance = '1985-08-22', ville = 'Lyon', pays = 'France', verifie = TRUE
WHERE utilisateur_id = 7;

UPDATE locataire_profil SET 
    date_naissance = '1992-12-03', ville = 'Marseille', pays = 'France', verifie = FALSE
WHERE utilisateur_id = 8;

-- Un utilisateur qui est HOTE ET LOCATAIRE
INSERT INTO utilisateur (email, mot_de_passe, nom, prenom, telephone) VALUES
('antoine.robert@email.com', 'hashed_password', 'Robert', 'Antoine', '0688990011');

INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES 
(9, 2), (9, 3); -- Hôte ET Locataire

UPDATE hote_profil SET 
    description = 'Appartements avec vue sur la Méditerranée.',
    ville = 'Nice', code_postal = '06000', pays = 'France', verifie = TRUE
WHERE utilisateur_id = 9;

UPDATE locataire_profil SET 
    date_naissance = '1988-03-18', ville = 'Nice', pays = 'France', verifie = TRUE
WHERE utilisateur_id = 9;

-- Biens de test
INSERT INTO logement (hote_id, type_logement_id, titre, description, adresse, ville, code_postal, prix_nuit, capacite, superficie, statut, date_publication) VALUES
(2, 1, 'Charmant appartement Marais', 'Superbe appartement au cœur du Marais, proche de toutes commodités.', '15 rue des Rosiers', 'Paris', '75004', 120.00, 4,  55.00, 'DISPONIBLE', NOW()),
(2, 3, 'Studio cosy Montmartre', 'Petit studio idéal pour un séjour romantique à Montmartre.', '8 rue Lepic', 'Paris', '75018', 75.00,  2, 25.00, 'DISPONIBLE', NOW()),
(3, 1, 'Appartement vue Rhône', 'Bel appartement avec vue imprenable sur le Rhône.', '12 quai Claude Bernard', 'Lyon', '69007', 95.00, 4, 70.00, 'DISPONIBLE', NOW()),
(4, 4, 'Villa provençale', 'Magnifique villa avec piscine en Provence.', '45 chemin des Oliviers', 'Aix-en-Provence', '13100', 250.00, 8, 180.00, 'DISPONIBLE', NOW()),
(5, 2, 'Maison vignoble bordelais', 'Charmante maison au milieu des vignes.', '3 route des Châteaux', 'Saint-Émilion', '33330', 180.00,  6,  120.00, 'DISPONIBLE', NOW()),
(9, 1, 'Appartement Promenade des Anglais', 'Vue mer exceptionnelle sur la Baie des Anges.', '100 Promenade des Anglais', 'Nice', '06000', 150.00, 4, 65.00, 'DISPONIBLE', NOW());

-- Équipements des biens
INSERT INTO logement_equipement (logement_id, equipement_id) VALUES
(1, 1), (1, 2), (1, 4), (1, 12), (1, 22), (1, 24), (1, 25),
(2, 1), (2, 3), (2, 4), (2, 12), (2, 22),
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 7), (3, 12), (3, 19),
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 7), (4, 12), (4, 14), (4, 16), (4, 18), (4, 20), (4, 21),
(5, 1), (5, 3), (5, 4), (5, 5), (5, 12), (5, 14), (5, 18), (5, 20), (5, 21),
(6, 1), (6, 2), (6, 4), (6, 12), (6, 19), (6, 22), (6, 24), (6, 25);


-- ============================================================================
-- FIN DU SCRIPT
-- ============================================================================

SELECT '========================================' AS '';
SELECT 'Base de données SQUAT R v2.0 créée !' AS Message;
SELECT '========================================' AS '';
SELECT 'Tables:', (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'hebergo' AND table_type = 'BASE TABLE') AS Nombre;
SELECT 'Vues:', (SELECT COUNT(*) FROM information_schema.views WHERE table_schema = 'hebergo') AS Nombre;
SELECT 'Procédures:', (SELECT COUNT(*) FROM information_schema.routines WHERE routine_schema = 'hebergo' AND routine_type = 'PROCEDURE') AS Nombre;
SELECT 'Fonctions:', (SELECT COUNT(*) FROM information_schema.routines WHERE routine_schema = 'hebergo' AND routine_type = 'FUNCTION') AS Nombre;
SELECT 'Triggers:', (SELECT COUNT(*) FROM information_schema.triggers WHERE trigger_schema = 'hebergo') AS Nombre;