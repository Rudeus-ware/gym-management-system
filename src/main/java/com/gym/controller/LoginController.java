package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.model.user.User;
import com.gym.persistence.DataManager;

/**
 * LoginController - Business Logic for Authentication
 * 
 * This class handles all authentication-related business logic:
 * - User login validation
 * - Password management
 * - Account status checking
 * - User session management
 * 
 * Pure business logic - NO UI code!
 */
public class LoginController {
    
    private DataManager dataManager;
    private User currentUser;
    
    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    
    public LoginController(DataManager dataManager) {
        this.dataManager = dataManager;
        this.currentUser = null;
    }
    
    // ============================================================
    // AUTHENTICATION METHODS
    // ============================================================
    
    /**
     * Authenticate a user with email and password
     * 
     * @param email User's email address
     * @param password User's password
     * @return User object if authenticated, null otherwise
     */
    public User login(String email, String password) {
        // Validate input
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.out.println("❌ Login failed: Email and password required");
            return null;
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        String normalizedPassword = password.trim();
        
        // 1. Check against members (Profiles)
        User memberUser = authenticateMember(normalizedEmail, normalizedPassword);
        if (memberUser != null) {
            currentUser = memberUser;
            System.out.println("✅ Member login successful: " + memberUser.getName());
            return memberUser;
        }
        
        // 2. Check against trainers
        Trainer trainer = authenticateTrainer(normalizedEmail, normalizedPassword);
        if (trainer != null) {
            currentUser = trainer;
            System.out.println("✅ Trainer login successful: " + trainer.getName());
            return trainer;
        }
        
        // 3. Check against admins
        Admin admin = authenticateAdmin(normalizedEmail, normalizedPassword);
        if (admin != null) {
            currentUser = admin;
            System.out.println("✅ Admin login successful: " + admin.getName());
            return admin;
        }
        
        System.out.println("❌ Login failed: Invalid credentials for " + email);
        return null;
    }
    
    /**
     * Authenticate a member (Profile)
     */
    private User authenticateMember(String email, String password) {
        for (Profile profile : dataManager.getProfiles()) {
            if (profile.getEmail().equalsIgnoreCase(email)) {
                // Check if account is active
                if (!profile.isActive()) {
                    System.out.println("❌ Login blocked: Account inactive for " + email);
                    return null;
                }
                
                // Check membership validity
                if (profile.getMembership() != null && !profile.getMembership().isValid()) {
                    System.out.println("❌ Login blocked: Membership expired for " + email);
                    return null;
                }
                
                // Validate password
                if (isPasswordValid(profile, password)) {
                    // Convert Profile to User for compatibility
                    return new User(
                        profile.getProfileId(),
                        profile.getName(),
                        profile.getEmail(),
                        profile.getPhone(),
                        profile.getAddress(),
                        String.valueOf(profile.getProfileId()),
                        password
                    );
                }
            }
        }
        return null;
    }
    
    /**
     * Authenticate a trainer
     */
    private Trainer authenticateTrainer(String email, String password) {
        for (Trainer trainer : dataManager.getTrainers()) {
            if (trainer.getEmail().equalsIgnoreCase(email) && 
                trainer.getPassword().equals(password)) {
                return trainer;
            }
        }
        return null;
    }
    
    /**
     * Authenticate an admin
     */
    private Admin authenticateAdmin(String email, String password) {
        for (Admin admin : dataManager.getAdmins()) {
            if (admin.getEmail().equalsIgnoreCase(email) && 
                admin.getPassword().equals(password)) {
                return admin;
            }
        }
        return null;
    }
    
    // ============================================================
    // PASSWORD MANAGEMENT
    // ============================================================
    
    /**
     * Validate password for a profile
     * In production, this would use hashed password comparison
     */
    private boolean isPasswordValid(Profile profile, String password) {
        // For demo: password = "password" or name lowercase
        // In production: use BCrypt or similar
        return password.equals("password") || 
               password.equals(profile.getName().toLowerCase());
    }
    
