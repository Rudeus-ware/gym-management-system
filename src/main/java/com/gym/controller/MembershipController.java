package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.persistence.DataManager;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Membership operations
 * Handles all membership-related business logic
 * Updated to use String-based IDs
 */
public class MembershipController {
    
    private DataManager dataManager;
    private IdGenerator idGenerator;
    
    public MembershipController(DataManager dataManager) {
        this.dataManager = dataManager;
        this.idGenerator = new IdGenerator(null); // Will be initialized properly
    }
    
    // ============================================================
    // CREATE MEMBERSHIP
    // ============================================================
    
    /**
     * Create a Basic membership for a profile
     */
    public Membership createBasicMembership(String profileId) {
        // ✅ Generate String membership ID
        String membershipId = generateMembershipId("BASIC");
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        
        Basic membership = new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
        dataManager.addMembership(membership);
        
        // Associate with profile
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembership(membership);
            dataManager.saveAllData();
            System.out.println("✅ Basic membership created for: " + profile.getName());
        } else {
            System.out.println("❌ Profile not found: " + profileId);
        }
        
        return membership;
    }
    
    /**
     * Create a Premium membership for a profile
     */
    public Membership createPremiumMembership(String profileId) {
        // ✅ Generate String membership ID
        String membershipId = generateMembershipId("PREMIUM");
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        
        Premium membership = new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
        dataManager.addMembership(membership);
        
        // Associate with profile
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembership(membership);
            dataManager.saveAllData();
            System.out.println("✅ Premium membership created for: " + profile.getName());
        } else {
            System.out.println("❌ Profile not found: " + profileId);
        }
        
        return membership;
    }
    
    /**
     * Create a Family membership for a profile
     */
    public Membership createFamilyMembership(String profileId, int numberOfMembers) {  // ✅ Changed String to int
        // ✅ Generate String membership ID
        String membershipId = generateMembershipId("FAMILY");
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        
        Family membership = new Family(membershipId, 69.99, startDate, expiryDate, "Active", numberOfMembers);
        dataManager.addMembership(membership);
        
        // Associate with profile
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembership(membership);
            dataManager.saveAllData();
            System.out.println("✅ Family membership created for: " + profile.getName());
        } else {
            System.out.println("❌ Profile not found: " + profileId);
        }
        
        return membership;
    }
    
    // ============================================================
    // RENEW MEMBERSHIP
    // ============================================================
    
    /**
     * Renew a membership
     */
    public boolean renewMembership(String membershipId) {
        if (membershipId == null || membershipId.isEmpty()) {
            System.out.println("❌ Invalid membership ID");
            return false;
        }
        
        for (Membership m : dataManager.getMemberships()) {
            if (m.getMembershipId().equals(membershipId)) {  // ✅ Use .equals()
                m.renew();
                dataManager.saveAllData();
                System.out.println("✅ Membership renewed: " + membershipId);
                return true;
            }
        }
        System.out.println("❌ Membership not found: " + membershipId);
        return false;
    }
    
    /**
     * Renew membership for a profile
     */
    public boolean renewMembershipForProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            System.out.println("❌ Invalid profile ID");
            return false;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null && profile.getMembership() != null) {
            profile.getMembership().renew();
            dataManager.saveAllData();
            System.out.println("✅ Membership renewed for profile: " + profile.getName());
            return true;
        }
        System.out.println("❌ No membership found for profile: " + profileId);
        return false;
    }
    
    // ============================================================
    // VALIDATION
    // ============================================================
    
    /**
     * Check if a profile has a valid membership
     */
    public boolean hasValidMembership(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            return false;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null || profile.getMembership() == null) {
            return false;
        }
        return profile.getMembership().isValid();
    }
    
    /**
     * Get membership status for a profile
     */
    public String getMembershipStatus(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            return "Invalid profile ID";
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            return "Profile not found";
        }
        if (profile.getMembership() == null) {
            return "No membership";
        }
        if (profile.getMembership().isValid()) {
            return "Active";
        }
        return "Expired";
    }
    
    /**
     * Get membership type for a profile
     */
    public String getMembershipType(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            return "Invalid profile ID";
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null || profile.getMembership() == null) {
            return "None";
        }
        return profile.getMembership().getClass().getSimpleName();
    }
    
    // ============================================================
    // UPGRADE/DOWNGRADE
    // ============================================================
    
    /**
     * Upgrade a membership to Premium
     */
    public Membership upgradeToPremium(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            System.out.println("❌ Invalid profile ID");
            return null;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        // Remove existing membership
        Membership oldMembership = profile.getMembership();
        if (oldMembership != null) {
            dataManager.getMemberships().remove(oldMembership);
        }
        
        // Create new Premium membership
        Membership newMembership = createPremiumMembership(profileId);
        System.out.println("✅ Upgraded to Premium: " + profile.getName());
        return newMembership;
    }
    
    /**
     * Downgrade a membership to Basic
     */
    public Membership downgradeToBasic(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            System.out.println("❌ Invalid profile ID");
            return null;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        // Remove existing membership
        Membership oldMembership = profile.getMembership();
        if (oldMembership != null) {
            dataManager.getMemberships().remove(oldMembership);
        }
        
        // Create new Basic membership
        Membership newMembership = createBasicMembership(profileId);
        System.out.println("✅ Downgraded to Basic: " + profile.getName());
        return newMembership;
    }
    
    // ============================================================
    // QUERIES
    // ============================================================
    
    /**
     * Get all members with active memberships
     */
    public List<Profile> getActiveMembers() {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .collect(Collectors.toList());
    }
    
    /**
     * Get all members with expired memberships
     */
    public List<Profile> getExpiredMembers() {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() == null || !p.getMembership().isValid())
            .collect(Collectors.toList());
    }
    
    /**
     * Get revenue from all memberships
     */
    public double getTotalRevenue() {
        double total = 0;
        for (Membership m : dataManager.getMemberships()) {
            if (m.isValid()) {
                total += m.calculateFee();
            }
        }
        return total;
    }
    
    /**
     * Count members by membership type
     */
    public long countByType(String type) {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() != null)
            .filter(p -> p.getMembership().getClass().getSimpleName().equalsIgnoreCase(type))
            .count();
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    /**
     * Generate a unique membership ID
     */
    private String generateMembershipId(String type) {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = type.substring(0, 3).toUpperCase();
        int count = dataManager.getMemberships().size() + 1;
        return prefix + timestamp + String.format("%04d", count);
    }
}