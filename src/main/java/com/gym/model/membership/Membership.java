package com.gym.model.membership;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Membership {
    
    private String membershipId;
    private String profileId;  // ← This field must exist
    private String type;
    private double price;
    private int duration;
    private String startDate;
    private String endDate;
    private String status;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    public Membership(String membershipId, String profileId, String type, double price, int duration) {
        this.membershipId = membershipId;
        this.profileId = profileId;  // ← profileId is stored here
        this.type = type;
        this.price = price;
        this.duration = duration;
        this.startDate = LocalDate.now().toString();
        this.endDate = LocalDate.now().plusDays(duration).toString();
        this.status = "ACTIVE";
    }
    
    // ============================================================
    // GETTERS - ADD getProfileId() HERE
    // ============================================================
    
    public String getMembershipId() { 
        return membershipId; 
    }
    
    /**
     * Get the profile ID associated with this membership
     * ADD THIS METHOD
     */
    public String getProfileId() {  // ← ADD THIS METHOD
        return profileId;
    }
    
    public String getType() { 
        return type; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public int getDuration() { 
        return duration; 
    }
    
    public String getStartDate() { 
        return startDate; 
    }
    
    public String getEndDate() { 
        return endDate; 
    }
    
    public String getStatus() { 
        return status; 
    }
    public String getMembershipDetails() {
    return String.format("Membership{id='%s', type='%s', price=%.2f, status='%s', endDate='%s'}",
        membershipId, type, price, status, endDate);
}
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    public void setMembershipId(String membershipId) { 
        this.membershipId = membershipId; 
    }
    
    public void setProfileId(String profileId) { 
        this.profileId = profileId; 
    }
    
    public void setType(String type) { 
        this.type = type; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    public void setDuration(int duration) { 
        this.duration = duration;
        if (startDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            this.endDate = start.plusDays(duration).toString();
        }
    }
    
    public void setStartDate(String startDate) { 
        this.startDate = startDate;
        if (duration > 0) {
            LocalDate start = LocalDate.parse(startDate);
            this.endDate = start.plusDays(duration).toString();
        }
    }
    
    public void setEndDate(String endDate) { 
        this.endDate = endDate; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
    
    public boolean isExpired() {
        return "EXPIRED".equalsIgnoreCase(status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }
    
    public boolean isValid() {
        if (!isActive()) return false;
        try {
            LocalDate end = LocalDate.parse(endDate);
            return end.isAfter(LocalDate.now()) || end.isEqual(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    public long getDaysRemaining() {
        if (!isActive()) return 0;
        try {
            LocalDate end = LocalDate.parse(endDate);
            LocalDate now = LocalDate.now();
            return java.time.temporal.ChronoUnit.DAYS.between(now, end);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public boolean renew() {
        if (isCancelled()) return false;
        try {
            LocalDate currentEnd = LocalDate.parse(endDate);
            LocalDate newEnd = currentEnd.plusDays(duration);
            this.endDate = newEnd.toString();
            this.status = "ACTIVE";
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean cancel() {
        if (isCancelled() || isExpired()) return false;
        this.status = "CANCELLED";
        return true;
    }
    
    public boolean checkExpired() {
        if (!isActive()) return false;
        try {
            LocalDate end = LocalDate.parse(endDate);
            if (end.isBefore(LocalDate.now())) {
                this.status = "EXPIRED";
                return true;
            }
        } catch (Exception e) {
            // Ignore
        }
        return false;
    }
    
    public String getFormattedStartDate() {
        try {
            LocalDate date = LocalDate.parse(startDate);
            return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return startDate;
        }
    }
    
    public String getFormattedEndDate() {
        try {
            LocalDate date = LocalDate.parse(endDate);
            return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return endDate;
        }
    }
    
    public String getStatusDisplay() {
        return switch (status) {
            case "ACTIVE" -> "✅ Active";
            case "EXPIRED" -> "⏰ Expired";
            case "CANCELLED" -> "❌ Cancelled";
            default -> "❓ " + status;
        };
    }
    
    public String getTypeDisplay() {
        return switch (type.toUpperCase()) {
            case "BASIC" -> "📘 Basic";
            case "PREMIUM" -> "📗 Premium";
            case "FAMILY" -> "📕 Family";
            default -> "❓ " + type;
        };
    }
    
    @Override
    public String toString() {
        return String.format("Membership{id='%s', profile='%s', type='%s', status='%s', endDate='%s'}",
            membershipId, profileId, type, status, endDate);
    }
}