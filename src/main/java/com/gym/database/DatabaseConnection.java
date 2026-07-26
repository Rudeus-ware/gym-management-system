package com.gym.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection - Singleton database connection manager
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
    
    private void loadProperties() {
        props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                System.out.println("⚠️ " + PROPERTIES_FILE + " not found. Using defaults.");
                setDefaults();
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading properties: " + e.getMessage());
            setDefaults();
        }
    }
    
    private void setDefaults() {
        props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        props.setProperty("db.url", "jdbc:mysql://localhost:3306/gym_db?useSSL=false&serverTimezone=UTC");
        props.setProperty("db.username", "root");
        props.setProperty("db.password", "");
    }
    
    private void connect() {
        try {
            Class.forName(props.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
            );
            System.out.println("✅ Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
    
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
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                reconnect();
            }
        } catch (SQLException e) {
            reconnect();
        }
        return connection;
    }
    
    private void reconnect() {
        try {
            Class.forName(props.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
            );
            System.out.println("✅ Reconnected successfully!");
        } catch (Exception e) {
            System.err.println("❌ Reconnection failed!");
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection.");
        }
    }
    
    public boolean testConnection() {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}