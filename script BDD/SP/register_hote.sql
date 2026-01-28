-- Procédure: Inscription d'un hôte
DELIMITER //
CREATE PROCEDURE sp_inscrire_hote(
    IN p_nom VARCHAR(100),
    IN p_prenom VARCHAR(100),
    IN p_email VARCHAR(255),
    IN p_mot_de_passe VARCHAR(255),
    IN p_telephone VARCHAR(20),
    IN p_adresse VARCHAR(255),
    IN p_ville VARCHAR(100),
    IN p_code_postal VARCHAR(10),
    IN p_pays VARCHAR(100),
    IN p_description TEXT,
    OUT p_hote_id INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE email_existe INT DEFAULT 0;
    
    -- Vérifier si l'email existe déjà
    SELECT COUNT(*) INTO email_existe FROM hote WHERE email = p_email;
    
    IF email_existe > 0 THEN
        SET p_hote_id = NULL;
        SET p_message = 'Cet email est déjà utilisé';
    ELSE
        INSERT INTO hote (nom, prenom, email, mot_de_passe, telephone, adresse, ville, code_postal, pays, description)
        VALUES (p_nom, p_prenom, p_email, p_mot_de_passe, p_telephone, p_adresse, p_ville, p_code_postal, IFNULL(p_pays, 'France'), p_description);
        
        SET p_hote_id = LAST_INSERT_ID();
        SET p_message = 'Inscription réussie';
    END IF;
END//
DELIMITER ;