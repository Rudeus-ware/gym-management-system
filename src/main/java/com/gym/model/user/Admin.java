package com.gym.model.user;

/**
 * Admin - Represents a system administrator
 * Extends User to inherit profile information
 */
public class Admin extends User {
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String adminId;
    private String adminLevel;
    private String department;
    private String lastLogin;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Full constructor
     */
    public Admin(String profileId, String name, String email, String phone, String address,
                 String userId, String password, String adminLevel) {
        super(profileId, name, email, phone, address, userId, password);
        this.adminId = userId;
        this.adminLevel = adminLevel != null ? adminLevel : "Staff";
        this.department = "Administration";
        this.lastLogin = null;
    }
    
    /**
     * Simplified constructor
     */
    public Admin(String name, String email, String phone, String address,
                 String userId, String password, String adminLevel) {
        this("", name, email, phone, address, userId, password, adminLevel);
    }
    
    /**
     * Simple constructor for migration
     */
    public Admin(String profileId, String name, String email, String password) {
        this(profileId, name, email, "", "", "", password, "Staff");
    }

    public String getRole() {
        return adminLevel;
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public String getAdminId() { return adminId; }
    public String getAdminLevel() { return adminLevel; }
    public String getDepartment() { return department; }
    public String getLastLogin() { return lastLogin; }
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    public void setDepartment(String department) { this.department = department; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
    
    // ============================================================
    // ADMIN METHODS
    // ============================================================
    
    /**
     * Reset admin password
     */
    @Override
    public void resetPassword(String newPassword) {
        super.resetPassword(newPassword);
        System.out.println("🔑 Admin password reset for: " + getName());
    }
    
    /**
     * Create a new member
     */
    public void createMember(String name, String email, String phone, String address) {
        System.out.println("👤 Member created: " + name);
        System.out.println("   Email: " + email);
        System.out.println("   Phone: " + phone);
        System.out.println("   Address: " + address);
    }
    
    /**
     * Assign a trainer to a class
     */
    public void assignTrainer(String trainerId, String className) {
        System.out.println("✅ Trainer " + trainerId + " assigned to " + className);
    }
    
    /**
     * Create a new class
     */
    public void assignClass(String className, String trainerId, String schedule) {
        System.out.println("✅ Class created: " + className);
        System.out.println("   Trainer: " + trainerId);
        System.out.println("   Schedule: " + schedule);
    }
    
    /**
     * Remove a trainer
     */
    public void removeTrainer(String trainerId) {
        System.out.println("🗑️ Trainer " + trainerId + " removed");
    }
    
    /**
     * Remove a member
     */
    public void removeMember(String memberId) {
        System.out.println("🗑️ Member " + memberId + " removed");
    }
    
    // ============================================================
    // OVERRIDE METHODS
    // ============================================================
    
    @Override
    public String viewProfile() {
        return super.viewProfile() +
               "\nRole: Administrator" +
               "\nAdmin ID: " + adminId +
               "\nAdmin Level: " + adminLevel +
               "\nDepartment: " + department +
               "\nLast Login: " + (lastLogin != null ? lastLogin : "Never");
    }
    
    @Override
    public String toString() {
        return "Admin{" +
               "adminId='" + adminId + '\'' +
               ", name='" + getName() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", adminLevel='" + adminLevel + '\'' +
               '}';
    }
}