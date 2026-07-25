package com.gym.test;

import com.gym.database.DatabaseConnection;
import com.gym.database.DatabaseManager;
import com.gym.model.Profile;
import com.gym.util.IdGenerator;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 TESTING DATABASE WITH CUSTOM IDs");
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
        
        // Test 2: ID Generator
        System.out.println("\n📁 Test 2: ID Generator");
        IdGenerator idGen = new IdGenerator(db.getConnection());
        
        // Test 2a: Generate Admin ID
        System.out.println("\n   Generating Admin ID...");
        String adminId = idGen.generateProfileId("00", java.time.LocalDate.now());
        System.out.println("   Admin ID: " + adminId);
        
        // Test 2b: Generate Trainer ID
        System.out.println("\n   Generating Trainer ID...");
        String trainerId = idGen.generateProfileId("11", java.time.LocalDate.now());
        System.out.println("   Trainer ID: " + trainerId);
        
        // Test 2c: Generate Member ID
        System.out.println("\n   Generating Member ID...");
        String memberId = idGen.generateProfileId("22", java.time.LocalDate.now());
        System.out.println("   Member ID: " + memberId);
        
        // Test 3: DatabaseManager
        System.out.println("\n📁 Test 3: DatabaseManager");
        DatabaseManager dm = new DatabaseManager();
        
        // Test 3a: Create Profile
        System.out.println("\n   Creating Member Profile...");
        Profile newProfile = dm.createProfile(
            "Test User",
            "test@email.com",
            "555-000-0000",
            "123 Test Street",
            "22"  // Member role
        );
        
        if (newProfile != null) {
            System.out.println("   ✅ Created: " + newProfile.getName() + 
                             " (ID: " + newProfile.getProfileId() + ")");
        }
        
        // Test 3b: Find All Profiles
        System.out.println("\n   📋 All Profiles:");
        for (Profile p : dm.findAllProfiles()) {
            System.out.println("      " + p.getProfileId() + " | " + p.getName());
        }
        
        System.out.println("\n✅ All tests completed!");
    }
}