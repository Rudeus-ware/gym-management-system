package com.gym.model.attendance;

public class Attendance {
    private int attendanceId;
    private int profileId;
    private int sessionId;
    private String attendanceDate;
    private String status;  // Present, Absent, Late, Excused
    
    // Constructor
    public Attendance(int attendanceId, int profileId, int sessionId, String attendanceDate, String status) {
        this.attendanceId = attendanceId;
        this.profileId = profileId;
        this.sessionId = sessionId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }
    
    // GETTERS
    public int getAttendanceId() { return attendanceId; }
    public int getProfileId() { return profileId; }
    public int getSessionId() { return sessionId; }
    public String getAttendanceDate() { return attendanceDate; }
    public String getStatus() { return status; }
    
    // SETTERS
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setAttendanceDate(String attendanceDate) { this.attendanceDate = attendanceDate; }
    public void setStatus(String status) { this.status = status; }
    
    // ATTENDANCE METHODS
    public void markPresent() {
        this.status = "Present";
        System.out.println("✅ Attendance marked as Present");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    public void markAbsent() {
        this.status = "Absent";
        System.out.println("❌ Attendance marked as Absent");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    public void markLate() {
        this.status = "Late";
        System.out.println("⏰ Attendance marked as Late");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    public void markExcused() {
        this.status = "Excused";
        System.out.println("📝 Attendance marked as Excused");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    public boolean isPresent() {
        return status.equalsIgnoreCase("Present");
    }
    
    public String getAttendanceDetails() {
        return "Attendance ID: " + attendanceId +
               "\nProfile ID: " + profileId +
               "\nSession ID: " + sessionId +
               "\nDate: " + attendanceDate +
               "\nStatus: " + status;
    }
}