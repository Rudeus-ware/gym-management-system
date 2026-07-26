package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;
import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.util.IdGenerator;

import java.time.LocalDate;

public class RegisterController {
    
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;
    
    public RegisterController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
        // Fix: Check if connection is available
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("REG");
        }
    }
    
    public RegisterController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
        this.idGenerator = new IdGenerator("REG");
    }
    
    // ============================================================
    // REGISTRATION METHODS
    // ============================================================
    
    public Profile registerMember(String name, String email, String phone, String membershipType) {
        if (email == null || email.isEmpty()) {
            System.out.println("❌ Email is required");
            return null;
        }
        
        // Check if email already exists
        Profile existing = databaseManager.findProfileByEmail(email);
        if (existing != null) {
            System.out.println("❌ Email already registered: " + email);
            return null;
        }
        
        String profileId = idGenerator.generateProfileId(IdGenerator.ROLE_MEMBER, LocalDate.now());
        Profile profile = databaseManager.createProfile(profileId, name, email, phone, membershipType);
        
        // Create membership based on type
        if (membershipType != null) {
            createMembership(profileId, membershipType);
        }
        
        System.out.println("✅ Member registered successfully: " + name);
        return profile;
    }
    
    public Trainer registerTrainer(String name, String email, String phone, String specialization) {
        if (email == null || email.isEmpty()) {
            System.out.println("❌ Email is required");
            return null;
        }
        
        // Check if email already exists
        Profile existing = databaseManager.findProfileByEmail(email);
        if (existing != null) {
            System.out.println("❌ Email already registered: " + email);
            return null;
        }
        
        String trainerId = idGenerator.generateProfileId(IdGenerator.ROLE_TRAINER, LocalDate.now());
        String hireDate = LocalDate.now().toString();
        return databaseManager.createTrainer(trainerId, name, email, phone, specialization, hireDate, "ACTIVE");
    }
    
    public Admin registerAdmin(String name, String email, String password) {
        if (email == null || email.isEmpty()) {
            System.out.println("❌ Email is required");
            return null;
        }
        
        String adminId = "ADM" + System.currentTimeMillis();
        Admin admin = new Admin(adminId, name, email, password);
        databaseManager.addAdmin(admin);
        System.out.println("✅ Admin registered: " + name);
        return admin;
    }
    
    // ============================================================
    // MEMBERSHIP CREATION
    // ============================================================
    
    private void createMembership(String profileId, String type) {
        String membershipId = idGenerator.generateMembershipId(LocalDate.now());
        double price = getMembershipPrice(type);
        int duration = getMembershipDuration(type);
        databaseManager.createMembership(membershipId, profileId, type, price, duration);
    }
    
    private double getMembershipPrice(String type) {
        return switch (type.toLowerCase()) {
            case "basic" -> 29.99;
            case "premium" -> 59.99;
            case "family" -> 89.99;
            default -> 0.0;
        };
    }
    
    private int getMembershipDuration(String type) {
        return switch (type.toLowerCase()) {
            case "basic" -> 30;
            case "premium" -> 30;
            case "family" -> 30;
            default -> 0;
        };
    }
}