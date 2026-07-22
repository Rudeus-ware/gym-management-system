package com.gym.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.Profile;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Premium;
import com.gym.persistence.DataManager;

/**
 * ProfileController - Handles all profile-related operations
 * Pure business logic - NO UI code
 */
public class ProfileController {
    
    private DataManager dataManager;
    
    public ProfileController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    /**
     * Create a new member profile
     */
    public Profile createProfile(String name, String email, String phone, String address) {
        // Check for duplicate email
        for (Profile p : dataManager.getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                System.out.println("❌ Email already exists: " + email);
                return null;
            }
        }
        
        int profileId = dataManager.getProfiles().size() + 1;
        Profile profile = new Profile(profileId, name, email, phone, address);
        dataManager.addProfile(profile);
        dataManager.saveAllData();
        
        System.out.println("✅ Profile created: " + name + " (ID: " + profileId + ")");
        return profile;
    }
    
    /**
     * Create a profile with membership
     */
    public Profile createProfileWithMembership(String name, String email, String phone, 
                                               String address, String membershipType) {
        Profile profile = createProfile(name, email, phone, address);
        if (profile != null) {
            assignMembership(profile.getProfileId(), membershipType);
        }
        return profile;
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    /**
     * Get profile by ID
     */
    public Profile getProfileById(int profileId) {
        return dataManager.findProfileById(profileId);
    }
    
    /**
     * Get profile by email
     */
    public Profile getProfileByEmail(String email) {
        for (Profile p : dataManager.getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * Get all profiles
     */
    public List<Profile> getAllProfiles() {
        return dataManager.getProfiles();
    }
    
    /**
     * Get profiles with active memberships
     */
    public List<Profile> getActiveMembers() {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .collect(Collectors.toList());
    }
    
    /**
     * Get profiles with expired memberships
     */
    public List<Profile> getExpiredMembers() {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() == null || !p.getMembership().isValid())
            .collect(Collectors.toList());
    }
    
    /**
     * Search profiles by name or email
     */
    public List<Profile> searchProfiles(String searchTerm) {
        String search = searchTerm.toLowerCase();
        return dataManager.getProfiles().stream()
            .filter(p -> p.getName().toLowerCase().contains(search) ||
                        p.getEmail().toLowerCase().contains(search) ||
                        p.getPhone().contains(search))
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    /**
     * Update profile details
     */
    public boolean updateProfile(int profileId, String name, String email, 
                                 String phone, String address) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        profile.updateProfile(name, email, phone, address);
        dataManager.saveAllData();
        
        System.out.println("✅ Profile updated: " + name + " (ID: " + profileId + ")");
        return true;
    }
    
    /**
     * Assign membership to a profile
     */
    public boolean assignMembership(int profileId, String membershipType) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        // Remove old membership if exists
        if (profile.getMembership() != null) {
            dataManager.getMemberships().remove(profile.getMembership());
        }
        
        // Create new membership
        Membership membership = createMembershipByType(profileId, membershipType);
        if (membership == null) {
            System.out.println("❌ Invalid membership type: " + membershipType);
            return false;
        }
        
        profile.setMembership(membership);
        dataManager.addMembership(membership);
        dataManager.saveAllData();
        
        System.out.println("✅ Membership assigned: " + membershipType + 
                         " to " + profile.getName());
        return true;
    }
    
    /**
     * Renew a profile's membership
     */
    public boolean renewMembership(int profileId) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        if (profile.getMembership() == null) {
            System.out.println("❌ No membership to renew for: " + profile.getName());
            return false;
        }
        
        profile.getMembership().renew();
        dataManager.saveAllData();
        
        System.out.println("✅ Membership renewed for: " + profile.getName());
        return true;
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    /**
     * Delete a profile (and all associated data)
     */
    public boolean deleteProfile(int profileId) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        String name = profile.getName();
        
        // Remove membership
        if (profile.getMembership() != null) {
            dataManager.getMemberships().remove(profile.getMembership());
        }
        
        // Remove bookings
        dataManager.getBookings().removeIf(b -> b.getProfileId() == profileId);
        
        // Remove attendance
        dataManager.getAttendanceRecords().removeIf(a -> a.getProfileId() == profileId);
        
        // Remove profile
        dataManager.removeProfile(profileId);
        dataManager.saveAllData();
        
        System.out.println("✅ Profile deleted: " + name + " (ID: " + profileId + ")");
        return true;
    }
    
    // ============================================================
    // MEMBERSHIP HELPERS
    // ============================================================
    
    private Membership createMembershipByType(int profileId, String type) {
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        int membershipId = dataManager.getMemberships().size() + 1000;
        
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
    // STATISTICS
    // ============================================================
    
    /**
     * Get profile statistics
     */
    public ProfileStats getStats() {
        List<Profile> profiles = dataManager.getProfiles();
        long active = profiles.stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .count();
        long inactive = profiles.size() - active;
        
        long basic = countMembershipType("Basic");
        long premium = countMembershipType("Premium");
        long family = countMembershipType("Family");
        
        return new ProfileStats(profiles.size(), active, inactive, basic, premium, family);
    }
    
    private long countMembershipType(String type) {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() != null)
            .filter(p -> p.getMembership().getClass().getSimpleName().equals(type))
            .count();
    }
    
    /**
     * Inner class for profile statistics
     */
    public static class ProfileStats {
        public final int total;
        public final long active;
        public final long inactive;
        public final long basic;
        public final long premium;
        public final long family;
        
        public ProfileStats(int total, long active, long inactive, 
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
                "Total Members: %d\n" +
                "Active Members: %d\n" +
                "Inactive Members: %d\n" +
                "Membership Types:\n" +
                "  - Basic: %d\n" +
                "  - Premium: %d\n" +
                "  - Family: %d",
                total, active, inactive, basic, premium, family
            );
        }
    }
}