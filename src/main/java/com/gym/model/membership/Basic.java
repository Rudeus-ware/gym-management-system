package com.gym.model.membership;

public class Basic extends Membership {
    
    // Basic-specific constants
    private static final String MEMBERSHIP_TYPE = "BASIC";
    private static final int DEFAULT_DURATION = 30; // 30 days
    private static final double BASE_PRICE = 29.99;
    private static final int MAX_GUEST_PASSES = 0;
    private static final double RENEWAL_DISCOUNT = 0.05; // 5% discount
    
    // Additional fields for Basic
    private int guestPassesUsed;
    private boolean hasAccessToClasses;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    public Basic() {
        this("MEM-DEFAULT", null);
    }

    /**
     * Compatibility constructor used by tests
     */
    public Basic(String membershipId, double price, String startDate, String endDate, String status) {
        this(membershipId, null, price, 30, startDate, endDate, status);
    }

    /**
     * Full constructor
     */
    public Basic(String membershipId, String profileId, double price, int duration, 
                 String startDate, String endDate, String status) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, price, duration);
        // Override dates and status if provided
        if (startDate != null) setStartDate(startDate);
        if (endDate != null) setEndDate(endDate);
        if (status != null) setStatus(status);
        
        this.guestPassesUsed = 0;
        this.hasAccessToClasses = false; // Basic members don't get class access
    }
    
    /**
     * Standard constructor with default duration
     */
    public Basic(String membershipId, String profileId) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, BASE_PRICE, DEFAULT_DURATION);
        this.guestPassesUsed = 0;
        this.hasAccessToClasses = false;
    }
    
    /**
     * Constructor with custom price and duration
     */
    public Basic(String membershipId, String profileId, double price, int duration) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, price, duration);
        this.guestPassesUsed = 0;
        this.hasAccessToClasses = false;
    }
    
    // ============================================================
    // BASIC-SPECIFIC METHODS
    // ============================================================
    
    /**
     * Get the membership type
     */
    public String getMembershipType() {
        return MEMBERSHIP_TYPE;
    }
    
    /**
     * Get maximum guest passes allowed
     */
    public int getMaxGuestPasses() {
        return MAX_GUEST_PASSES;
    }
    
    /**
     * Get guest passes used
     */
    public int getGuestPassesUsed() {
        return guestPassesUsed;
    }
    
    /**
     * Check if member has access to classes
     */
    public boolean hasAccessToClasses() {
        return hasAccessToClasses;
    }
    
    /**
     * Check if member can upgrade to Premium
     */
    public boolean canUpgradeToPremium() {
        return isValid() && !isExpired() && !isCancelled();
    }
    
    /**
     * Check if member can upgrade to Family
     */
    public boolean canUpgradeToFamily() {
        return isValid() && !isExpired() && !isCancelled();
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
        
        return Math.round(finalPrice * 100.0) / 100.0;
    }
    
    /**
     * Check if eligible for renewal discount
     */
    private boolean isEligibleForRenewalDiscount() {
        // Check if membership is close to expiry (within 7 days)
        long daysRemaining = getDaysRemaining();
        return daysRemaining >= 0 && daysRemaining <= 7 && isValid();
    }
    
    /**
     * Renew with discount if eligible
     */
    @Override
    public boolean renew() {
        if (isCancelled()) return false;
        
        // Use the parent renew method
        boolean renewed = super.renew();
        
        // Apply discount if eligible
        if (renewed && isEligibleForRenewalDiscount()) {
            double discountedPrice = calculateDiscountedFee();
            setPrice(discountedPrice);
        }
        
        return renewed;
    }
    
    /**
     * Check membership benefits
     */
    public String getBenefits() {
        return String.format("""
            Basic Membership Benefits:
            - Access to gym facilities
            - %d guest passes per month
            - No group class access
            - Standard locker access
            - Mobile app access
            Price: $%.2f/month
            """, MAX_GUEST_PASSES, getPrice());
    }
    
    @Override
    public String toString() {
        return String.format("Basic%s [Guest Passes Used: %d/%d, Classes: No]",
            super.toString().substring(super.toString().indexOf("{")),
            guestPassesUsed, MAX_GUEST_PASSES);
    }
}