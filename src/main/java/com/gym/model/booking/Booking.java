package com.gym.model.booking;

public class Booking {
    private String bookingId;
    private String profileId;      // Who is booking
    private String classId;        // Which class
    private String sessionId;      // Which session
    private String bookingDate; // When they booked
    private String status;      // Confirmed, Cancelled, Completed, Waitlisted
    
    // Constructor
    public Booking(String bookingId, String profileId, String classId, String sessionId, String bookingDate, String status) {
        this.bookingId = bookingId;
        this.profileId = profileId;
        this.classId = classId;
        this.sessionId = sessionId;
        this.bookingDate = bookingDate;
        this.status = status;
    }
    
    // GETTERS
    public String getBookingId() { return bookingId; }
    public String getProfileId() { return profileId; }
    public String getClassId() { return classId; }
    public String getSessionId() { return sessionId; }
    public String getBookingDate() { return bookingDate; }
    public String getStatus() { return status; }
    
    // SETTERS
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    public void setClassId(String classId) { this.classId = classId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    public void setStatus(String status) { this.status = status; }
    
    // BOOKING METHODS
    public void confirmBooking() {
        if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Waitlisted")) {
            this.status = "Confirmed";
            System.out.println("✅ Booking " + bookingId + " confirmed!");
            System.out.println("   Member: " + profileId);
            System.out.println("   Class: " + classId);
            System.out.println("   Session: " + sessionId);
        } else {
            System.out.println("⚠️ Booking cannot be confirmed. Current status: " + status);
        }
    }
    
    public void cancelBooking() {
        if (!status.equalsIgnoreCase("Cancelled")) {
            this.status = "Cancelled";
            System.out.println("❌ Booking " + bookingId + " cancelled!");
            System.out.println("   Member: " + profileId);
            System.out.println("   Class: " + classId);
            System.out.println("   Session: " + sessionId);
        } else {
            System.out.println("⚠️ Booking already cancelled.");
        }
    }
    
    public void changeStatus(String newStatus) {
        String[] validStatuses = {"Pending", "Confirmed", "Cancelled", "Completed", "Waitlisted"};
        boolean isValid = false;
        
        for (String valid : validStatuses) {
            if (valid.equalsIgnoreCase(newStatus)) {
                isValid = true;
                break;
            }
        }
        
        if (isValid) {
            this.status = newStatus;
            System.out.println("🔄 Booking " + bookingId + " status changed to: " + newStatus);
        } else {
            System.out.println("❌ Invalid status: " + newStatus);
            System.out.println("   Valid statuses: Pending, Confirmed, Cancelled, Completed, Waitlisted");
        }
    }
    
    public boolean isConfirmed() {
        return status.equalsIgnoreCase("Confirmed");
    }
    
    public boolean isActive() {
        return status.equalsIgnoreCase("Confirmed") || status.equalsIgnoreCase("Pending");
    }
    
    public String getBookingDetails() {
        return "Booking ID: " + bookingId +
               "\nProfile ID: " + profileId +
               "\nClass ID: " + classId +
               "\nSession ID: " + sessionId +
               "\nBooking Date: " + bookingDate +
               "\nStatus: " + status;
    }
}