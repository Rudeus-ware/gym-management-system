package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProfileController - Handles profile-related operations
 * Uses DatabaseManager as primary, JsonDataManager as backup
 */
public class ProfileController {
    
    private GymController gymController;
    private IdGenerator idGenerator;
    
    public ProfileController(GymController gymController) {
        this.gymController = gymController;
        this.idGenerator = new IdGenerator(null);
    }
    
    // ============================================================
    // DATA ACCESS HELPERS
    // ============================================================
    
    private List<Profile> getProfiles() {
        return gymController.getAllProfiles();
    }
    
    private Profile findProfileById(String id) {
        return gymController.getProfileById(id);
    }
    
    private void saveAllData() {
        gymController.saveAllData();
    }
    
    // ============================================================
    // CREATE
    // ============================================================
    
    public Profile createProfile(String name, String email, String phone, String address) {
        // Check for duplicate email
        for (Profile p : getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                System.out.println("❌ Email already exists: " + email);
                return null;
            }
        }
        
        String profileId = idGenerator.generateProfileId("22", LocalDate.now());
        Profile profile = new Profile(profileId, name, email, phone, address);
        profile.setActive(true);
        
        gymController.createProfile(profile);
        saveAllData();
        
        System.out.println("✅ Profile created: " + name + " (ID: " + profileId + ")");
        return profile;
    }
    
    public Profile createProfileWithMembership(String name, String email, String phone, 
                                               String address, String membershipType) {
        Profile profile = createProfile(name, email, phone, address);
        if (profile != null) {
            assignMembership(profile.getProfileId(), membershipType);
        }
        return profile;
    }
    
    // ============================================================
    // READ
    // ============================================================
    
    public Profile getProfileById(String profileId) {
        return findProfileById(profileId);
    }
    
    public Profile getProfileByEmail(String email) {
        for (Profile p : getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }
    
    public List<Profile> getAllProfiles() {
        return getProfiles();
    }
    
    public List<Profile> getActiveMembers() {
        return getProfiles().stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .collect(Collectors.toList());
    }
    
    public List<Profile> searchProfiles(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return getProfiles();
        }
        String search = searchTerm.toLowerCase();
        return getProfiles().stream()
            .filter(p -> p.getName().toLowerCase().contains(search) ||
                        p.getEmail().toLowerCase().contains(search) ||
                        p.getPhone().contains(search))
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // UPDATE
    // ============================================================
    
    public boolean updateProfile(String profileId, String name, String email, 
                                 String phone, String address) {
        Profile profile = findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        // Check if email is being changed and already exists
        if (!profile.getEmail().equalsIgnoreCase(email) && 
            getProfileByEmail(email) != null) {
            System.out.println("❌ Email already exists: " + email);
            return false;
        }
        
        profile.updateProfile(name, email, phone, address);
        saveAllData();
        
        System.out.println("✅ Profile updated: " + profileId);
        return true;
    }
    
    public boolean assignMembership(String profileId, String membershipType) {
        Profile profile = findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        // Remove old membership if exists
        if (profile.getMembership() != null) {
            // Remove from data source
        }
        
        Membership membership = createMembershipByType(membershipType);
        if (membership == null) {
            System.out.println("❌ Invalid membership type: " + membershipType);
            return false;
        }
        
        profile.setMembership(membership);
        saveAllData();
        
        System.out.println("✅ Membership assigned: " + membershipType);
        return true;
    }
    
    public boolean activateProfile(String profileId) {
        Profile profile = findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        profile.setActive(true);
        saveAllData();
        System.out.println("✅ Profile activated: " + profileId);
        return true;
    }
    
    public boolean deactivateProfile(String profileId) {
        Profile profile = findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        profile.setActive(false);
        saveAllData();
        System.out.println("✅ Profile deactivated: " + profileId);
        return true;
    }
    
    // ============================================================
    // DELETE
    // ============================================================
    
    public boolean deleteProfile(String profileId) {
        Profile profile = findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        gymController.deleteProfile(profileId);
        saveAllData();
        
        System.out.println("✅ Profile deleted: " + profileId);
        return true;
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    public ProfileStats getStats() {
        List<Profile> profiles = getProfiles();
        int total = profiles.size();
        int active = (int) profiles.stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .count();
        int inactive = total - active;
        
        long basic = profiles.stream()
            .filter(p -> p.getMembership() != null)
            .filter(p -> p.getMembership().getClass().getSimpleName().equals("Basic"))
            .count();
        long premium = profiles.stream()
            .filter(p -> p.getMembership() != null)
            .filter(p -> p.getMembership().getClass().getSimpleName().equals("Premium"))
            .count();
        long family = profiles.stream()
            .filter(p -> p.getMembership() != null)
            .filter(p -> p.getMembership().getClass().getSimpleName().equals("Family"))
            .count();
        
        return new ProfileStats(total, active, inactive, basic, premium, family);
    }
    
    // ============================================================
    // HELPERS
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
    
    // ============================================================
    // INNER CLASS
    // ============================================================
    
    public static class ProfileStats {
        public final int total;
        public final int active;
        public final int inactive;
        public final long basic;
        public final long premium;
        public final long family;
        
        public ProfileStats(int total, int active, int inactive, 
                           long basic, long premium, long family) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
            this.basic = basic;
            this.premium = premium;
            this.family = family;
        }
        
        @Override
        public String toString() {
            return String.format(
                "📊 PROFILE STATISTICS\n" +
                "=====================\n" +
                "Total Profiles: %d\n" +
                "Active: %d\n" +
                "Inactive: %d\n" +
                "Membership Breakdown:\n" +
                "  Basic: %d\n" +
                "  Premium: %d\n" +
                "  Family: %d",
                total, active, inactive, basic, premium, family
            );
        }
    }
}