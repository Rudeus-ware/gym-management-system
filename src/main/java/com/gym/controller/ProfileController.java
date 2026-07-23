package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.persistence.DataManager;
import com.gym.database.ProfileDatabaseManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProfileController - Handles all profile-related operations
 * Supports both Database and File-based persistence
 */
public class ProfileController {
    
    private DataManager dataManager;
    private ProfileDatabaseManager profileDatabaseManager;
    private boolean useDatabase = true;
    
    public ProfileController(DataManager dataManager) {
        this.dataManager = dataManager;
        this.profileDatabaseManager = new ProfileDatabaseManager();
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    /**
     * Create a new member profile
     */
    public Profile createProfile(String name, String email, String phone, String address) {
        if (useDatabase) {
            return profileDatabaseManager.createProfile(name, email, phone, address);
        } else {
            // File-based fallback
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
            return profile;
        }
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
        if (useDatabase) {
            return profileDatabaseManager.findProfileById(profileId);
        } else {
            return dataManager.findProfileById(profileId);
        }
    }
    
    /**
     * Get profile by email
     */
    public Profile getProfileByEmail(String email) {
        if (useDatabase) {
            return profileDatabaseManager.findProfileByEmail(email);
        } else {
            for (Profile p : dataManager.getProfiles()) {
                if (p.getEmail().equalsIgnoreCase(email)) {
                    return p;
                }
            }
            return null;
        }
    }
    
    /**
     * Get all profiles
     */
    public List<Profile> getAllProfiles() {
        if (useDatabase) {
            return profileDatabaseManager.findAllProfiles();
        } else {
            return dataManager.getProfiles();
        }
    }
    
    /**
     * Get profiles with active memberships
     */
    public List<Profile> getActiveMembers() {
        if (useDatabase) {
            return profileDatabaseManager.findActiveMembers();
        } else {
            return dataManager.getProfiles().stream()
                .filter(p -> p.getMembership() != null && p.getMembership().isValid())
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Search profiles by name or email
     */
    public List<Profile> searchProfiles(String searchTerm) {
        if (useDatabase) {
            return profileDatabaseManager.searchProfiles(searchTerm);
        } else {
            String search = searchTerm.toLowerCase();
            return dataManager.getProfiles().stream()
                .filter(p -> p.getName().toLowerCase().contains(search) ||
                            p.getEmail().toLowerCase().contains(search))
                .collect(Collectors.toList());
        }
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    /**
     * Update profile details
     */
    public boolean updateProfile(int profileId, String name, String email, 
                                 String phone, String address) {
        Profile profile = getProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        profile.updateProfile(name, email, phone, address);
        
        if (useDatabase) {
            return profileDatabaseManager.updateProfile(profile);
        } else {
            dataManager.saveAllData();
            return true;
        }
    }
    
    /**
     * Assign membership to a profile
     */
    public boolean assignMembership(int profileId, String membershipType) {
        Profile profile = getProfileById(profileId);
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
        
        System.out.println("✅ Membership assigned: " + membershipType);
        return true;
    }
    
    /**
     * Delete a profile
     */
    public boolean deleteProfile(int profileId) {
        if (useDatabase) {
            return profileDatabaseManager.deleteProfile(profileId);
        } else {
            Profile profile = dataManager.findProfileById(profileId);
            if (profile == null) return false;
            
            if (profile.getMembership() != null) {
                dataManager.getMemberships().remove(profile.getMembership());
            }
            dataManager.removeProfile(profileId);
            dataManager.saveAllData();
            return true;
        }
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get profile statistics
     */
    public ProfileDatabaseManager.ProfileStats getStats() {
        if (useDatabase) {
            return profileDatabaseManager.getStatistics();
        } else {
            List<Profile> profiles = dataManager.getProfiles();
            int total = profiles.size();
            int active = (int) profiles.stream()
                .filter(p -> p.getMembership() != null && p.getMembership().isValid())
                .count();
            int inactive = total - active;
            return new ProfileDatabaseManager.ProfileStats(total, active, inactive);
        }
    }
    
    // ============================================================
    // SWITCH METHODS
    // ============================================================
    
    public void switchToDatabase() {
        useDatabase = true;
        System.out.println("✅ ProfileController switched to Database mode");
    }
    
    public void switchToFile() {
        useDatabase = false;
        System.out.println("✅ ProfileController switched to File mode");
    }
    
    // ============================================================
    // HELPER METHODS
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
}
