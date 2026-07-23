package com.gym.test;

import com.gym.database.DatabaseConnection;
import com.gym.database.ProfileDatabaseManager;
import com.gym.model.Profile;

import java.util.List;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 TESTING DATABASE CONNECTION");
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Test 1: Database Connection
        System.out.println("📁 Test 1: Database Connection");
        System.out.println("-".repeat(40));
        
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (db.testConnection()) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
            System.out.println("   Check your database server and credentials.");
            return;
        }
        System.out.println();
        
        // Test 2: Profile Database Manager
        System.out.println("📁 Test 2: Profile Database Manager");
        System.out.println("-".repeat(40));
        
        ProfileDatabaseManager profileManager = new ProfileDatabaseManager();
        
        // Test 2a: Create Profile
        System.out.println("   Test 2a: Create Profile");
        Profile newProfile = profileManager.createProfile(
            "Test User",
            "test@email.com",
            "555-000-0000",
            "123 Test Street"
        );
        
        if (newProfile != null) {
            System.out.println("   ✅ Profile created:");
            System.out.println("      ID: " + newProfile.getProfileId());
            System.out.println("      Name: " + newProfile.getName());
            System.out.println("      Email: " + newProfile.getEmail());
        }
        System.out.println();
        
        // Test 2b: Find Profile by ID
        System.out.println("   Test 2b: Find Profile by ID");
        if (newProfile != null) {
            Profile found = profileManager.findProfileById(newProfile.getProfileId());
            if (found != null) {
                System.out.println("   ✅ Profile found:");
                System.out.println("      Name: " + found.getName());
                System.out.println("      Email: " + found.getEmail());
            }
        }
        System.out.println();
        
        // Test 2c: List All Profiles
        System.out.println("   Test 2c: List All Profiles");
        List<Profile> profiles = profileManager.findAllProfiles();
        System.out.println("   ✅ Found " + profiles.size() + " profiles:");
        for (Profile p : profiles) {
            System.out.println("      " + p.getProfileId() + ". " + p.getName() + 
                             " (" + p.getEmail() + ")");
        }
        System.out.println();
        
        // Test 2d: Statistics
        System.out.println("   Test 2d: Statistics");
        ProfileDatabaseManager.ProfileStats stats = profileManager.getStatistics();
        System.out.println(stats.toString());
        System.out.println();
        
        System.out.println("=".repeat(60));
        System.out.println("✅ All tests completed!");
        System.out.println("=".repeat(60));
    }
}
