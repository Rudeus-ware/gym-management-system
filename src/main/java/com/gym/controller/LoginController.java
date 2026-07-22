package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.model.user.User;
import com.gym.persistence.DataManager;

/**
 * LoginController - Handles authentication and login operations
 * Pure business logic - NO UI code
 */
public class LoginController {
    
    private DataManager dataManager;
    private User currentUser;
    
    public LoginController(DataManager dataManager) {
        this.dataManager = dataManager;
        this.currentUser = null;
    }
    
    /**
     * Authenticate a user with email and password
     * @param email User's email
     * @param password User's password
     * @return User object if authenticated, null otherwise
     */
    public User login(String email, String password) {
        // Check if user exists in profiles (as a User)
        for (Profile profile : dataManager.getProfiles()) {
            if (profile instanceof User) {
                User user = (User) profile;
                if (user.getEmail().equalsIgnoreCase(email) && 
                    user.getPassword().equals(password)) {
                    currentUser = user;
                    System.out.println("✅ Login successful: " + user.getName());
                    return user;
                }
            }
        }
        
        // Check if user is a Trainer
        for (Trainer trainer : dataManager.getTrainers()) {
            if (trainer.getEmail().equalsIgnoreCase(email) && 
                trainer.getPassword().equals(password)) {
                currentUser = trainer;
                System.out.println("✅ Trainer login successful: " + trainer.getName());
                return trainer;
            }
        }
        
        // Check if user is an Admin (if stored separately)
        // For now, we'll check if there's an admin with matching credentials
        // You can add admin to your data manager if needed
        
        System.out.println("❌ Login failed: Invalid email or password");
        return null;
    }
    
    /**
     * Authenticate a trainer with email and password
     */
    public Trainer loginTrainer(String email, String password) {
        for (Trainer trainer : dataManager.getTrainers()) {
            if (trainer.getEmail().equalsIgnoreCase(email) && 
                trainer.getPassword().equals(password)) {
                currentUser = trainer;
                System.out.println("✅ Trainer login successful: " + trainer.getName());
                return trainer;
            }
        }
        System.out.println("❌ Trainer login failed: Invalid credentials");
        return null;
    }
    
    /**
     * Logout the current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("👋 Logout successful: " + currentUser.getName());
            currentUser = null;
        } else {
            System.out.println("⚠️ No user is currently logged in");
        }
    }
    
    /**
     * Get the currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if the current user is an Admin
     */
    public boolean isAdmin() {
        return currentUser instanceof Admin;
    }
    
    /**
     * Check if the current user is a Trainer
     */
    public boolean isTrainer() {
        return currentUser instanceof Trainer;
    }
    
    /**
     * Reset password for a user
     * @param email User's email
     * @param newPassword New password
     * @return true if password reset was successful
     */
    public boolean resetPassword(String email, String newPassword) {
        // Check profiles
        for (Profile profile : dataManager.getProfiles()) {
            if (profile instanceof User) {
                User user = (User) profile;
                if (user.getEmail().equalsIgnoreCase(email)) {
                    user.setPassword(newPassword);
                    dataManager.saveAllData();
                    System.out.println("✅ Password reset successful for: " + user.getName());
                    return true;
                }
            }
        }
        
        // Check trainers
        for (Trainer trainer : dataManager.getTrainers()) {
            if (trainer.getEmail().equalsIgnoreCase(email)) {
                trainer.setPassword(newPassword);
                dataManager.saveAllData();
                System.out.println("✅ Password reset successful for trainer: " + trainer.getName());
                return true;
            }
        }
        
        System.out.println("❌ Password reset failed: Email not found");
        return false;
    }
    
    /**
     * Validate password strength
     */
    public boolean validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return true;
    }
    
    /**
     * Get login statistics
     */
    public String getLoginStats() {
        int totalUsers = dataManager.getProfiles().size();
        int totalTrainers = dataManager.getTrainers().size();
        boolean isLoggedIn = this.isLoggedIn();
        String currentUserInfo = isLoggedIn ? currentUser.getName() : "None";
        
        return String.format(
            "📊 LOGIN STATISTICS\n" +
            "===================\n" +
            "Total Users: %d\n" +
            "Total Trainers: %d\n" +
            "Logged In: %s\n" +
            "Current User: %s",
            totalUsers, totalTrainers,
            isLoggedIn ? "Yes ✅" : "No ❌",
            currentUserInfo
        );
    }
}