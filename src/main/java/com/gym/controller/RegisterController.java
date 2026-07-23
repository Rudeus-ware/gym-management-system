package com.gym.controller;

import java.time.LocalDate;

import com.gym.model.Profile;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Premium;
import com.gym.persistence.DataManager;

/**
 * RegisterController - Business Logic for Registration
 * 
 * Handles new user registration business logic.
 * Note: This is OPTIONAL - AdminController can handle this.
 */
public class RegisterController {
    
    private DataManager dataManager;
    
    public RegisterController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    /**
     * Register a new member
     * 
     * @param name Member's full name
     * @param email Member's email address
     * @param phone Member's phone number
     * @param address Member's address
     * @param membershipType Type of membership (Basic, Premium, Family)
     * @return The created Profile, or null if registration failed
     */
    public Profile registerMember(String name, String email, String phone, 
                                  String address, String membershipType) {
        // Validate input
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            System.out.println("❌ Registration failed: Name and email required");
            return null;
        }
        
        // Check if email already exists
        for (Profile p : dataManager.getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email.trim())) {
                System.out.println("❌ Registration failed: Email already exists");
                return null;
            }
        }
        
        // Create profile
        int profileId = dataManager.getProfiles().size() + 1;
        Profile profile = new Profile(profileId, name.trim(), email.trim(), 
                                      phone.trim(), address.trim());
        profile.setActive(true);
        
        // Assign membership
        String membership = membershipType != null ? membershipType : "Basic";
        int membershipId = dataManager.getMemberships().size() + 1000;
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusMonths(1).toString();
        
        switch (membership) {
            case "Premium":
                Premium premium = new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
                profile.setMembership(premium);
                dataManager.addMembership(premium);
                break;
            case "Family":
                Family family = new Family(membershipId, 69.99, startDate, expiryDate, "Active", 2);
                profile.setMembership(family);
                dataManager.addMembership(family);
                break;
            default:
                Basic basic = new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
                profile.setMembership(basic);
                dataManager.addMembership(basic);
                break;
        }
        
        // Save
        dataManager.addProfile(profile);
        dataManager.saveAllData();
        
        System.out.println("✅ Registration successful: " + profile.getName());
        return profile;
    }
    
    /**
     * Check if an email is already registered
     */
    public boolean isEmailRegistered(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        for (Profile p : dataManager.getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email.trim())) {
                return true;
            }
        }
        return false;
    }
}