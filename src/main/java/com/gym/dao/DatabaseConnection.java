package com.gym.dao;

public class DatabaseConnection {
    public static String getConnectionUrl() {
        return "jdbc:h2:mem:gymdb";
    }
}
