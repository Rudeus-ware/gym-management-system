package com.gym.model.membership;

public class Premium extends Membership {
    
    // Premium-specific constants
    private static final String MEMBERSHIP_TYPE = "PREMIUM";
    private static final int DEFAULT_DURATION = 30; // 30 days
    private static final double BASE_PRICE = 59.99;
    private static final int MAX_GUEST_PASSES = 4;
    private static final double RENEWAL_DISCOUNT = 0.10; // 10% discount
    private static final int MAX_CLASS_BOOKINGS = 10;
    
    // Additional fields for Premium
    private int guestPassesUsed;
    private int classBookingsUsed;
    private boolean hasPersonalTrainerAccess;
    private boolean hasSaunaAccess;
    private boolean hasPoolAccess;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Compatibility constructor used by tests
     */
    public Premium(String membershipId, double price, String startDate, String endDate, String status, String benefits) {
        this(membershipId, null, price, 30, startDate, endDate, status);
    }

    /**
     * Full constructor
     */
    public Premium(String membershipId, String profileId, double price, int duration,
                   String startDate, String endDate, String status) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, price, duration);
        
        if (startDate != null) setStartDate(startDate);
        if (endDate != null) setEndDate(endDate);
        if (status != null) setStatus(status);
        
        this.guestPassesUsed = 0;
        this.classBookingsUsed = 0;
        this.hasPersonalTrainerAccess = true;
        this.hasSaunaAccess = true;
        this.hasPoolAccess = true;
    }
    
    /**
     * Standard constructor with default values
     */
    public Premium(String membershipId, String profileId) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, BASE_PRICE, DEFAULT_DURATION);
        this.guestPassesUsed = 0;
        this.classBookingsUsed = 0;
        this.hasPersonalTrainerAccess = true;
        this.hasSaunaAccess = true;
        this.hasPoolAccess = true;
    }
    
    /**
     * Constructor with custom price and duration
     */
    public Premium(String membershipId, String profileId, double price, int duration) {
        super(membershipId, profileId, MEMBERSHIP_TYPE, price, duration);
        this.guestPassesUsed = 0;
        this.classBookingsUsed = 0;
        this.hasPersonalTrainerAccess = true;
        this.hasSaunaAccess = true;
        this.hasPoolAccess = true;
    }
    
    // ============================================================
    // PREMIUM-SPECIFIC METHODS
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
    
    public int getMaxClassBookings() {
        return MAX_CLASS_BOOKINGS;
    }
    
    public int getClassBookingsUsed() {
        return classBookingsUsed;
    }
    
    public boolean hasPersonalTrainerAccess() {
        return hasPersonalTrainerAccess;
    }
    
    public boolean hasSaunaAccess() {
        return hasSaunaAccess;
    }
    
    public boolean hasPoolAccess() {
        return hasPoolAccess;
    }
    
    /**
     * Use a guest pass
     */
    public boolean useGuestPass() {
        if (!isValid()) return false;
        if (guestPassesUsed >= MAX_GUEST_PASSES) return false;
        
        guestPassesUsed++;
        return true;
    }
    
    /**
     * Book a class
     */
    public boolean bookClass() {
        if (!isValid()) return false;
        if (classBookingsUsed >= MAX_CLASS_BOOKINGS) return false;
        
        classBookingsUsed++;
        return true;
    }
    
    /**
     * Reset monthly usage (called at start of each month)
     */
    public void resetMonthlyUsage() {
        guestPassesUsed = 0;
        classBookingsUsed = 0;
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
        
        // Additional discount for annual commitment
        if (getDuration() >= 365) {
            finalPrice = finalPrice * 0.85; // 15% discount for annual
        }
        
        return Math.round(finalPrice * 100.0) / 100.0;
    }
    
    /**
     * Check if eligible for renewal discount
     */
    private boolean isEligibleForRenewalDiscount() {
        long daysRemaining = getDaysRemaining();
        return daysRemaining >= 0 && daysRemaining <= 14 && isValid();
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
     * Downgrade to Basic membership
     */
    public Basic downgradeToBasic() {
        if (!isValid()) return null;
        
        Basic basic = new Basic(getMembershipId(), getProfileId());
        basic.setStartDate(getStartDate());
        basic.setEndDate(getEndDate());
        basic.setStatus(getStatus());
        
        return basic;
    }
    
    /**
     * Get benefits summary
     */
    public String getBenefits() {
        return String.format("""
            Premium Membership Benefits:
            - Access to all gym facilities
            - %d guest passes per month
            - %d group class bookings per month
            - Personal trainer access
            - Sauna & pool access
            - Priority locker access
            - Mobile app with advanced features
            Price: $%.2f/month
            """, MAX_GUEST_PASSES, MAX_CLASS_BOOKINGS, getPrice());
    }
    
    @Override
    public String toString() {
        return String.format("Premium%s [Guest Passes: %d/%d, Class Bookings: %d/%d, Personal Trainer: Yes, Sauna: Yes, Pool: Yes]",
            super.toString().substring(super.toString().indexOf("{")),
            guestPassesUsed, MAX_GUEST_PASSES, classBookingsUsed, MAX_CLASS_BOOKINGS);
    }
}