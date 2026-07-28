package com.gym.controller;

import com.gym.database.DatabaseManager;
import com.gym.model.Profile;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.model.user.User;
import com.gym.persistence.JsonDataManager;

public class LoginController {
    
    private final DatabaseManager databaseManager;
    private User currentUser;
    
    public LoginController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
        this.currentUser = null;
    }
    
    public LoginController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
        this.currentUser = null;
    }
    
    // ============================================================
    // AUTHENTICATION METHODS
    // ============================================================
    
    public User authenticateMember(String email, String password) {
        Profile profile = databaseManager.findProfileByEmail(email);
        if (profile != null) {
            if (password != null && (password.equals("password") || password.equals(email))) {
                User user = new User(profile.getProfileId(), profile.getName(), email, "", "", email, password);
                this.currentUser = user;
                System.out.println("✅ Member authenticated: " + profile.getName());
                return user;
            }
        }
        System.out.println("❌ Authentication failed for member: " + email);
        return null;
    }
    
    public Trainer authenticateTrainer(String email, String password) {
        for (Trainer trainer : databaseManager.findAllTrainers()) {
            if (trainer.getEmail().equals(email)) {
                if (password != null && (password.equals("password") || password.equals(email))) {
                    System.out.println("✅ Trainer authenticated: " + trainer.getName());
                    return trainer;
                }
            }
        }
        System.out.println("❌ Authentication failed for trainer: " + email);
        return null;
    }
    
    public Admin authenticateAdmin(String email, String password) {
        for (Admin admin : databaseManager.getAdmins()) {
            if (admin.getEmail().equals(email)) {
                if (password != null && (password.equals("password") || password.equals(email))) {
                    System.out.println("✅ Admin authenticated: " + admin.getName());
                    return admin;
                }
            }
        }
        System.out.println("❌ Authentication failed for admin: " + email);
        return null;
    }
    
    public User login(String email, String password, String role) {
        if (email == null || password == null || role == null) return null;
        
        switch (role.toLowerCase()) {
            case "member" -> {
                return authenticateMember(email, password);
            }
            case "trainer" -> {
                Trainer trainer = authenticateTrainer(email, password);
                if (trainer != null) {
                    this.currentUser = new User(trainer.getTrainerId(), trainer.getName(), email, "", "", email, "password");
                    return this.currentUser;
                }
                return null;
            }
            case "admin" -> {
                Admin admin = authenticateAdmin(email, password);
                if (admin != null) {
                    this.currentUser = new User(admin.getAdminId(), admin.getName(), email, "", "", email, "password");
                    return this.currentUser;
                }
                return null;
            }
            default -> {
                System.out.println("❌ Unknown role: " + role);
                return null;
            }
        }
    }
    
    // ============================================================
    // SESSION MANAGEMENT
    // ============================================================
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public void logout() {
        if (currentUser != null) {
            System.out.println("👋 Logged out: " + currentUser.getName());
        }
        this.currentUser = null;
    }
}