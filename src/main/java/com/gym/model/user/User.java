package com.gym.model.user;

import com.gym.model.Profile;

/**
 * User - Represents a user account with login credentials
 * Extends Profile to inherit personal information
 */
public class User extends Profile {
    private String userId;
    private String password;
    // isActive is inherited from Profile - DO NOT redeclare!
    
    // Constructor
    public User(int profileId, String name, String email, String phone, String address,
                String userId, String password) {
        super(profileId, name, email, phone, address);
        this.userId = userId;
        this.password = password;
        // isActive is already initialized in Profile constructor
    }
    
    // ===== GETTERS =====
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    
    // ===== SETTERS =====
    public void setUserId(String userId) { this.userId = userId; }
    public void setPassword(String password) { this.password = password; }
    
    // ===== BUSINESS METHODS =====
    public void resetPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("✅ Password reset successfully!");
    }
    
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }
    
    // ===== OVERRIDE METHODS =====
    @Override
    public String viewProfile() {
        return super.viewProfile() + 
               "\nUser ID: " + userId;
    }
    
    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", name='" + getName() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", isActive=" + isActive() +
               '}';
    }
}
