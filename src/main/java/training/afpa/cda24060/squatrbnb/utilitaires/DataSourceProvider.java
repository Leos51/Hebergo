package training.afpa.cda24060.squatrbnb.utilitaires;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Accès direct au DataSource JNDI (pour requêtes SQL natives)
 * Utiliser HibernateUtil pour les opérations ORM
 */
public class DataSourceProvider {
    private static DataSource dataSource;

    static {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/SquatRbnBDS");
            System.out.println("✅ DataSource JNDI initialisé");
        } catch (Exception e) {
            System.err.println("❌ Erreur DataSource JNDI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource non initialisé");
        }
        return dataSource.getConnection();
    }
}
