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
    static Connection _myConnection;

    /**
     * Default Constructor
     */
    public JdbcConnection() {
        super();
    }

    public static Connection getConnection() {
        // get the default JDBC Connection
        if (_myConnection == null) { // if null, need to initialize
            // Load the database environment variables
            Properties props = new Properties();
            Path envFile = Paths.get(System.getProperty("user.dir"), ".env");

            try (var inputStream = Files.newInputStream(envFile)) {
                props.load(inputStream);
            } catch(Exception e) {
                LOGGER.log(Level.WARNING, "Environment File is not found.", e);
            }

            try {
                // Dereference the environment variables
                String DB_HOST = props.getProperty("DB_HOST");
                String DB_PORT = props.getProperty("DB_PORT");
                String DB_NAME = props.getProperty("DB_NAME");
                String DB_USERNAME = props.getProperty("DB_USERNAME");
                String DB_PASSWORD = props.getProperty("DB_PASSWORD");

                String sourceURL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

                // Establish connection
                _myConnection = DriverManager.getConnection(sourceURL, DB_USERNAME, DB_PASSWORD);
                System.out.println("Connected Connection");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Could not connect to the database.", e);
            }
        }

        return _myConnection;
    }

    public static void resetConnection() {
        System.out.println("Confirmed connection reset triggered");
        if (_myConnection != null) {
            try {
                _myConnection.close();
            } catch (SQLException se) {
                System.out.println("Error while closing connection: " + se);
            }
        }

        _myConnection = null;
    }

    public static void checkStatus() {
        System.out.println(_myConnection);
    }
}
