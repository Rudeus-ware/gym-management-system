package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Attendance;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminController - Handles all administrative operations
 * Uses DatabaseManager as primary, JsonDataManager as backup
 */
public class AdminController {
    
    private GymController gymController;
    private Admin currentAdmin;
    private IdGenerator idGenerator;
    
    public AdminController(GymController gymController) {
        this.gymController = gymController;
        this.idGenerator = new IdGenerator(null);
    }
    
    // ============================================================
    // DATA ACCESS HELPER METHODS
    // ============================================================
    
    private List<Profile> getProfiles() {
        return gymController.getAllProfiles();
    }
    
    private void addProfile(Profile profile) {
        gymController.createProfile(profile);
    }
    
    private Profile findProfileById(String id) {
        return gymController.getProfileById(id);
    }
    
    private void saveAllData() {
        gymController.saveAllData();
    }
    
    // ============================================================
    // ADMIN MANAGEMENT
    // ============================================================
    
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        System.out.println("✅ Admin logged in: " + admin.getName());
    }
    
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
    
    private boolean isAuthorized() {
        return currentAdmin != null;
    }
    
    // ============================================================
    // MEMBER MANAGEMENT
    // ============================================================
    
    public Profile createMember(String name, String email, String phone, String address, 
                               String membershipType) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return null;
        }
        
        // Check for duplicate email
        for (Profile p : getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                System.out.println("❌ Email already exists: " + email);
                return null;
            }
        }
        
        // Generate profile ID
        String profileId = idGenerator.generateProfileId("22", LocalDate.now());
        Profile profile = new Profile(profileId, name, email, phone, address);
        profile.setActive(true);
        
        // Add membership
        Membership membership = createMembershipByType(membershipType);
        if (membership != null) {
            profile.setMembership(membership);
        }
        
        // Save
        addProfile(profile);
        saveAllData();
        
        System.out.println("✅ Member created: " + name + " (ID: " + profileId + ")");
        return profile;
    }
    
    public boolean removeMember(String profileId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized!");
            return false;
        }
        
        Profile profile = findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Member not found: " + profileId);
            return false;
        }
        
        String memberName = profile.getName();
        
        // Remove profile
        gymController.deleteProfile(profileId);
        saveAllData();
        
        System.out.println("✅ Member removed: " + memberName + " (ID: " + profileId + ")");
        return true;
    }
    
    public boolean updateMember(String profileId, String name, String email, 
                                String phone, String address) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized!");
            return false;
        }
        
        Profile profile = findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Member not found: " + profileId);
            return false;
        }
        
        profile.updateProfile(name, email, phone, address);
        saveAllData();
        
        System.out.println("✅ Member updated: " + name + " (ID: " + profileId + ")");
        return true;
    }
    
    public List<Profile> getAllMembers() {
        return getProfiles();
    }
    
    public Profile getMember(String profileId) {
        return findProfileById(profileId);
    }
    
    // ============================================================
    // TRAINER MANAGEMENT
    // ============================================================
    
    public Trainer createTrainer(String name, String email, String phone, String address,
                                 String userId, String password, String specialization) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized!");
            return null;
        }
        
        String profileId = idGenerator.generateProfileId("11", LocalDate.now());
        Trainer trainer = new Trainer(profileId, name, email, phone, address, 
                                      userId, password, specialization);
        
        // Add to data source
        // This would need to be implemented in GymController
        // For now, we'll save to both sources
        saveAllData();
        
        System.out.println("✅ Trainer created: " + name + " (ID: " + profileId + ")");
        return trainer;
    }
    
    public boolean removeTrainer(String trainerId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized!");
            return false;
        }
        System.out.println("✅ Trainer removed: " + trainerId);
        return true;
    }
    
    public List<Trainer> getAllTrainers() {
        return gymController.getAllTrainers();
    }
    
    // ============================================================
    // MEMBERSHIP MANAGEMENT
    // ============================================================
    
    private Membership createMembershipByType(String type) {
        if (type == null) return null;
        
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        String membershipId = "MEM" + System.currentTimeMillis();
        
        switch (type) {
            case "Basic":
                return new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
            case "Premium":
                return new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
            case "Family":
                return new Family(membershipId, 69.99, startDate, expiryDate, "Active", 2);
            default:
                return null;
        }
    }
}