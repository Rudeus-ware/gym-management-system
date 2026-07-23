package com.gym.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection - Singleton class for database connectivity
 * Handles connection pooling and configuration
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private Properties props;
    
    private static final String PROPERTIES_FILE = "application.properties";
    
    private DatabaseConnection() {
        loadProperties();
        connect();
    }
    
    /**
     * Load database properties from configuration file
     */
    private void loadProperties() {
        props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                // Fallback to default values
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/gym_db");
                props.setProperty("db.user", "root");
                props.setProperty("db.password", "");
                props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                System.out.println("⚠️ " + PROPERTIES_FILE + " not found. Using default values.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading properties: " + e.getMessage());
            // Set defaults
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/gym_db");
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "");
            props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }
    }
    
    /**
     * Establish database connection
     */
    private void connect() {
        try {
            String driver = props.getProperty("db.driver");
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database connected successfully!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Database driver not found: " + e.getMessage());
            System.out.println("   Make sure MySQL connector is in pom.xml");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            System.out.println("   Check that MySQL is running and credentials are correct.");
        }
    }
    
    /**
     * Get singleton instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection check failed: " + e.getMessage());
            connect();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Test connection
     */
    public boolean testConnection() {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
