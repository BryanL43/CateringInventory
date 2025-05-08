package org.fakeskymeal;

import java.sql.*;

// Env key loader
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.util.logging.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Load the database environment variables
        Properties props = new Properties();
        var envFile = Paths.get("./.env");
        try (var inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Environment File is not found.", e);
        }

        // Dereference the environment variables
        String DB_HOST = props.getProperty("DB_HOST");
        String DB_PORT = props.getProperty("DB_PORT");
        String DB_NAME = props.getProperty("DB_NAME");
        String DB_USERNAME = props.getProperty("DB_USERNAME");
        String DB_PASSWORD = props.getProperty("DB_PASSWORD");

        String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

        // Test connection [DELETE]
        try (Connection conn = DriverManager.getConnection(url, DB_USERNAME, DB_PASSWORD)) {
            System.out.println("Connected to the database!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM airline_companies;");

            while (rs.next()) {
                System.out.println("Company: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Connection failed:");
            LOGGER.log(Level.SEVERE, "Database query failed", e);
        }
    }
}