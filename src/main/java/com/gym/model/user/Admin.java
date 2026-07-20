package com.gym.model.user;

public class Admin extends User {
    private String adminId;
    private String adminLevel; // e.g., "Super", "Manager", "Staff"
    
    // Constructor
    public Admin(int profileId, String name, String email, String phone, String address,
                 String userId, String password, String adminLevel) {
        super(profileId, name, email, phone, address, userId, password);
        this.adminId = userId; // Admin ID can be same as User ID
        this.adminLevel = adminLevel;
    }
    
    // GETTERS & SETTERS
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    
    // ADMIN-SPECIFIC METHODS
    public void createMember(String name, String email, String phone, String address) {
        // In a real system, this would create a Profile object
        System.out.println("✅ Member created: " + name);
        System.out.println("   Email: " + email);
        System.out.println("   Phone: " + phone);
        System.out.println("   Address: " + address);
    }
    
    public void assignTrainer(String trainerId, String className) {
        System.out.println("✅ Trainer " + trainerId + " assigned to " + className);
    }
    
    public void assignClass(String className, String trainerId, String schedule) {
        System.out.println("✅ Class created: " + className);
        System.out.println("   Trainer: " + trainerId);
        System.out.println("   Schedule: " + schedule);
    }
    
    public void removeTrainer(String trainerId) {
        System.out.println("🗑️ Trainer " + trainerId + " removed");
    }
    
    public void removeMember(String memberId) {
        System.out.println("🗑️ Member " + memberId + " removed");
    }
    
    // Override resetPassword
    @Override
    public void resetPassword(String newPassword) {
        super.resetPassword(newPassword);
        System.out.println("Admin password updated!");
    }
    
    // Override viewProfile
    @Override
    public String viewProfile() {
        return super.viewProfile() + 
               "\nRole: Administrator" +
               "\nAdmin ID: " + adminId +
               "\nAdmin Level: " + adminLevel;
    }
}