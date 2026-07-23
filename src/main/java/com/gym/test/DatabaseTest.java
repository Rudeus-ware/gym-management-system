package com.gym.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gym.database.DatabaseConnection;

public class DatabaseTest {
    public static void main(String[] args) {
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();
        
        try {
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            
            if (rs.next()) {
                System.out.println("✅ Database test successful!");
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Test failed: " + e.getMessage());
        }
    }
}