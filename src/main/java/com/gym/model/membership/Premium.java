package com.gym.model.membership;

public class Premium extends Membership {
    
    private String benefits;

    public Premium(String membershipId, double fee, String startDate, String expiryDate, 
                   String status, String benefits) {
        super(membershipId, fee, startDate, expiryDate, status);
        this.benefits = benefits;
    }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    @Override
    public void renew() {
        // Implementation
    }

    @Override
    public double calculateFee() {
        return this.fee;
    }

    @Override
    public boolean isValid() {
        return "Active".equalsIgnoreCase(status) && 
               java.time.LocalDate.parse(expiryDate).isAfter(java.time.LocalDate.now());
    }
}