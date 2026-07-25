package com.gym.model.membership;

public class Basic extends Membership {

    public Basic(String membershipId, double fee, String startDate, String expiryDate, String status) {
        super(membershipId, fee, startDate, expiryDate, status);
    }

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