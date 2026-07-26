package com.gym.model.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Session model class
 */
public class Session {
    
    private String sessionId;
    private String classId;
    private String trainerId;
    private String sessionDate;
    private String startTime;
    private String endTime;
    private int maxCapacity;
    private int currentBookings;
    private String status; // "SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Main constructor with all fields
     */
    public Session(String sessionId, String classId, String trainerId, 
                   String sessionDate, String startTime, String endTime, 
                   int maxCapacity) {
        this.sessionId = sessionId;
        this.classId = classId;
        this.trainerId = trainerId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxCapacity = maxCapacity;
        this.currentBookings = 0;
        this.status = "SCHEDULED";
    }
    
    /**
     * Constructor with status
     */
    public Session(String sessionId, String classId, String trainerId, 
                   String sessionDate, String startTime, String endTime, 
                   int maxCapacity, String status) {
        this(sessionId, classId, trainerId, sessionDate, startTime, endTime, maxCapacity);
        this.status = status != null ? status : "SCHEDULED";
    }
    
    /**
     * Simplified constructor (for testing)
     */
    public Session(String sessionId, String classId, String sessionDate) {
        this(sessionId, classId, "UNASSIGNED", sessionDate, "09:00", "10:00", 20);
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public String getSessionId() { return sessionId; }
    public String getClassId() { return classId; }
    public String getTrainerId() { return trainerId; }
    public String getSessionDate() { return sessionDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getCurrentBookings() { return currentBookings; }
    public String getStatus() { return status; }
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setClassId(String classId) { this.classId = classId; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public void setCurrentBookings(int currentBookings) { this.currentBookings = currentBookings; }
    public void setStatus(String status) { this.status = status; }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    /**
     * Check if there are available spots
     */
    public boolean hasAvailableSpots() {
        return currentBookings < maxCapacity;
    }
    
    /**
     * Get number of available spots
     */
    public int getAvailableSpots() {
        return maxCapacity - currentBookings;
    }
    
    /**
     * Add a booking to this session
     */
    public boolean addBooking() {
        if (hasAvailableSpots()) {
            currentBookings++;
            return true;
        }
        return false;
    }
    
    /**
     * Cancel a booking from this session
     */
    public boolean cancelBooking() {
        if (currentBookings > 0) {
            currentBookings--;
            return true;
        }
        return false;
    }
    
    /**
     * Check if the session is full
     */
    public boolean isFull() {
        return currentBookings >= maxCapacity;
    }
    
    /**
     * Check if the session is in the past
     */
    public boolean isPastSession() {
        try {
            LocalDate date = LocalDate.parse(sessionDate);
            return date.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if the session is today
     */
    public boolean isToday() {
        try {
            LocalDate date = LocalDate.parse(sessionDate);
            return date.isEqual(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get formatted date for display
     */
    public String getFormattedDate() {
        try {
            LocalDate date = LocalDate.parse(sessionDate);
            return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return sessionDate;
        }
    }
    
    /**
     * Get formatted time range
     */
    public String getFormattedTime() {
        return String.format("%s - %s", startTime, endTime);
    }
    
    /**
     * Get status in readable format
     */
    public String getStatusDisplay() {
        return switch (status) {
            case "SCHEDULED" -> "📅 Scheduled";
            case "IN_PROGRESS" -> "🔄 In Progress";
            case "COMPLETED" -> "✅ Completed";
            case "CANCELLED" -> "❌ Cancelled";
            default -> "❓ " + status;
        };
    }
    
    /**
     * Get capacity display
     */
    public String getCapacityDisplay() {
        return String.format("%d/%d", currentBookings, maxCapacity);
    }
    
    @Override
    public String toString() {
        return String.format("Session{id='%s', class='%s', trainer='%s', date='%s', time='%s-%s', capacity=%d/%d, status='%s'}",
            sessionId, classId, trainerId, sessionDate, startTime, endTime, 
            currentBookings, maxCapacity, status);
    }
}