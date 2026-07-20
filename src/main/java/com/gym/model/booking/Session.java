package com.gym.model.booking;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Session {
    private int sessionId;
    private int classId;          // Which class this session belongs to
    private String sessionDate;   // Date of the session
    private String startTime;     // When it starts
    private String expiryTime;    // When it ends (should be called endTime)
    private String duration;      // How long it lasts
    private int trainerId;        // Who is teaching
    private int currentAttendees;
    private boolean isActive;
    
    // Constructor
    public Session(int sessionId, int classId, String sessionDate, String startTime, 
                   String expiryTime, String duration, int trainerId) {
        this.sessionId = sessionId;
        this.classId = classId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.expiryTime = expiryTime;
        this.duration = duration;
        this.trainerId = trainerId;
        this.currentAttendees = 0;
        this.isActive = true;
    }
    
    // GETTERS
    public int getSessionId() { return sessionId; }
    public int getClassId() { return classId; }
    public String getSessionDate() { return sessionDate; }
    public String getStartTime() { return startTime; }
    public String getExpiryTime() { return expiryTime; }
    public String getDuration() { return duration; }
    public int getTrainerId() { return trainerId; }
    public int getCurrentAttendees() { return currentAttendees; }
    public boolean isActive() { return isActive; }
    
    // SETTERS
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setClassId(int classId) { this.classId = classId; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setExpiryTime(String expiryTime) { this.expiryTime = expiryTime; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public void setActive(boolean active) { isActive = active; }
    
    // SESSION METHODS
    public void startSession() {
        if (!isActive) {
            System.out.println("❌ Cannot start - session is not active");
            return;
        }
        
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(startTime);
        
        if (now.isBefore(start)) {
            System.out.println("⏰ Session starts at " + startTime + ". Waiting for start time...");
            System.out.println("   Current time: " + now.format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            System.out.println("✅ Session started at " + startTime);
            System.out.println("   Class ID: " + classId);
            System.out.println("   Trainer ID: " + trainerId);
        }
    }
    
    public void endSession() {
        if (!isActive) {
            System.out.println("❌ Cannot end - session is not active");
            return;
        }
        
        LocalTime now = LocalTime.now();
        LocalTime end = LocalTime.parse(expiryTime);
        
        System.out.println("✅ Session ended at " + expiryTime);
        System.out.println("   Total attendees: " + currentAttendees);
        System.out.println("   Duration: " + duration);
        this.isActive = false;
    }
    
    public String calculateDuration() {
        // Calculate duration from startTime and expiryTime
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(expiryTime);
        
        long hours = java.time.Duration.between(start, end).toHours();
        long minutes = java.time.Duration.between(start, end).toMinutes() % 60;
        
        return hours + "h " + minutes + "m";
    }
    
    public void addAttendee() {
        currentAttendees++;
    }
    
    public void removeAttendee() {
        if (currentAttendees > 0) {
            currentAttendees--;
        }
    }
    
    public String getSessionDetails() {
        return "Session ID: " + sessionId +
               "\nClass ID: " + classId +
               "\nDate: " + sessionDate +
               "\nTime: " + startTime + " - " + expiryTime +
               "\nDuration: " + duration +
               "\nTrainer ID: " + trainerId +
               "\nAttendees: " + currentAttendees +
               "\nStatus: " + (isActive ? "Active" : "Ended");
    }
    
    public boolean isAvailable() {
        return isActive && currentAttendees < 30; // Max capacity
    }
}