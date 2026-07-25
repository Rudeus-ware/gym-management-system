package com.gym.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.gym.model.Profile;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Premium;
import com.gym.database.DatabaseManager;
import com.gym.util.IdGenerator;

/**
 * RegisterController - Business Logic for Registration
 * 
 * Handles new user registration business logic.
 * Updated to use String-based IDs
 */
public class RegisterController {
    
    private DatabaseManager dataManager;
    private IdGenerator idGenerator;
    
    public RegisterController(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        this.idGenerator = new IdGenerator(null); // Will be initialized properly
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
        
        // Trim inputs
        String trimmedName = name.trim();
        String trimmedEmail = email.trim();
        String trimmedPhone = phone != null ? phone.trim() : "";
        String trimmedAddress = address != null ? address.trim() : "";
        
        // Check if email already exists
        if (isEmailRegistered(trimmedEmail)) {
            System.out.println("❌ Registration failed: Email already exists: " + trimmedEmail);
            return null;
        }
        
        // ✅ Generate String profile ID (Member role = "22")
        String profileId = generateProfileId("22");
        
        // Create profile with String ID
        Profile profile = new Profile(profileId, trimmedName, trimmedEmail, 
                                      trimmedPhone, trimmedAddress);
        profile.setActive(true);
        
        // Assign membership
        String membership = membershipType != null ? membershipType : "Basic";
        String membershipId = generateMembershipId(membership);
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusMonths(1).toString();
        
        // Create membership based on type
        switch (membership) {
            case "Premium":
                Premium premium = new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
                profile.setMembership(premium);
                databaseManager.addMembership(premium);
                System.out.println("   Membership: Premium (ID: " + membershipId + ")");
                break;
            case "Family":
                Family family = new Family(membershipId, 69.99, startDate, expiryDate, "Active", 2);
                profile.setMembership(family);
                databaseManager.addMembership(family);
                System.out.println("   Membership: Family (ID: " + membershipId + ")");
                break;
            default:
                Basic basic = new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
                profile.setMembership(basic);
                databaseManager.addMembership(basic);
                System.out.println("   Membership: Basic (ID: " + membershipId + ")");
                break;
        }
        
        // Save profile
        databaseManager.addProfile(profile);
        databaseManager.saveAllData();
        
        System.out.println("✅ Registration successful!");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Name: " + profile.getName());
        System.out.println("   Email: " + profile.getEmail());
        
        return profile;
    }
    
    /**
     * Register a new member with specific role
     * 
     * @param name Member's full name
     * @param email Member's email address
     * @param phone Member's phone number
     * @param address Member's address
     * @param roleCode Role code: "00"=Admin, "11"=Trainer, "22"=Member
     * @param membershipType Type of membership (Basic, Premium, Family)
     * @return The created Profile, or null if registration failed
     */
    public Profile registerMember(String name, String email, String phone, 
                                  String address, String roleCode, String membershipType) {
        // Validate role code
        if (roleCode == null || (!roleCode.equals("00") && !roleCode.equals("11") && !roleCode.equals("22"))) {
            System.out.println("❌ Invalid role code. Use: 00=Admin, 11=Trainer, 22=Member");
            return null;
        }
        
        // Validate input
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            System.out.println("❌ Registration failed: Name and email required");
            return null;
        }
        
        String trimmedName = name.trim();
        String trimmedEmail = email.trim();
        String trimmedPhone = phone != null ? phone.trim() : "";
        String trimmedAddress = address != null ? address.trim() : "";
        
        // Check if email already exists
        if (isEmailRegistered(trimmedEmail)) {
            System.out.println("❌ Registration failed: Email already exists: " + trimmedEmail);
            return null;
        }
        
        // ✅ Generate profile ID with role
        String profileId = generateProfileId(roleCode);
        
        Profile profile = new Profile(profileId, trimmedName, trimmedEmail, 
                                      trimmedPhone, trimmedAddress);
        profile.setActive(true);
        
        // Assign membership
        String membership = membershipType != null ? membershipType : "Basic";
        String membershipId = generateMembershipId(membership);
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusMonths(1).toString();
        
        switch (membership) {
            case "Premium":
                Premium premium = new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
                profile.setMembership(premium);
                databaseManager.addMembership(premium);
                break;
            case "Family":
                Family family = new Family(membershipId, 69.99, startDate, expiryDate, "Active", 2);
                profile.setMembership(family);
                databaseManager.addMembership(family);
                break;
            default:
                Basic basic = new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
                profile.setMembership(basic);
                databaseManager.addMembership(basic);
                break;
        }
        
        databaseManager.addProfile(profile);
        databaseManager.saveAllData();
        
        System.out.println("✅ Registration successful!");
        System.out.println("   Profile ID: " + profileId + " (Role: " + getRoleName(roleCode) + ")");
        System.out.println("   Name: " + profile.getName());
        System.out.println("   Email: " + profile.getEmail());
        
        return profile;
    }
    
    /**
     * Check if an email is already registered
     */
    public boolean isEmailRegistered(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String trimmedEmail = email.trim();
        for (Profile p : databaseManager.getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(trimmedEmail)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get role name from role code
     */
    private String getRoleName(String roleCode) {
        switch (roleCode) {
            case "00": return "Admin";
            case "11": return "Trainer";
            case "22": return "Member";
            default: return "Unknown";
        }
    }
    
    /**
     * Generate a unique profile ID
     */
    private String generateProfileId(String roleCode) {
        if (idGenerator != null) {
            return idGenerator.generateProfileId(roleCode, LocalDate.now());
        }
        // Fallback ID generation
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd"));
        int count = databaseManager.getProfiles().size() + 1;
        return String.format("%03d", count) + timestamp + roleCode;
    }
    
    /**
     * Generate a unique membership ID
     */
    private String generateMembershipId(String type) {
        String prefix = type.substring(0, 3).toUpperCase();
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = databaseManager.getMemberships().size() + 1;
        return prefix + timestamp + String.format("%04d", count);
    }
}