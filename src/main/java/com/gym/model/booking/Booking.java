package com.gym.model.booking;

public class Booking {
    private String bookingId;
    private String profileId;
    private String sessionId;
    private String bookingDate;
    private String status;
    private boolean active;
    private boolean cancelled;
    private boolean completed;
    
    // Constructor - MUST MATCH THE CALL
    public Booking(String bookingId, String profileId, String sessionId, 
                   String bookingDate, String status) {
        this.bookingId = bookingId;
        this.profileId = profileId;
        this.sessionId = sessionId;
        this.bookingDate = bookingDate;
        this.status = status != null ? status : "ACTIVE";
        this.active = "ACTIVE".equalsIgnoreCase(this.status);
        this.cancelled = "CANCELLED".equalsIgnoreCase(this.status);
        this.completed = "COMPLETED".equalsIgnoreCase(this.status);
    }
    
    // GETTERS
    public String getBookingId() { return bookingId; }
    public String getProfileId() { return profileId; }
    public String getSessionId() { return sessionId; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    public boolean isActive() { return active; }
    public boolean isCancelled() { return cancelled; }
    public boolean isCompleted() { return completed; }
    
    // SETTERS
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public void setStatus(String status) { 
        this.status = status;
        this.active = "ACTIVE".equalsIgnoreCase(status);
        this.cancelled = "CANCELLED".equalsIgnoreCase(status);
        this.completed = "COMPLETED".equalsIgnoreCase(status);
    }
}