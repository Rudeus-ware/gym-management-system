package com.gym.model.membership;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Premium extends Membership {
    private String benefits;
    private List<String> extraBenefits;
    
    // Default constructor
    public Premium() {
        this(2000, 99.99, "2026-01-01", "2026-12-31", "Active", "VIP Access");
    }

    // Constructor
    public Premium(int membershipId, double fee, String startDate, String expiryDate, 
                   String status, String benefits) {
        super(membershipId, fee, startDate, expiryDate, status);
        this.benefits = benefits;
        this.extraBenefits = new ArrayList<>();
        // Add default benefits
        this.extraBenefits.add("Free guest passes (2 per month)");
        this.extraBenefits.add("Access to premium classes");
        this.extraBenefits.add("Free towel service");
        this.extraBenefits.add("Priority booking");
    }
    
    // GETTERS & SETTERS
    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    
    public List<String> getExtraBenefits() { 
        return new ArrayList<>(extraBenefits); 
    }
    
    public void addBenefit(String benefit) {
        extraBenefits.add(benefit);
        System.out.println("✨ New benefit added: " + benefit);
    }
    
    // IMPLEMENT ABSTRACT METHODS
    @Override
    public void renew() {
        LocalDate currentExpiry = LocalDate.parse(expiryDate);
        LocalDate newExpiry = currentExpiry.plusYears(1);
        this.expiryDate = newExpiry.toString();
        this.status = "Active";
        System.out.println("✅ Premium membership renewed until: " + expiryDate);
        System.out.println("   All premium benefits retained!");
    }
    
    @Override
    public double calculateFee() {
        // Premium has a higher fee with possible discounts
        double baseFee = this.fee;
        
        // Could add logic for loyalty discounts
        // For now, return the base fee
        return baseFee;
    }
    
    @Override
    public boolean isValid() {
        if (!status.equalsIgnoreCase("Active")) {
            return false;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate expiry = LocalDate.parse(expiryDate);
        
        return today.isBefore(expiry) || today.isEqual(expiry);
    }
    
    // PREMIUM-SPECIFIC METHODS
    public void displayBenefits() {
        System.out.println("🎁 Premium Benefits:");
        for (String benefit : extraBenefits) {
            System.out.println("   • " + benefit);
        }
    }
    
    public void downgradeToBasic() {
        System.out.println("🔄 Downgrading from Premium to Basic...");
        System.out.println("   Premium benefits will be removed!");
    }
    
    @Override
    public String getMembershipDetails() {
        return super.getMembershipDetails() +
               "\nPlan: Premium Membership" +
               "\nBenefits: " + benefits +
               "\nExtra Benefits: " + extraBenefits +
               "\n- Guest passes included" +
               "\n- Premium class access" +
               "\n- Priority booking";
    }
}