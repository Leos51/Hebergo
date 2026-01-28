
-- Procédure: Authentification d'un hôte
DELIMITER |
CREATE PROCEDURE sp_authentifier_hote(
    IN p_email VARCHAR(255),
    IN p_mot_de_passe VARCHAR(255),
    OUT p_hote_id BIGINT,
    OUT p_resultat VARCHAR(50)
)
BEGIN
    DECLARE v_id BIGINT;
    DECLARE v_actif BOOLEAN;
    
    SELECT id, actif INTO v_id, v_actif 
    FROM hote 
    WHERE email = p_email AND mot_de_passe = p_mot_de_passe;
    
    IF v_id IS NULL THEN
        SET p_hote_id = NULL;
        SET p_resultat = 'IDENTIFIANTS_INCORRECTS';
    ELSEIF v_actif = FALSE THEN
        SET p_hote_id = NULL;
        SET p_resultat = 'COMPTE_DESACTIVE';
    ELSE
        SET p_hote_id = v_id;
        SET p_resultat = 'SUCCES';
        
        -- Mettre à jour la date de dernière connexion
        UPDATE hote SET date_derniere_connexion = NOW() WHERE id = v_id;
    END IF;
END|
DELIMITER ;