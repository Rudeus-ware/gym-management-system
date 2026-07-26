package com.gym.model.booking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Attendance {
    
    private String attendanceId;
    private String profileId;
    private String sessionId;
    private LocalDateTime attendanceDate;
    private String status;
    private boolean present;
    private boolean absent;
    private boolean late;
    private boolean excused;
    
    // ============================================================
    // CONSTRUCTORS - THESE BELONG IN Attendance.java
    // ============================================================
    
    /**
     * Constructor with LocalDateTime
     */
    public Attendance(String attendanceId, String profileId, String sessionId, 
                      LocalDateTime attendanceDate, String status) {
        this.attendanceId = attendanceId;
        this.profileId = profileId;
        this.sessionId = sessionId;
        this.attendanceDate = attendanceDate != null ? attendanceDate : LocalDateTime.now();
        this.status = status;
        this.present = false;
        this.absent = false;
        this.late = false;
        this.excused = false;
    }
    
    /**
     * Constructor with String date
     */
    public Attendance(String attendanceId, String profileId, String sessionId, 
                      String attendanceDate, String status) {
        this(attendanceId, profileId, sessionId, 
             attendanceDate != null ? LocalDateTime.parse(attendanceDate) : LocalDateTime.now(), 
             status);
    }
    
    // ============================================================
    // GETTERS & SETTERS
    // ============================================================
    
    public String getAttendanceId() { return attendanceId; }
    public String getProfileId() { return profileId; }
    public String getSessionId() { return sessionId; }
    public LocalDateTime getAttendanceDate() { return attendanceDate; }
    public String getStatus() { return status; }
    public boolean isPresent() { return present; }
    public boolean isAbsent() { return absent; }
    public boolean isLate() { return late; }
    public boolean isExcused() { return excused; }
    
    public void setAttendanceId(String attendanceId) { this.attendanceId = attendanceId; }
    public void setProfileId(String profileId) { this.profileId = profileId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setAttendanceDate(LocalDateTime attendanceDate) { this.attendanceDate = attendanceDate; }
    public void setStatus(String status) { this.status = status; }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public void markPresent() {
        this.present = true;
        this.absent = false;
        this.late = false;
        this.excused = false;
        this.status = "PRESENT";
    }
    
    public void markAbsent() {
        this.present = false;
        this.absent = true;
        this.late = false;
        this.excused = false;
        this.status = "ABSENT";
    }
    
    public void markLate() {
        this.present = false;
        this.absent = false;
        this.late = true;
        this.excused = false;
        this.status = "LATE";
    }
    
    public void markExcused() {
        this.present = false;
        this.absent = false;
        this.late = false;
        this.excused = true;
        this.status = "EXCUSED";
    }
    
    public String getAttendanceDateAsString() {
        if (attendanceDate == null) return "";
        return attendanceDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    @Override
    public String toString() {
        return String.format("Attendance{id='%s', profile='%s', session='%s', date='%s', status='%s'}",
            attendanceId, profileId, sessionId, getAttendanceDateAsString(), status);
    }
}