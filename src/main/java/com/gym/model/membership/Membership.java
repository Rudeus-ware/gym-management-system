package com.gym.model.membership;

public abstract class Membership {
    // Common attributes for ALL membership types
    protected int membershipId;
    protected double fee;
    protected String startDate;
    protected String expiryDate;
    protected String status;
    
    // Constructor
    public Membership(int membershipId, double fee, String startDate, String expiryDate, String status) {
        this.membershipId = membershipId;
        this.fee = fee;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }
    
    // GETTERS
    public int getMembershipId() { return membershipId; }
    public double getFee() { return fee; }
    public String getStartDate() { return startDate; }
    public String getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
    public String getName() { return this.getClass().getSimpleName(); }
    public double getPrice() { return fee; }
    
    // SETTERS
    public void setMembershipId(int membershipId) { this.membershipId = membershipId; }
    public void setFee(double fee) { this.fee = fee; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public void setStatus(String status) { this.status = status; }
    
    // ABSTRACT METHODS (must be implemented by subclasses)
    public abstract void renew();
    public abstract double calculateFee();
    public abstract boolean isValid();
    
    // CONCRETE METHOD (shared by all)
    public String getMembershipDetails() {
        return "Membership ID: " + membershipId +
               "\nType: " + this.getClass().getSimpleName() +
               "\nFee: $" + fee +
               "\nStart Date: " + startDate +
               "\nExpiry Date: " + expiryDate +
               "\nStatus: " + status;
    }
}