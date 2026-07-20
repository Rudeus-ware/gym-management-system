package com.gym.model.membership;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Family extends Membership {
    private int numberOfMembers;
    private List<String> familyMembers;
    
    // Default constructor
    public Family() {
        this(3000, 69.99, "2026-01-01", "2026-12-31", "Active", 4);
    }

    // Constructor
    public Family(int membershipId, double fee, String startDate, String expiryDate, 
                  String status, int numberOfMembers) {
        super(membershipId, fee, startDate, expiryDate, status);
        this.numberOfMembers = numberOfMembers;
        this.familyMembers = new ArrayList<>();
        // Add default family member slots
        initializeFamilyMembers();
    }
    
    private void initializeFamilyMembers() {
        // Add placeholder names for family members
        for (int i = 1; i <= numberOfMembers; i++) {
            familyMembers.add("Family Member " + i);
        }
    }
    
    // GETTERS & SETTERS
    public int getNumberOfMembers() { return numberOfMembers; }
    public void setNumberOfMembers(int numberOfMembers) { 
        this.numberOfMembers = numberOfMembers; 
    }
    
    public List<String> getFamilyMembers() {
        return new ArrayList<>(familyMembers);
    }
    
    // IMPLEMENT ABSTRACT METHODS
    @Override
    public void renew() {
        LocalDate currentExpiry = LocalDate.parse(expiryDate);
        LocalDate newExpiry = currentExpiry.plusYears(1);
        this.expiryDate = newExpiry.toString();
        this.status = "Active";
        System.out.println("✅ Family membership renewed until: " + expiryDate);
        System.out.println("   All " + numberOfMembers + " family members covered!");
    }
    
    @Override
    public double calculateFee() {
        // Family fee is per member with a discount for groups
        double baseFee = this.fee;
        double discountPerMember = 10.0; // $10 discount per additional member
        
        if (numberOfMembers > 1) {
            // Family discount: (fee * numberOfMembers) - discount
            double totalFee = (baseFee * numberOfMembers) - (discountPerMember * (numberOfMembers - 1));
            return totalFee;
        }
        
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
    
    // FAMILY-SPECIFIC METHODS
    public void addFamilyMember(String memberName) {
        familyMembers.add(memberName);
        numberOfMembers++;
        System.out.println("👨‍👩‍👧‍👦 Family member added: " + memberName);
        System.out.println("   Total family members: " + numberOfMembers);
    }
    
    public void removeFamilyMember(String memberName) {
        if (familyMembers.remove(memberName)) {
            numberOfMembers--;
            System.out.println("🗑️ Family member removed: " + memberName);
            System.out.println("   Remaining family members: " + numberOfMembers);
        } else {
            System.out.println("⚠️ Member not found: " + memberName);
        }
    }
    
    public void displayFamilyMembers() {
        System.out.println("👨‍👩‍👧‍👦 Family Members (" + numberOfMembers + " total):");
        for (String member : familyMembers) {
            System.out.println("   • " + member);
        }
    }
    
    @Override
    public String getMembershipDetails() {
        return super.getMembershipDetails() +
               "\nPlan: Family Membership" +
               "\nNumber of Members: " + numberOfMembers +
               "\nFamily Members: " + familyMembers +
               "\n- Cost-effective for families" +
               "\n- All members share the same benefits" +
               "\n- Add or remove members anytime";
    }
}