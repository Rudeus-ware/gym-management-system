package com.gym.controller;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Session;
import com.gym.database.DatabaseManager;
import com.gym.util.IdGenerator;

/**
 * Controller for Attendance operations
 * Updated to use String-based IDs
 */
public class AttendanceController {
    
    private final DatabaseManager dataManager;
    private final IdGenerator idGenerator;
    
    public AttendanceController(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        this.idGenerator = new IdGenerator(null); // Will be initialized properly
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    /**
     * Mark attendance for a profile in a session
     */
    public Attendance markAttendance(String profileId, String sessionId, String status) {
        // Validate input
        if (profileId == null || profileId.isEmpty()) {
            System.out.println("❌ Invalid profile ID");
            return null;
        }
        if (sessionId == null || sessionId.isEmpty()) {
            System.out.println("❌ Invalid session ID");
            return null;
        }
        if (status == null || status.isEmpty()) {
            System.out.println("❌ Status is required");
            return null;
        }
        
        // Check if profile exists
        Profile profile = databaseManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        // Check if session exists
        Session session = null;
        for (Session s : databaseManager.getSessions()) {
            if (s.getSessionId().equals(sessionId)) {  // ✅ Use .equals()
                session = s;
                break;
            }
        }
        if (session == null) {
            System.out.println("❌ Session not found: " + sessionId);
            return null;
        }
        
        // Check if already marked
        boolean alreadyMarked = databaseManager.getAttendanceRecords().stream()
            .anyMatch(a -> a.getProfileId().equals(profileId) && a.getSessionId().equals(sessionId));  // ✅ Use .equals()
        if (alreadyMarked) {
            System.out.println("⚠️ Attendance already marked for this session!");
            return null;
        }
        
        // Generate String attendance ID
        String attendanceId = generateAttendanceId();
        String attendanceDate = LocalDate.now().toString();
        Attendance attendance = new Attendance(attendanceId, profileId, sessionId, attendanceDate, status);
        
        databaseManager.addAttendance(attendance);
        
        // Mark based on status
        String normalizedStatus = status.toLowerCase();
        switch (normalizedStatus) {
            case "present":
                attendance.markPresent();
                break;
            case "absent":
                attendance.markAbsent();
                break;
            case "late":
                attendance.markLate();
                break;
            case "excused":
                attendance.markExcused();
                break;
            default:
                System.out.println("⚠️ Unknown status: " + status);
                System.out.println("   Valid statuses: Present, Absent, Late, Excused");
                return null;
        }
        
        System.out.println("✅ Attendance recorded for: " + profile.getName());
        return attendance;
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    /**
     * Get attendance for a profile
     */
    public List<Attendance> getAttendanceForProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            return List.of();
        }
        return databaseManager.getAttendanceRecords().stream()
            .filter(a -> a.getProfileId().equals(profileId))  // ✅ Use .equals()
            .collect(Collectors.toList());
    }
    
    /**
     * Get attendance for a session
     */
    public List<Attendance> getAttendanceForSession(String sessionId) {  // ✅ Changed to String
        if (sessionId == null || sessionId.isEmpty()) {
            return List.of();
        }
        return databaseManager.getAttendanceRecords().stream()
            .filter(a -> a.getSessionId().equals(sessionId))  // ✅ Use .equals()
            .collect(Collectors.toList());
    }
    
    /**
     * Get attendance for a specific date
     */
    public List<Attendance> getAttendanceForDate(String date) {
        if (date == null || date.isEmpty()) {
            return List.of();
        }
        return databaseManager.getAttendanceRecords().stream()
            .filter(a -> a.getAttendanceDate().equals(date))
            .collect(Collectors.toList());
    }
    
