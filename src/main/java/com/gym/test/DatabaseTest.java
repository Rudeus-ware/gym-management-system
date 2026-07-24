package com.gym.test;

import com.gym.database.DatabaseConnection;
import com.gym.database.DatabaseManager;
import com.gym.model.Profile;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 TESTING DATABASE CONNECTION");
        System.out.println("=".repeat(60));
        
        // Test 1: Connection
        System.out.println("\n📁 Test 1: Database Connection");
        DatabaseConnection db = DatabaseConnection.getInstance();
        
        if (db.testConnection()) {
            System.out.println("✅ Connection successful!");
        } else {
            System.err.println("❌ Connection failed!");
            return;
        }
        
        // Test 2: DatabaseManager
        System.out.println("\n📁 Test 2: DatabaseManager");
        DatabaseManager dm = new DatabaseManager();
        
        // Test 2a: Find All Profiles
        System.out.println("\n📋 Existing Profiles:");
        for (Profile p : dm.findAllProfiles()) {
            System.out.println("   " + p.getProfileId() + ". " + p.getName() + 
                             " (" + p.getEmail() + ")");
        }
        
        // Test 2b: Create Profile
        System.out.println("\n📋 Creating Test Profile...");
        Profile newProfile = dm.createProfile(
            "Test User",
            "test@email.com",
            "555-000-0000",
            "123 Test Street"
        );
        
        if (newProfile != null) {
            System.out.println("✅ Created: " + newProfile.getName() + 
                             " (ID: " + newProfile.getProfileId() + ")");
        }
        
        // Test 2c: Find by ID
        if (newProfile != null) {
            Profile found = dm.findProfileById(newProfile.getProfileId());
            if (found != null) {
                System.out.println("✅ Found by ID: " + found.getName());
            }
        }
        
        System.out.println("\n✅ All tests completed!");
    }
}