package com.gym.test;

import com.gym.database.DatabaseManager;
import com.gym.model.Profile;

import java.util.List;

public class DatabaseIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("🔍 DATABASE INTEGRATION TEST");
        System.out.println("=".repeat(50));
        
        DatabaseManager db = new DatabaseManager();
        
        // Test 1: Find all profiles
        System.out.println("\n📁 Test 1: Find All Profiles");
        List<Profile> profiles = db.findAllProfiles();
        System.out.println("   Found " + profiles.size() + " profiles");
        for (Profile p : profiles) {
            System.out.println("   - " + p.getProfileId() + ": " + p.getName());
        }
        
        // Test 2: Find profile by ID
        System.out.println("\n📁 Test 2: Find Profile by ID");
        if (!profiles.isEmpty()) {
            Profile p = db.findProfileById(profiles.get(0).getProfileId());
            System.out.println("   Found: " + p.getName() + " (" + p.getEmail() + ")");
        }
        
        // Test 3: Create profile
        System.out.println("\n📁 Test 3: Create Profile");
        String testId = "TEST" + System.currentTimeMillis();
        Profile testProfile = new Profile(
            testId,
            "Test User",
            "test@email.com",
            "555-000-0000",
            "123 Test St"
        );
        testProfile.setActive(true);
        db.createProfile(testProfile);
        System.out.println("   Created profile: " + testProfile.getName());
        
        // Test 4: Verify creation
        System.out.println("\n📁 Test 4: Verify Creation");
        Profile found = db.findProfileById(testId);
        if (found != null) {
            System.out.println("   ✅ Verified: " + found.getName());
        } else {
            System.out.println("   ❌ Verification failed!");
        }
        
        System.out.println("\n✅ All tests completed!");
    }
}