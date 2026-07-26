package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MembershipController {
    
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;
    
    public MembershipController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("MEM");
        }
    }
    
    public MembershipController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
        this.idGenerator = new IdGenerator("MEM");
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    /**
     * Create a new membership for a profile
     * @param profileId The ID of the profile (PARAMETER DECLARED HERE)
     * @param type The membership type (BASIC, PREMIUM, FAMILY)
     * @param price The membership price
     * @param duration The duration in days
     */
    public Membership createMembership(String profileId, String type, double price, int duration) {
        // ✅ profileId is declared as a parameter above
        
        if (profileId == null || profileId.isEmpty()) {
            System.out.println("❌ Invalid profile ID");
            return null;
        }
        
        Profile profile = databaseManager.findProfileById(profileId);  // ✅ profileId is now defined
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        String membershipId = idGenerator.generateMembershipId(LocalDate.now());
        Membership membership = databaseManager.createMembership(membershipId, profileId, type, price, duration);
        
        profile.setMembershipType(type);
        databaseManager.updateProfile(profile);
        databaseManager.saveAllData();
        
        System.out.println("✅ Membership created for: " + profile.getName());
        return membership;
    }
    
    // Convenience methods with fixed types
    public Membership createBasicMembership(String profileId) {
        return createMembership(profileId, "BASIC", 29.99, 30);  // ✅ profileId passed as parameter
    }
    
    public Membership createPremiumMembership(String profileId) {
        return createMembership(profileId, "PREMIUM", 59.99, 30);  // ✅ profileId passed as parameter
    }
    
    public Membership createFamilyMembership(String profileId) {
        return createMembership(profileId, "FAMILY", 89.99, 30);  // ✅ profileId passed as parameter
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    /**
     * Get membership by ID
     * @param membershipId The membership ID (PARAMETER DECLARED HERE)
     */
    public Membership getMembershipById(String membershipId) {
        // ✅ membershipId is declared as a parameter above
        if (membershipId == null || membershipId.isEmpty()) return null;
        return databaseManager.getMemberships().stream()
            .filter(m -> m.getMembershipId().equals(membershipId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get all memberships
     */
    public List<Membership> getAllMemberships() {
        return databaseManager.findAllMemberships();
    }
    
    /**
     * Get memberships for a specific profile
     * @param profileId The profile ID (PARAMETER DECLARED HERE)
     */
    public List<Membership> getMembershipsForProfile(String profileId) {
        // ✅ profileId is declared as a parameter above
        if (profileId == null || profileId.isEmpty()) return List.of();
        return databaseManager.getMemberships().stream()
            .filter(m -> m.getProfileId().equals(profileId))  // ✅ profileId is defined
            .collect(Collectors.toList());
    }
    
    /**
     * Get active memberships
     */
    public List<Membership> getActiveMemberships() {
        return databaseManager.getMemberships().stream()
            .filter(Membership::isActive)
            .collect(Collectors.toList());
    }
    
    /**
     * Get memberships by type
     * @param type The membership type (PARAMETER DECLARED HERE)
     */
    public List<Membership> getMembershipsByType(String type) {
        // ✅ type is declared as a parameter above
        if (type == null || type.isEmpty()) return List.of();
        return databaseManager.getMemberships().stream()
            .filter(m -> m.getType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    /**
     * Renew a membership
     * @param membershipId The membership ID (PARAMETER DECLARED HERE)
     */
    public boolean renewMembership(String membershipId) {
        // ✅ membershipId is declared as a parameter above
        if (membershipId == null || membershipId.isEmpty()) {
            System.out.println("❌ Invalid membership ID");
            return false;
        }
        
        Membership membership = getMembershipById(membershipId);
        if (membership == null) {
            System.out.println("❌ Membership not found: " + membershipId);
            return false;
        }
        
        if (membership.isCancelled()) {
            System.out.println("❌ Cannot renew a cancelled membership");
            return false;
        }
        
        membership.renew();
        databaseManager.saveAllData();
        System.out.println("✅ Membership renewed: " + membershipId);
        return true;
    }
    
    /**
     * Cancel a membership
     * @param membershipId The membership ID (PARAMETER DECLARED HERE)
     */
    public boolean cancelMembership(String membershipId) {
        // ✅ membershipId is declared as a parameter above
        if (membershipId == null || membershipId.isEmpty()) {
            System.out.println("❌ Invalid membership ID");
            return false;
        }
        
        Membership membership = getMembershipById(membershipId);
        if (membership == null) {
            System.out.println("❌ Membership not found: " + membershipId);
            return false;
        }
        
        if (membership.isCancelled()) {
            System.out.println("❌ Membership already cancelled");
            return false;
        }
        
        membership.cancel();
        
        // Get profileId from the membership
        String profileId = membership.getProfileId();  // ✅ profileId is declared here
        Profile profile = databaseManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembershipType("NONE");
            databaseManager.updateProfile(profile);
        }
        
        databaseManager.saveAllData();
        System.out.println("✅ Membership cancelled: " + membershipId);
        return true;
    }
    
    /**
     * Upgrade a membership to a new type
     * @param membershipId The membership ID (PARAMETER DECLARED HERE)
     * @param newType The new membership type (PARAMETER DECLARED HERE)
     */
    public boolean upgradeMembership(String membershipId, String newType) {
        // ✅ Both parameters are declared above
        if (membershipId == null || membershipId.isEmpty()) {
            System.out.println("❌ Invalid membership ID");
            return false;
        }
        if (newType == null || newType.isEmpty()) {
            System.out.println("❌ Invalid membership type");
            return false;
        }
        
        Membership membership = getMembershipById(membershipId);
        if (membership == null) {
            System.out.println("❌ Membership not found: " + membershipId);
            return false;
        }
        
        if (membership.isCancelled() || membership.isExpired()) {
            System.out.println("❌ Cannot upgrade a cancelled or expired membership");
            return false;
        }
        
        double newPrice = getMembershipPrice(newType);
        if (newPrice <= 0) {
            System.out.println("❌ Invalid membership type: " + newType);
            return false;
        }
        
        membership.setType(newType);
        membership.setPrice(newPrice);
        
        // Get profileId from the membership
        String profileId = membership.getProfileId();  // ✅ profileId is declared here
        Profile profile = databaseManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembershipType(newType);
            databaseManager.updateProfile(profile);
        }
        
        databaseManager.saveAllData();
        System.out.println("✅ Membership upgraded to: " + newType);
        return true;
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    /**
     * Delete a membership
     * @param membershipId The membership ID (PARAMETER DECLARED HERE)
     */
    public boolean deleteMembership(String membershipId) {
        // ✅ membershipId is declared as a parameter above
        if (membershipId == null || membershipId.isEmpty()) {
            System.out.println("❌ Invalid membership ID");
            return false;
        }
        
        Membership membership = getMembershipById(membershipId);
        if (membership == null) {
            System.out.println("❌ Membership not found: " + membershipId);
            return false;
        }
        
        // Get profileId from the membership
        String profileId = membership.getProfileId();  // ✅ profileId is declared here
        Profile profile = databaseManager.findProfileById(profileId);
        if (profile != null) {
            profile.setMembershipType("NONE");
            databaseManager.updateProfile(profile);
        }
        
        boolean removed = databaseManager.getMemberships().removeIf(m -> m.getMembershipId().equals(membershipId));
        if (removed) {
            databaseManager.saveAllData();
            System.out.println("✅ Membership deleted: " + membershipId);
            return true;
        }
        
        System.out.println("❌ Failed to delete membership: " + membershipId);
        return false;
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    public int getTotalMemberships() {
        return databaseManager.getMemberships().size();
    }
    
    public int getActiveMembershipsCount() {
        return (int) databaseManager.getMemberships().stream()
            .filter(Membership::isActive)
            .count();
    }
    
    public int getExpiredMembershipsCount() {
        return (int) databaseManager.getMemberships().stream()
            .filter(Membership::isExpired)
            .count();
    }
    
    public int getCancelledMembershipsCount() {
        return (int) databaseManager.getMemberships().stream()
            .filter(Membership::isCancelled)
            .count();
    }
    
    public int getMembershipCountByType(String type) {
        if (type == null || type.isEmpty()) return 0;
        return (int) databaseManager.getMemberships().stream()
            .filter(m -> m.getType().equalsIgnoreCase(type))
            .count();
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    private double getMembershipPrice(String type) {
        if (type == null) return 0.0;
        return switch (type.toLowerCase()) {
            case "basic" -> 29.99;
            case "premium" -> 59.99;
            case "family" -> 89.99;
            default -> 0.0;
        };
    }
}