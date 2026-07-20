package com.gym.model.user;

import com.gym.model.Profile;

public class User extends Profile {
    private String userId;
    private String password;
    private boolean isActive;
    
    // Constructor
    public User(int profileId, String name, String email, String phone, String address, 
                String userId, String password) {
        super(profileId, name, email, phone, address); // Call Profile constructor
        this.userId = userId;
        this.password = password;
        this.isActive = true;
    }
    
    // GETTERS
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    public boolean isActive() { return isActive; }
    
    // SETTERS
    public void setUserId(String userId) { this.userId = userId; }
    public void setPassword(String password) { this.password = password; }
    public void setActive(boolean active) { isActive = active; }
    
    // USER-SPECIFIC METHODS
    public void resetPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("Password reset successfully!");
    }
    
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }
    
    // Override viewProfile to include user info
    @Override
    public String viewProfile() {
        return super.viewProfile() + 
               "\nUser ID: " + userId + 
               "\nAccount Status: " + (isActive ? "Active" : "Inactive");
    }
}