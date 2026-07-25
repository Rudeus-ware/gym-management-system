package com.gym.service;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.membership.Membership;

/**
 * MembershipService - Handles membership operations
 * Updated to use proper Membership methods
 */
public class MembershipService {
    private final List<Membership> memberships = new ArrayList<>();

    public void addMembership(Membership membership) {
        memberships.add(membership);
    }

    // ============================================================
    // FIND BY TYPE
    // ============================================================
    
    /**
     * Find membership by type (Basic, Premium, Family)
     */
    public Membership findByType(String type) {
        return memberships.stream()
                .filter(m -> m.getClass().getSimpleName().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * Find all memberships by type
     */
    public List<Membership> findAllByType(String type) {
        return memberships.stream()
                .filter(m -> m.getClass().getSimpleName().equalsIgnoreCase(type))
                .collect(java.util.stream.Collectors.toList());
    }

    // ============================================================
    // FIND BY ID
    // ============================================================
    
    /**
     * Find membership by ID
     */
    public Membership findById(String membershipId) {
        return memberships.stream()
                .filter(m -> m.getMembershipId().equals(membershipId))
                .findFirst()
                .orElse(null);
    }

    // ============================================================
    // FIND BY STATUS
    // ============================================================
    
    /**
     * Find active memberships
     */
    public List<Membership> findActiveMemberships() {
        return memberships.stream()
                .filter(m -> m.getStatus().equalsIgnoreCase("Active"))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Find memberships by status
     */
    public List<Membership> findByStatus(String status) {
        return memberships.stream()
                .filter(m -> m.getStatus().equalsIgnoreCase(status))
                .collect(java.util.stream.Collectors.toList());
    }

    // ============================================================
    // FIND BY PROFILE (if membership has profile reference)
    // ============================================================
    
    // Note: If Membership has a profileId field, you can add:
    // public Membership findByProfileId(String profileId) {
    //     return memberships.stream()
    //             .filter(m -> m.getProfileId().equals(profileId))
    //             .findFirst()
    //             .orElse(null);
    // }

    // ============================================================
    // VALIDATION
    // ============================================================
    
    /**
     * Check if a valid membership exists for a profile
     */
    public boolean hasValidMembership(String profileId) {
        return memberships.stream()
                .anyMatch(m -> m.getStatus().equalsIgnoreCase("Active") 
                        && m.isValid());
    }

    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get count of memberships by type
     */
    public long countByType(String type) {
        return memberships.stream()
                .filter(m -> m.getClass().getSimpleName().equalsIgnoreCase(type))
                .count();
    }

    /**
     * Get total active memberships
     */
    public long countActive() {
        return memberships.stream()
                .filter(m -> m.getStatus().equalsIgnoreCase("Active"))
                .count();
    }

    // ============================================================
    // ORIGINAL METHOD (For backward compatibility - if needed)
    // ============================================================
    
    /**
     * @deprecated Use findByType() instead
     */
    @Deprecated
    public Membership findByName(String name) {
        // If you want to search by type instead of name
        return findByType(name);
        
        // OR if you want to keep the name but search differently:
        // return memberships.stream()
        //         .filter(m -> m.getClass().getSimpleName().equalsIgnoreCase(name))
        //         .findFirst()
        //         .orElse(null);
    }
}