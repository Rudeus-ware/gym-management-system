package com.gym.model.user;

import com.gym.model.Profile;

/**
 * User - Represents a user account with login credentials
 * Extends Profile to inherit personal information
 */
public class User extends Profile {
    private String userId;
    private String password;
    private String role;
    private String trainerId; 
    // isActive is inherited from Profile - DO NOT redeclare!
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Full constructor with all fields
     */
    public User(String profileId, String name, String email, String phone, String address,
                String userId, String password, String role) {
        super(profileId, name, email, phone, address);
        this.userId = userId;
        this.password = password;
        this.role = role;
        // isActive is already initialized in Profile constructor
    }
    
    /**
     * Constructor without phone and address
     */
    public User(String profileId, String name, String email, String userId, String password, String role) {
        super(profileId, name, email, null, null);
        this.userId = userId;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Constructor with minimal fields (for quick creation)
     */
    public User(String userId, String name, String email, String password, String role) {
        super(generateProfileId(), name, email, null, null);
        this.userId = userId;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Constructor for basic user (default role)
     */
    public User(String userId, String name, String email, String password) {
        this(userId, name, email, password, "MEMBER");
    }
    
    // ===== HELPER METHOD =====
    private static String generateProfileId() {
        return "PROF_" + System.currentTimeMillis();
    }
    
    // ===== GETTERS =====
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getTrainerId() { return trainerId; }
    
    // ===== SETTERS =====
    public void setUserId(String userId) { this.userId = userId; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }
    
    // ===== BUSINESS METHODS =====
    
    /**
     * Reset user password
     */
    public void resetPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("✅ Password reset successfully for user: " + userId);
    }
    
    /**
     * Validate login credentials
     */
    public boolean login(String enteredPassword) {
        return this.password != null && this.password.equals(enteredPassword);
    }
    
    /**
     * Check if user has admin privileges
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user is a trainer
     */
    public boolean isTrainer() {
        return "TRAINER".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user is a member
     */
    public boolean isMember() {
        return "MEMBER".equalsIgnoreCase(role);
    }
    
    /**
     * Deactivate user account
     */
    public void deactivate() {
        setActive(false);
        System.out.println("⚠️ User account " + userId + " has been deactivated.");
    }
    
    /**
     * Activate user account
     */
    public void activate() {
        setActive(true);
        System.out.println("✅ User account " + userId + " has been activated.");
    }
    
    // ===== VIEW PROFILE METHOD (NO @Override) =====
    
    /**
     * Display user profile information
     * This is a method specific to User class, not overriding anything
     */
    public String viewProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== USER PROFILE ===\n");
        sb.append("Profile ID: ").append(getProfileId()).append("\n");
        sb.append("User ID: ").append(userId).append("\n");
        sb.append("Name: ").append(getName()).append("\n");
        sb.append("Email: ").append(getEmail()).append("\n");
        if (getPhone() != null) {
            sb.append("Phone: ").append(getPhone()).append("\n");
        }
        if (getAddress() != null) {
            sb.append("Address: ").append(getAddress()).append("\n");
        }
        sb.append("Role: ").append(role).append("\n");
        sb.append("Status: ").append(isActive() ? "ACTIVE" : "INACTIVE").append("\n");
        if (trainerId != null) {
            sb.append("Trainer ID: ").append(trainerId).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Get a summary of user profile (for display purposes)
     */
    public String getProfileSummary() {
        return String.format("%s (%s) - %s [%s]", 
            getName(), userId, email, isActive() ? "Active" : "Inactive");
    }
    
    // ===== OVERRIDE METHODS =====
    
    @Override
    public String toString() {
        return String.format("User{userId='%s', name='%s', email='%s', role='%s', active=%s}",
            userId, getName(), getEmail(), role, isActive());
    }
}