package com.gym.model;

import com.gym.model.membership.Membership;

public class Profile {
    private String profileId;   // ✅ Changed from int to String
    private String name;
    private String email;
    private String phone;
    private String address;
    private Membership membership;
    private boolean isActive;
    
    public Profile(String profileId, String name, String email, String phone, String address) {
        this.profileId = profileId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isActive = true;
    }
    
    // ===== GETTERS & SETTERS =====
    public String getProfileId() { return profileId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Membership getMembership() { return membership; }
    public void setMembership(Membership membership) { this.membership = membership; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    // ===== BUSINESS METHODS =====
    public void updateProfile(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    public String viewProfile() {
        String info = "Profile ID: " + profileId + 
                      "\nName: " + name + 
                      "\nEmail: " + email + 
                      "\nPhone: " + phone + 
                      "\nAddress: " + address +
                      "\nStatus: " + (isActive ? "Active" : "Inactive");
        
        if (membership != null) {
            info += "\n\n--- MEMBERSHIP INFO ---\n" + membership.getMembershipDetails();
        }
        return info;
    }
}