package com.gym.model.membership;

import java.util.ArrayList;
import java.util.List;

public class Family extends Membership {
    
    // Family-specific constants
    private static final String MEMBERSHIP_TYPE = "FAMILY";
    private static final int DEFAULT_DURATION = 30; // 30 days
    private static final double BASE_PRICE = 89.99;
    private static final int MAX_GUEST_PASSES = 6;
    private static final int MAX_FAMILY_MEMBERS = 4;
    private static final double RENEWAL_DISCOUNT = 0.08; // 8% discount
    private static final double ADDITIONAL_MEMBER_DISCOUNT = 0.10; // 10% off per additional member
    
    // Additional fields for Family
    private int guestPassesUsed;
    private List<FamilyMember> familyMembers;
    private boolean hasKidsClubAccess;
    private boolean hasFamilyEventsAccess;
    
    // ============================================================
    // INNER CLASS FOR FAMILY MEMBERS
    // ============================================================
    
    public static class FamilyMember {
        private String name;
        private String relationship;
        private int age;
        private boolean isActive;
        
        public FamilyMember(String name, String relationship, int age) {
            this.name = name;
            this.relationship = relationship;
            this.age = age;
            this.isActive = true;
        }
        
        // Getters and setters
        public String getName() { return name; }
        public String getRelationship() { return relationship; }
        public int getAge() { return age; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        
        @Override
        public String toString() {
            return String.format("%s (%s, %d years old) - %s", 
                name, relationship, age, isActive ? "Active" : "Inactive");
        }
    }
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Full constructor
     */
    public Family(String membershipId, String profileId, double price, int duration,
                  String startDate, String endDate, String status) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, price, duration);
        
        if (startDate != null) setStartDate(startDate);
        if (endDate != null) setEndDate(endDate);
        if (status != null) setStatus(status);
        
        this.guestPassesUsed = 0;
        this.familyMembers = new ArrayList<>();
        this.hasKidsClubAccess = true;
        this.hasFamilyEventsAccess = true;
    }
    
    /**
     * Standard constructor with default values
     */
    public Family(String membershipId, String profileId) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, BASE_PRICE, DEFAULT_DURATION);
        this.guestPassesUsed = 0;
        this.familyMembers = new ArrayList<>();
        this.hasKidsClubAccess = true;
        this.hasFamilyEventsAccess = true;
    }
    
    /**
     * Constructor with custom price and duration
     */
    public Family(String membershipId, String profileId, double price, int duration) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, price, duration);
        this.guestPassesUsed = 0;
        this.familyMembers = new ArrayList<>();
        this.hasKidsClubAccess = true;
        this.hasFamilyEventsAccess = true;
    }
    
    // ============================================================
    // FAMILY-SPECIFIC METHODS
    // ============================================================
    
    public String getMembershipType() {
        return MEMBERSHIP_TYPE;
    }
    
    public int getMaxGuestPasses() {
        return MAX_GUEST_PASSES;
    }
    
    public int getGuestPassesUsed() {
        return guestPassesUsed;
    }
    
    public int getMaxFamilyMembers() {
        return MAX_FAMILY_MEMBERS;
    }
    
    public List<FamilyMember> getFamilyMembers() {
        return new ArrayList<>(familyMembers);
    }
    
    public boolean hasKidsClubAccess() {
        return hasKidsClubAccess;
    }
    
    public boolean hasFamilyEventsAccess() {
        return hasFamilyEventsAccess;
    }
    
    /**
     * Add a family member
     */
    public boolean addFamilyMember(String name, String relationship, int age) {
        if (!isValid()) return false;
        if (familyMembers.size() >= MAX_FAMILY_MEMBERS) return false;
        if (findFamilyMember(name) != null) return false; // Duplicate check
        
        FamilyMember member = new FamilyMember(name, relationship, age);
        familyMembers.add(member);
        
        // Adjust price based on number of members
        updatePriceBasedOnMembers();
        
        return true;
    }
    
    /**
     * Remove a family member
     */
    public boolean removeFamilyMember(String name) {
        FamilyMember member = findFamilyMember(name);
        if (member == null) return false;
        
        familyMembers.remove(member);
        updatePriceBasedOnMembers();
        return true;
    }
    
    /**
     * Find a family member by name
     */
    public FamilyMember findFamilyMember(String name) {
        return familyMembers.stream()
            .filter(m -> m.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Update price based on number of family members
     */
    private void updatePriceBasedOnMembers() {
        if (familyMembers.isEmpty()) {
            setPrice(BASE_PRICE);
            return;
        }
        
        // Base price plus discount for each additional member
        double basePrice = BASE_PRICE;
        int memberCount = familyMembers.size();
        
        // Calculate price: first member full price, others get discount
        double totalPrice = basePrice;
        for (int i = 1; i < memberCount; i++) {
            totalPrice += basePrice * (1 - ADDITIONAL_MEMBER_DISCOUNT);
        }
        
        setPrice(Math.round(totalPrice * 100.0) / 100.0);
    }
    
    /**
     * Get active family members count
     */
    public int getActiveFamilyMembersCount() {
        return (int) familyMembers.stream()
            .filter(FamilyMember::isActive)
            .count();
    }
    
    /**
     * Calculate fee with any applicable discounts
     */
    public double calculateDiscountedFee() {
        double finalPrice = getPrice();
        
        // Apply renewal discount if applicable
        if (isEligibleForRenewalDiscount()) {
            finalPrice = finalPrice * (1 - RENEWAL_DISCOUNT);
        }
        
        // Additional family discount for large families
        if (familyMembers.size() >= 3) {
            finalPrice = finalPrice * 0.95; // 5% extra discount for large families
        }
        
        return Math.round(finalPrice * 100.0) / 100.0;
    }
    
    /**
     * Check if eligible for renewal discount
     */
    private boolean isEligibleForRenewalDiscount() {
        long daysRemaining = getDaysRemaining();
        return daysRemaining >= 0 && daysRemaining <= 10 && isValid();
    }
    
    @Override
    public boolean renew() {
        if (isCancelled()) return false;
        
        boolean renewed = super.renew();
        
        if (renewed && isEligibleForRenewalDiscount()) {
            double discountedPrice = calculateDiscountedFee();
            setPrice(discountedPrice);
        }
        
        return renewed;
    }
    
    /**
     * Deactivate a family member
     */
    public boolean deactivateFamilyMember(String name) {
        FamilyMember member = findFamilyMember(name);
        if (member == null) return false;
        
        member.setActive(false);
        updatePriceBasedOnMembers();
        return true;
    }
    
    /**
     * Activate a family member
     */
    public boolean activateFamilyMember(String name) {
        FamilyMember member = findFamilyMember(name);
        if (member == null) return false;
        
        member.setActive(true);
        updatePriceBasedOnMembers();
        return true;
    }
    
    /**
     * Get benefits summary
     */
    public String getBenefits() {
        return String.format("""
            Family Membership Benefits:
            - Access to all gym facilities for up to %d family members
            - %d guest passes per month
            - Kids club access
            - Family events access
            - Family locker rooms
            - Mobile app with family features
            Current Members: %d
            Price: $%.2f/month
            """, MAX_FAMILY_MEMBERS, MAX_GUEST_PASSES, 
            familyMembers.size(), getPrice());
    }
    
    @Override
    public String toString() {
        return String.format("Family%s [Members: %d/%d, Guest Passes: %d/%d, Kids Club: Yes]",
            super.toString().substring(super.toString().indexOf("{")),
            familyMembers.size(), MAX_FAMILY_MEMBERS, 
            guestPassesUsed, MAX_GUEST_PASSES);
    }
}