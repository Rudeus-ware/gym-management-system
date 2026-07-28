package com.gym.model.user;

import com.gym.model.Profile;

/**
 * User - Represents a user with login credentials
 * Extends Profile to inherit personal information
 */
public class User extends Profile {
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String userId;
    private String password;
    private boolean isActive;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    public User(String profileId, String name, String email, String phone, String address,
                String userId, String password) {
        super(profileId, name, email, phone, address);
        this.userId = userId;
        this.password = password;
        this.isActive = true;
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public boolean isActive() { return isActive; }
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    public void setUserId(String userId) { this.userId = userId; }
    public void setPassword(String password) { this.password = password; }
    public void setActive(boolean active) { isActive = active; }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public void resetPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("✅ Password reset successfully!");
    }
    
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }
    
    // ============================================================
    // OVERRIDE METHODS
    // ============================================================
    
    @Override
    public String viewProfile() {
        return super.viewProfile() +
               "\nUser ID: " + userId +
               "\nAccount Status: " + (isActive ? "Active" : "Inactive");
    }
    
    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", name='" + getName() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", isActive=" + isActive +
               '}';
    }
}