package util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Env key loader
import java.nio.file.*;
import java.util.*;

import java.util.logging.*;

/**
 * JdbcConnection
 *
 * Helper class to get JDBC connection
 *
 */
public class JdbcConnection {
    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    private static final Properties props = new Properties();

    static {
        Path envFile = Paths.get(System.getProperty("user.dir"), ".env");

        try (var inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Environment File is not found.", e);
            throw new RuntimeException(e);
        }
    }

    public static Connection createConnection() throws SQLException {
        String sourceURL = "jdbc:mysql://"
                + props.getProperty("DB_HOST")
                + ":" + props.getProperty("DB_PORT")
                + "/" + props.getProperty("DB_NAME");

        return DriverManager.getConnection(sourceURL,
                props.getProperty("DB_USERNAME"),
                props.getProperty("DB_PASSWORD"));
    }
}
