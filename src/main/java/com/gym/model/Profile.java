package com.gym.model;

import com.gym.model.membership.Membership;

public class Profile {  // ← NOT abstract - can be instantiated
    private int profileId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Membership membership; // Association: A Profile can have a Membership (composition)
    
    // Constructor
    public Profile(int profileId, String name, String email, String phone, String address) {
        this.profileId = profileId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.membership = null; // No membership initially
    }
    
    // GETTERS
    public int getProfileId() { return profileId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Membership getMembership() { return membership; }
    public void setMembership(Membership membership) { this.membership = membership; }
    
    
    // SETTERS
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    
    // BUSINESS METHODS
    public void updateProfile(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    public String viewProfile() {
        String profileInfo = "Profile ID: " + profileId + 
                             "\nName: " + name + 
                             "\nEmail: " + email + 
                             "\nPhone: " + phone + 
                             "\nAddress: " + address;
        
        if (membership != null) {
            profileInfo += "\n\n--- MEMBERSHIP INFO ---\n" + membership.getMembershipDetails();
        } else {
            profileInfo += "\n\n⚠️ No active membership";
        }
        
        return profileInfo;
    }
}


