package com.gym.model.membership;

public abstract class Membership {
    
    protected String membershipId;   // Changed from int to String
    protected double fee;
    protected String startDate;
    protected String expiryDate;
    protected String status;

    public Membership(String membershipId, double fee, String startDate, String expiryDate, String status) {
        this.membershipId = membershipId;
        this.fee = fee;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    // Getters
    public String getMembershipId() { return membershipId; }
    public double getFee() { return fee; }
    public String getStartDate() { return startDate; }
    public String getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }

    // Setters
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }
    public void setFee(double fee) { this.fee = fee; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public void setStatus(String status) { this.status = status; }

    // Abstract methods
    public abstract void renew();
    public abstract double calculateFee();
    public abstract boolean isValid();

    public String getMembershipDetails() {
        return "Membership ID: " + membershipId +
               "\nType: " + this.getClass().getSimpleName() +
               "\nFee: $" + fee +
               "\nStart Date: " + startDate +
               "\nExpiry Date: " + expiryDate +
               "\nStatus: " + status;
    }
}