package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.persistence.DataManager;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Membership operations
 * Handles all membership-related business logic
 */
public class MembershipController {
    
    private DataManager dataManager;
    
    public MembershipController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ===== CREATE MEMBERSHIP =====
    
    /**
     * Create a Basic membership for a profile
     */
    public Membership createBasicMembership(int profileId) {
        int membershipId = dataManager.getMemberships().size() + 1001;
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        
        Basic membership = new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
        dataManager.addMembership(membership);
        
        // Associate with profile
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembership(membership);
            dataManager.saveAllData();
        }
        
        return membership;
    }
    
    /**
     * Create a Premium membership for a profile
     */
    public Membership createPremiumMembership(int profileId) {
        int membershipId = dataManager.getMemberships().size() + 2001;
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        
        Premium membership = new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
        dataManager.addMembership(membership);
        
        // Associate with profile
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembership(membership);
            dataManager.saveAllData();
        }
        
        return membership;
    }
    
    /**
     * Create a Family membership for a profile
     */
    public Membership createFamilyMembership(int profileId, int numberOfMembers) {
        int membershipId = dataManager.getMemberships().size() + 3001;
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        
        Family membership = new Family(membershipId, 69.99, startDate, expiryDate, "Active", numberOfMembers);
        dataManager.addMembership(membership);
        
        // Associate with profile
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembership(membership);
            dataManager.saveAllData();
        }
        
        return membership;
    }
    
    // ===== RENEW MEMBERSHIP =====
    
    /**
     * Renew a membership
     */
    public boolean renewMembership(int membershipId) {
        for (Membership m : dataManager.getMemberships()) {
            if (m.getMembershipId() == membershipId) {
                m.renew();
                dataManager.saveAllData();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Renew membership for a profile
     */
    public boolean renewMembershipForProfile(int profileId) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile != null && profile.getMembership() != null) {
            profile.getMembership().renew();
            dataManager.saveAllData();
            return true;
        }
        return false;
    }
    
    // ===== VALIDATION =====
    
    /**
     * Check if a profile has a valid membership
     */
    public boolean hasValidMembership(int profileId) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null || profile.getMembership() == null) {
            return false;
        }
        return profile.getMembership().isValid();
    }
    
    /**
     * Get membership status for a profile
     */
    public String getMembershipStatus(int profileId) {
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
    public String getMembershipType(int profileId) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null || profile.getMembership() == null) {
            return "None";
        }
        return profile.getMembership().getClass().getSimpleName();
    }
    
    // ===== UPGRADE/DOWNGRADE =====
    
    /**
     * Upgrade a membership to Premium
     */
    public Membership upgradeToPremium(int profileId) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            return null;
        }
        
        // Remove existing membership
        Membership oldMembership = profile.getMembership();
        if (oldMembership != null) {
            dataManager.getMemberships().remove(oldMembership);
        }
        
        // Create new Premium membership
        return createPremiumMembership(profileId);
    }
    
    /**
     * Downgrade a membership to Basic
     */
    public Membership downgradeToBasic(int profileId) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            return null;
        }
        
        // Remove existing membership
        Membership oldMembership = profile.getMembership();
        if (oldMembership != null) {
            dataManager.getMemberships().remove(oldMembership);
        }
        
        // Create new Basic membership
        return createBasicMembership(profileId);
    }
    
    // ===== QUERIES =====
    
    /**
     * Get all members with active memberships
     */
    public List<Profile> getActiveMembers() {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get all members with expired memberships
     */
    public List<Profile> getExpiredMembers() {
        return dataManager.getProfiles().stream()
            .filter(p -> p.getMembership() == null || !p.getMembership().isValid())
            .collect(java.util.stream.Collectors.toList());
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
}