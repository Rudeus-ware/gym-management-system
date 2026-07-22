package com.gym.model.booking;

/**
 * Attendance Model - Tracks member attendance for gym sessions
 * 
 * This class represents a record of whether a member attended a specific session.
 * It links a Profile (member) to a Session (class time) with a status.
 * 
 * @author Gym Management System
 * @version 1.0
 */
public class Attendance {
    
    // ============================================================
    // ATTRIBUTES
    // ============================================================
    
    private int attendanceId;      // Unique identifier for this attendance record
    private int profileId;          // ID of the member (Profile)
    private int sessionId;          // ID of the session attended
    private String attendanceDate;  // Date of attendance (YYYY-MM-DD)
    private String status;          // Present, Absent, Late, Excused
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Full constructor - creates a new attendance record
     * 
     * @param attendanceId   Unique ID for this attendance record
     * @param profileId      ID of the member (Profile)
     * @param sessionId      ID of the session
     * @param attendanceDate Date of attendance
     * @param status         Attendance status (Present/Absent/Late/Excused)
     */
    public Attendance(int attendanceId, int profileId, int sessionId, 
                      String attendanceDate, String status) {
        this.attendanceId = attendanceId;
        this.profileId = profileId;
        this.sessionId = sessionId;
        this.attendanceDate = attendanceDate;
        this.status = status;
    }
    
    /**
     * Simplified constructor - uses current date
     * 
     * @param attendanceId   Unique ID for this attendance record
     * @param profileId      ID of the member (Profile)
     * @param sessionId      ID of the session
     * @param status         Attendance status (Present/Absent/Late/Excused)
     */
    public Attendance(int attendanceId, int profileId, int sessionId, String status) {
        this(attendanceId, profileId, sessionId, 
             java.time.LocalDate.now().toString(), status);
    }
    
    /**
     * Default constructor
     */
    public Attendance() {
        this.attendanceId = 0;
        this.profileId = 0;
        this.sessionId = 0;
        this.attendanceDate = java.time.LocalDate.now().toString();
        this.status = "Present";
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    /**
     * Get the attendance record ID
     * @return attendanceId
     */
    public int getAttendanceId() { 
        return attendanceId; 
    }
    
    /**
     * Get the profile ID (member)
     * @return profileId
     */
    public int getProfileId() { 
        return profileId; 
    }
    
    /**
     * Get the session ID
     * @return sessionId
     */
    public int getSessionId() { 
        return sessionId; 
    }
    
    /**
     * Get the attendance date
     * @return attendanceDate as String
     */
    public String getAttendanceDate() { 
        return attendanceDate; 
    }
    
    /**
     * Get the attendance status
     * @return status (Present/Absent/Late/Excused)
     */
    public String getStatus() { 
        return status; 
    }
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    /**
     * Set the attendance record ID
     * @param attendanceId new ID
     */
    public void setAttendanceId(int attendanceId) { 
        this.attendanceId = attendanceId; 
    }
    
    /**
     * Set the profile ID (member)
     * @param profileId new profile ID
     */
    public void setProfileId(int profileId) { 
        this.profileId = profileId; 
    }
    
    /**
     * Set the session ID
     * @param sessionId new session ID
     */
    public void setSessionId(int sessionId) { 
        this.sessionId = sessionId; 
    }
    
    /**
     * Set the attendance date
     * @param attendanceDate new date (YYYY-MM-DD)
     */
    public void setAttendanceDate(String attendanceDate) { 
        this.attendanceDate = attendanceDate; 
    }
    
    /**
     * Set the attendance status
     * @param status new status (Present/Absent/Late/Excused)
     */
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    /**
     * Mark attendance as Present
     * This method updates the status to "Present" and provides confirmation
     */
    public void markPresent() {
        this.status = "Present";
        System.out.println("✅ Attendance marked as Present");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    /**
     * Mark attendance as Absent
     * This method updates the status to "Absent" and provides confirmation
     */
    public void markAbsent() {
        this.status = "Absent";
        System.out.println("❌ Attendance marked as Absent");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    /**
     * Mark attendance as Late
     * This method updates the status to "Late" and provides confirmation
     */
    public void markLate() {
        this.status = "Late";
        System.out.println("⏰ Attendance marked as Late");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    /**
     * Mark attendance as Excused
     * This method updates the status to "Excused" and provides confirmation
     */
    public void markExcused() {
        this.status = "Excused";
        System.out.println("📝 Attendance marked as Excused");
        System.out.println("   Profile ID: " + profileId);
        System.out.println("   Session ID: " + sessionId);
        System.out.println("   Date: " + attendanceDate);
    }
    
    // ============================================================
    // QUERY METHODS
    // ============================================================
    
    /**
     * Check if the member was present
     * @return true if status is "Present", false otherwise
     */
    public boolean isPresent() {
        return "Present".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the member was absent
     * @return true if status is "Absent", false otherwise
     */
    public boolean isAbsent() {
        return "Absent".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the member was late
     * @return true if status is "Late", false otherwise
     */
    public boolean isLate() {
        return "Late".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the member was excused
     * @return true if status is "Excused", false otherwise
     */
    public boolean isExcused() {
        return "Excused".equalsIgnoreCase(status);
    }
    
    /**
     * Check if the attendance status is valid
     * @return true if status is one of: Present, Absent, Late, Excused
     */
    public boolean isValidStatus() {
        String[] validStatuses = {"Present", "Absent", "Late", "Excused"};
        for (String valid : validStatuses) {
            if (valid.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Get a formatted string with all attendance details
     * @return Formatted attendance information
     */
    public String getAttendanceDetails() {
        return "Attendance ID: " + attendanceId +
               "\nProfile ID: " + profileId +
               "\nSession ID: " + sessionId +
               "\nDate: " + attendanceDate +
               "\nStatus: " + status;
    }
    
    /**
     * Get a summary string (one line)
     * @return Short summary
     */
    public String getSummary() {
        return "Attendance #" + attendanceId + 
               " | Member: " + profileId +
               " | Session: " + sessionId +
               " | " + status;
    }
    
    /**
     * Get status with emoji for visual representation
     * @return Status with emoji
     */
    public String getStatusWithEmoji() {
        switch (status.toLowerCase()) {
            case "present":
                return "✅ Present";
            case "absent":
                return "❌ Absent";
            case "late":
                return "⏰ Late";
            case "excused":
                return "📝 Excused";
            default:
                return "❓ Unknown";
        }
    }
    
    // ============================================================
    // OVERRIDDEN METHODS
    // ============================================================
    
    @Override
    public String toString() {
        return getSummary();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Attendance that = (Attendance) obj;
        return attendanceId == that.attendanceId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(attendanceId);
    }
}