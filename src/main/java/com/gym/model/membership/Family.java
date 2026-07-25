package com.gym.model.membership;

public class Family extends Membership {
    
    private int numberOfMembers;

    public Family(String membershipId, double fee, String startDate, String expiryDate, 
                  String status, int numberOfMembers) {
        super(membershipId, fee, startDate, expiryDate, status);
        this.numberOfMembers = numberOfMembers;
    }

    public int getNumberOfMembers() { return numberOfMembers; }
    public void setNumberOfMembers(int numberOfMembers) { this.numberOfMembers = numberOfMembers; }

    @Override
    public void renew() {
        // Implementation
    }

    @Override
    public double calculateFee() {
        return this.fee * numberOfMembers;
    }

    @Override
    public boolean isValid() {
        return "Active".equalsIgnoreCase(status) && 
               java.time.LocalDate.parse(expiryDate).isAfter(java.time.LocalDate.now());
    }
}