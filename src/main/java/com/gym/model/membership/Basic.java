package com.gym.model.membership;

import java.time.LocalDate;

public class Basic extends Membership {
    
    // Default constructor
    public Basic() {
        this(1000, 49.99, "2026-01-01", "2026-12-31", "Active");
    }

    // Constructor
    public Basic(int membershipId, double fee, String startDate, String expiryDate, String status) {
        super(membershipId, fee, startDate, expiryDate, status);
    }
    
    // IMPLEMENT ABSTRACT METHODS
    @Override
    public void renew() {
        // Extend membership by 1 year from current expiry date
        LocalDate currentExpiry = LocalDate.parse(expiryDate);
        LocalDate newExpiry = currentExpiry.plusYears(1);
        this.expiryDate = newExpiry.toString();
        this.status = "Active";
        System.out.println("✅ Basic membership renewed until: " + expiryDate);
    }
    
    @Override
    public double calculateFee() {
        // Basic membership has a flat rate
        return this.fee;
    }
    
    @Override
    public boolean isValid() {
        // Check if membership is active and not expired
        if (!status.equalsIgnoreCase("Active")) {
            return false;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate expiry = LocalDate.parse(expiryDate);
        
        return today.isBefore(expiry) || today.isEqual(expiry);
    }
    
    // BASIC-SPECIFIC METHODS
    public void upgradeToPremium() {
        System.out.println("🔄 Upgrading from Basic to Premium...");
        System.out.println("   Additional benefits will be added!");
        // In a real system, this would create a Premium membership
    }
    
    @Override
    public String getMembershipDetails() {
        return super.getMembershipDetails() +
               "\nPlan: Basic Membership" +
               "\n- Access to standard gym facilities" +
               "\n- No guest passes" +
               "\n- Standard class access";
    }
}