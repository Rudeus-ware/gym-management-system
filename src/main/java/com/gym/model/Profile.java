package com.gym.model;

import com.gym.model.membership.Membership;

public class Profile {
    private int profileId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Membership membership;
    private String registrationDate;
    private boolean isActive;  // ✅ NEW: Auto-activate on registration
    
    public Profile(int profileId, String name, String email, String phone, String address) {
        this.profileId = profileId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.membership = null;
        this.registrationDate = java.time.LocalDate.now().toString();
        this.isActive = true;  // ✅ NEW: Auto-activate
    }
    
    // ===== GETTERS & SETTERS =====
    public int getProfileId() { return profileId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Membership getMembership() { return membership; }
    public String getRegistrationDate() { return registrationDate; }
    public boolean isActive() { return isActive; }
    
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setMembership(Membership membership) { this.membership = membership; }
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
                      "\nRegistered: " + registrationDate +
                      "\nStatus: " + (isActive ? "✅ Active" : "❌ Inactive");
        
        if (membership != null) {
            info += "\n\n--- MEMBERSHIP INFO ---\n" + membership.getMembershipDetails();
        }
        
        return info;
    }
}