package training.afpa.cda24060.squatrbnb.utilitaires;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Singleton_HikariCP {

    private static final Logger LOGGER = LoggerFactory.getLogger(Singleton_HikariCP.class);
    private static final String CHEMIN_CONF = "conf.properties";
    private static final HikariDataSource dataSource = initDataSource();

    private Singleton_HikariCP() {}


    private static HikariDataSource initDataSource() {

        LogUtils.info(LOGGER, "Initialisation du pool HikariCP...");

        Properties props = new Properties();

        try (InputStream is = Singleton_HikariCP.class
                .getClassLoader()
                .getResourceAsStream(CHEMIN_CONF)) {

            if (is == null) {
                LogUtils.error(LOGGER, "Le fichier {} est introuvable dans le classpath", CHEMIN_CONF);
                throw new RuntimeException("Fichier de configuration introuvable : " + CHEMIN_CONF);
            }

            props.load(is);
            LogUtils.info(LOGGER, "Configuration chargée depuis {} ", CHEMIN_CONF);

        } catch (IOException e) {
            LogUtils.error(LOGGER, "Erreur lors du chargement du fichier properties", e);
            throw new RuntimeException("Impossible de charger la configuration", e);
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("jdbc.url"));
            config.setUsername(props.getProperty("jdbc.login"));
            config.setPassword(props.getProperty("jdbc.password"));
            config.setDriverClassName(props.getProperty("jdbc.driver.class"));

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(60000);
            config.setConnectionTimeout(30000);
            config.setPoolName("AFPA-HikariPool");

            LogUtils.info(LOGGER, "HikariCP configuré avec succès. Création du pool...");

            HikariDataSource ds = new HikariDataSource(config);
            LogUtils.info(LOGGER, "Pool HikariCP initialisé avec succès.");

            return ds;

        } catch (Exception e) {
            LogUtils.error(LOGGER, "Erreur lors de l'initialisation du pool HikariCP", e);
            throw new RuntimeException("Erreur d'initialisation du pool HikariCP", e);
        }
    }

    public static Connection getInstanceDB() throws SQLException {
        try {
            Connection cn = dataSource.getConnection();
            LogUtils.debug(LOGGER, "Connexion obtenue depuis HikariCP.");
            return cn;
        } catch (SQLException e) {
            LogUtils.error(LOGGER, "Impossible d'obtenir une connexion depuis HikariCP", e);
            throw e;
        }
    }

    public static void closeInstanceDB() {
        if (dataSource != null && !dataSource.isClosed()) {
            LogUtils.info(LOGGER, "Fermeture du pool HikariCP...");
            dataSource.close();
            LogUtils.info(LOGGER, "Pool HikariCP fermé avec succès.");
        }
    }
}