    /**
     * Reset password for a user
     * 
     * @param email User's email
     * @param newPassword New password
     * @return true if password reset was successful
     */
    public boolean resetPassword(String email, String newPassword) {
        if (email == null || email.trim().isEmpty() || 
            newPassword == null || newPassword.length() < 6) {
            System.out.println("❌ Password reset failed: Invalid input");
            return false;
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        
        // Check members
        for (Profile profile : dataManager.getProfiles()) {
            if (profile.getEmail().equalsIgnoreCase(normalizedEmail)) {
                // In production: hash the password before storing
                // profile.setPassword(newPassword);
                System.out.println("✅ Password reset for member: " + profile.getName());
                dataManager.saveAllData();
                return true;
            }
        }
        
        // Check trainers
        for (Trainer trainer : dataManager.getTrainers()) {
            if (trainer.getEmail().equalsIgnoreCase(normalizedEmail)) {
                trainer.setPassword(newPassword);
                System.out.println("✅ Password reset for trainer: " + trainer.getName());
                dataManager.saveAllData();
                return true;
            }
        }
        
        // Check admins
        for (Admin admin : dataManager.getAdmins()) {
            if (admin.getEmail().equalsIgnoreCase(normalizedEmail)) {
                admin.setPassword(newPassword);
                System.out.println("✅ Password reset for admin: " + admin.getName());
                dataManager.saveAllData();
                return true;
            }
        }
        
        System.out.println("❌ Password reset failed: User not found for " + email);
        return false;
    }
    
    /**
     * Validate password strength
     * 
     * @param password Password to validate
     * @return true if password meets requirements
     */
    public boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Additional checks in production:
        // - Contains uppercase
        // - Contains lowercase
        // - Contains number
        // - Contains special character
        return true;
    }
    
    // ============================================================
    // SESSION MANAGEMENT
    // ============================================================
    
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
     * Check if the current user is a Member (Profile)
     */
    public boolean isMember() {
        return currentUser != null && 
               !(currentUser instanceof Admin) && 
               !(currentUser instanceof Trainer);
    }
    
    /**
     * Get the current user's role
     */
    public String getCurrentUserRole() {
        if (currentUser == null) {
            return "Not Logged In";
        }
        if (currentUser instanceof Admin) {
            return "Admin";
        }
        if (currentUser instanceof Trainer) {
            return "Trainer";
        }
        return "Member";
    }
    
    // ============================================================
    // USER LOOKUP METHODS
    // ============================================================
    
    /**
     * Find a user by email
     */
    public User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        
        String normalizedEmail = email.trim().toLowerCase();
        
        // Check members
        for (Profile profile : dataManager.getProfiles()) {
            if (profile.getEmail().equalsIgnoreCase(normalizedEmail)) {
                return new User(
                    profile.getProfileId(),
                    profile.getName(),
                    profile.getEmail(),
                    profile.getPhone(),
                    profile.getAddress(),
                    String.valueOf(profile.getProfileId()),
                    "*****"  // Password masked
                );
            }
        }
        
        // Check trainers
        for (Trainer trainer : dataManager.getTrainers()) {
            if (trainer.getEmail().equalsIgnoreCase(normalizedEmail)) {
                return trainer;
            }
        }
        
        // Check admins
        for (Admin admin : dataManager.getAdmins()) {
            if (admin.getEmail().equalsIgnoreCase(normalizedEmail)) {
                return admin;
            }
        }
        
        return null;
    }
    
    /**
     * Check if a user exists with the given email
     */
    public boolean userExists(String email) {
        return findUserByEmail(email) != null;
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get login statistics
     */
    public LoginStats getLoginStats() {
        int totalMembers = dataManager.getProfiles().size();
        int totalTrainers = dataManager.getTrainers().size();
        int totalAdmins = dataManager.getAdmins().size();
        boolean isLoggedIn = this.isLoggedIn();
        String currentUserInfo = isLoggedIn ? currentUser.getName() : "None";
        String currentUserRole = getCurrentUserRole();
        
        return new LoginStats(
            totalMembers,
            totalTrainers,
            totalAdmins,
            isLoggedIn,
            currentUserInfo,
            currentUserRole
        );
    }
    
    // ============================================================
    // INNER CLASS: LoginStats
    // ============================================================
    
    /**
     * Data Transfer Object for login statistics
     */
    public static class LoginStats {
        public final int totalMembers;
        public final int totalTrainers;
        public final int totalAdmins;
        public final boolean isLoggedIn;
        public final String currentUser;
        public final String currentUserRole;
        
        public LoginStats(int members, int trainers, int admins,
                         boolean loggedIn, String user, String role) {
            this.totalMembers = members;
            this.totalTrainers = trainers;
            this.totalAdmins = admins;
            this.isLoggedIn = loggedIn;
            this.currentUser = user;
            this.currentUserRole = role;
        }
        
        @Override
        public String toString() {
            return String.format(
                "📊 LOGIN STATISTICS\n" +
                "===================\n" +
                "Total Members: %d\n" +
                "Total Trainers: %d\n" +
                "Total Admins: %d\n" +
                "Logged In: %s\n" +
                "Current User: %s\n" +
                "User Role: %s",
                totalMembers, totalTrainers, totalAdmins,
                isLoggedIn ? "Yes ✅" : "No ❌",
                currentUser,
                currentUserRole
            );
        }
    }
}