    /**
     * Get attendance by status
     */
    public List<Attendance> getAttendanceByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return List.of();
        }
        return databaseManager.getAttendanceRecords().stream()
            .filter(a -> a.getStatus().equalsIgnoreCase(status))
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get attendance rate for a profile
     */
    public double getAttendanceRateForProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            return 0.0;
        }
        List<Attendance> records = getAttendanceForProfile(profileId);
        if (records.isEmpty()) return 0.0;
        
        long present = records.stream()
            .filter(Attendance::isPresent)
            .count();
            
        return (double) present / records.size() * 100;
    }
    
    /**
     * Get overall attendance rate
     */
    public double getOverallAttendanceRate() {
        List<Attendance> records = databaseManager.getAttendanceRecords();
        if (records.isEmpty()) return 0.0;
        
        long present = records.stream()
            .filter(Attendance::isPresent)
            .count();
            
        return (double) present / records.size() * 100;
    }
    
    /**
     * Get attendance statistics
     */
    public AttendanceStats getAttendanceStats() {
        List<Attendance> records = databaseManager.getAttendanceRecords();
        int total = records.size();
        long present = records.stream().filter(Attendance::isPresent).count();
        long absent = records.stream().filter(Attendance::isAbsent).count();
        long late = records.stream().filter(Attendance::isLate).count();
        long excused = records.stream().filter(Attendance::isExcused).count();
        double rate = total > 0 ? (double) present / total * 100 : 0.0;
        
        return new AttendanceStats(total, present, absent, late, excused, rate);
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    /**
     * Update attendance status
     */
    public boolean updateAttendanceStatus(String attendanceId, String newStatus) {
        if (attendanceId == null || attendanceId.isEmpty() || newStatus == null || newStatus.isEmpty()) {
            System.out.println("❌ Invalid attendance ID or status");
            return false;
        }
        
        Attendance record = findAttendanceById(attendanceId);
        if (record == null) {
            System.out.println("❌ Attendance record not found: " + attendanceId);
            return false;
        }
        
        record.setStatus(newStatus);
        databaseManager.saveAllData();
        System.out.println("✅ Attendance status updated to: " + newStatus);
        return true;
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    /**
     * Delete an attendance record
     */
    public boolean deleteAttendanceRecord(String attendanceId) {
        if (attendanceId == null || attendanceId.isEmpty()) {
            System.out.println("❌ Invalid attendance ID");
            return false;
        }
        
        boolean removed = databaseManager.getAttendanceRecords().removeIf(a -> a.getAttendanceId().equals(attendanceId));
        if (removed) {
            databaseManager.saveAllData();
            System.out.println("✅ Attendance record deleted: " + attendanceId);
            return true;
        } else {
            System.out.println("❌ Attendance record not found: " + attendanceId);
            return false;
        }
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    /**
     * Find attendance by ID
     */
    private Attendance findAttendanceById(String attendanceId) {
        if (attendanceId == null || attendanceId.isEmpty()) {
            return null;
        }
        return databaseManager.getAttendanceRecords().stream()
            .filter(a -> a.getAttendanceId().equals(attendanceId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Generate a unique attendance ID
     */
    private String generateAttendanceId() {
        if (idGenerator != null) {
            return idGenerator.generateId("attendance", "00", LocalDate.now());
        }
        // Fallback
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = databaseManager.getAttendanceRecords().size() + 1;
        return "ATT" + timestamp + String.format("%04d", count);
    }
    
    // ============================================================
    // INNER CLASS
    // ============================================================
    
    /**
     * Attendance Statistics DTO
     */
    public static class AttendanceStats {
        public final int total;
        public final long present;
        public final long absent;
        public final long late;
        public final long excused;
        public final double rate;
        
        public AttendanceStats(int total, long present, long absent, long late, long excused, double rate) {
            this.total = total;
            this.present = present;
            this.absent = absent;
            this.late = late;
            this.excused = excused;
            this.rate = rate;
        }
        
        @Override
        public String toString() {
            return String.format(
                "📊 ATTENDANCE STATISTICS\n" +
                "========================\n" +
                "Total Records: %d\n" +
                "Present: %d\n" +
                "Absent: %d\n" +
                "Late: %d\n" +
                "Excused: %d\n" +
                "Attendance Rate: %.1f%%",
                total, present, absent, late, excused, rate
            );
        }
    }
}