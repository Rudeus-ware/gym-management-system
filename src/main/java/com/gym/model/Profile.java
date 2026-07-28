package com.gym.model;

import java.time.LocalDate;

public class Profile {
    
    // ✅ ONLY these fields belong in Profile
    private String profileId;
    private String name;
    protected String email;
    private String phone;
    private String membershipType;
    private String status;
    private String joinDate;
    private String address;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    public Profile(String profileId, String name, String email, String phone, String membershipType) {
        this.profileId = profileId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membershipType = membershipType != null ? membershipType : "NONE";
        this.status = "ACTIVE";
        this.joinDate = LocalDate.now().toString();
        this.address = ""; 
    }
    
    public Profile(String profileId, String name, String email, String phone, 
                   String membershipType, String status) {
        this.profileId = profileId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membershipType = membershipType != null ? membershipType : "NONE";
        this.status = status != null ? status : "ACTIVE";
        this.joinDate = LocalDate.now().toString();
    }
    
    public Profile(String profileId, String name, String email, String phone, 
                   String membershipType, String status, String joinDate) {
        this.profileId = profileId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.membershipType = membershipType != null ? membershipType : "NONE";
        this.status = status != null ? status : "ACTIVE";
        this.joinDate = joinDate != null ? joinDate : LocalDate.now().toString();
    }
    
    // ============================================================
    // GETTERS - ✅ ONLY these belong in Profile
    // ============================================================
    
    public String getProfileId() { return profileId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMembershipType() { return membershipType; }
    public String getStatus() { return status; }
    public String getJoinDate() { return joinDate; }
    public String getAddress() { return address; }
    
    // ✅ CORRECT - uses status field
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
    
    // ============================================================
    // SETTERS - ✅ ONLY these belong in Profile
    // ============================================================
    
    public void setProfileId(String profileId) { this.profileId = profileId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }
    public void setStatus(String status) { this.status = status; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }
    
    // ✅ CORRECT - converts boolean to String
    public void setActive(boolean active) {
        this.status = active ? "ACTIVE" : "INACTIVE";
    }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public void activate() {
        this.status = "ACTIVE";
    }
    
    public void deactivate() {
        this.status = "INACTIVE";
    }
    
    public String getStatusDisplay() {
        return isActive() ? "✅ Active" : "❌ Inactive";
    }
    
    @Override
    public String toString() {
        return String.format("Profile{id='%s', name='%s', email='%s', membership='%s', status='%s'}",
            profileId, name, email, membershipType, status);
    }

    public String viewProfile() {
    StringBuilder sb = new StringBuilder();
    sb.append("Profile ID: ").append(profileId).append("\n");
    sb.append("Name: ").append(name).append("\n");
    sb.append("Email: ").append(email).append("\n");
    sb.append("Phone: ").append(phone != null ? phone : "N/A").append("\n");
    sb.append("Address: ").append(address != null ? address : "N/A").append("\n");
    sb.append("Status: ").append(isActive() ? "ACTIVE" : "INACTIVE");
    return sb.toString();
}
}

