package com.gym.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Session;
import com.gym.persistence.JsonDataManager;
import com.gym.database.DatabaseManager;
import com.gym.util.IdGenerator;

public class AttendanceController {
    
    private final DatabaseManager dataManager;
    private final IdGenerator idGenerator;
    
    public AttendanceController(DatabaseManager dataManager) {
        this.dataManager = dataManager;
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("ATT");
        }
    }
    
    public AttendanceController(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        this.idGenerator = new IdGenerator("ATT");
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    public Attendance markAttendance(String profileId, String sessionId, String status) {
        if (profileId == null || profileId.isEmpty() || sessionId == null || sessionId.isEmpty() 
            || status == null || status.isEmpty()) {
            System.out.println("❌ Invalid input parameters");
            return null;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        Session session = dataManager.getSessions().stream()
            .filter(s -> s.getSessionId().equals(sessionId))
            .findFirst()
            .orElse(null);
            
        if (session == null) {
            System.out.println("❌ Session not found: " + sessionId);
            return null;
        }
        
        boolean alreadyMarked = dataManager.getAttendanceRecords().stream()
            .anyMatch(a -> a.getProfileId().equals(profileId) && 
                          a.getSessionId().equals(sessionId));
        if (alreadyMarked) {
            System.out.println("⚠️ Attendance already marked for this session!");
            return null;
        }
        
        String attendanceId = generateAttendanceId();
        LocalDateTime attendanceDate = LocalDateTime.now();  // Use LocalDateTime
        Attendance attendance = new Attendance(attendanceId, profileId, sessionId, 
                                               attendanceDate, status);
        
        dataManager.addAttendance(attendance);
        
        String normalizedStatus = status.toLowerCase();
        switch (normalizedStatus) {
            case "present" -> attendance.markPresent();
            case "absent" -> attendance.markAbsent();
            case "late" -> attendance.markLate();
            case "excused" -> attendance.markExcused();
            default -> {
                System.out.println("⚠️ Unknown status: " + status);
                System.out.println("   Valid statuses: Present, Absent, Late, Excused");
                return null;
            }
        }
        
        System.out.println("✅ Attendance recorded for: " + profile.getName());
        return attendance;
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    public List<Attendance> getAttendanceForProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) {
            return List.of();
        }
        return dataManager.getAttendanceRecords().stream()
            .filter(a -> a.getProfileId().equals(profileId))
            .collect(Collectors.toList());
    }
    
    public List<Attendance> getAttendanceForSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return List.of();
        }
        return dataManager.getAttendanceRecords().stream()
            .filter(a -> a.getSessionId().equals(sessionId))
            .collect(Collectors.toList());
    }
    
    public List<Attendance> getAttendanceForDate(String date) {
        if (date == null || date.isEmpty()) {
            return List.of();
        }
        
        try {
            LocalDate targetDate = LocalDate.parse(date);
            return dataManager.getAttendanceRecords().stream()
                .filter(a -> {
                    LocalDateTime attendanceDateTime = a.getAttendanceDate();
                    if (attendanceDateTime == null) return false;
                    return attendanceDateTime.toLocalDate().equals(targetDate);
                })
                .collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format. Use yyyy-MM-dd");
            return List.of();
        }
    }
    
    public List<Attendance> getAttendanceByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return List.of();
        }
        return dataManager.getAttendanceRecords().stream()
            .filter(a -> a.getStatus().equalsIgnoreCase(status))
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
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
    
    public double getOverallAttendanceRate() {
        List<Attendance> records = dataManager.getAttendanceRecords();
        if (records.isEmpty()) return 0.0;
        
        long present = records.stream()
            .filter(Attendance::isPresent)
            .count();
            
        return (double) present / records.size() * 100;
    }
    
    public AttendanceStats getAttendanceStats() {
        List<Attendance> records = dataManager.getAttendanceRecords();
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
    
    public boolean updateAttendanceStatus(String attendanceId, String newStatus) {
        if (attendanceId == null || attendanceId.isEmpty() || 
            newStatus == null || newStatus.isEmpty()) {
            System.out.println("❌ Invalid attendance ID or status");
            return false;
        }
        
        Attendance record = findAttendanceById(attendanceId);
        if (record == null) {
            System.out.println("❌ Attendance record not found: " + attendanceId);
            return false;
        }
        
        record.setStatus(newStatus);
        dataManager.saveAllData();
        System.out.println("✅ Attendance status updated to: " + newStatus);
        return true;
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    public boolean deleteAttendanceRecord(String attendanceId) {
        if (attendanceId == null || attendanceId.isEmpty()) {
            System.out.println("❌ Invalid attendance ID");
            return false;
        }
        
        boolean removed = dataManager.getAttendanceRecords()
            .removeIf(a -> a.getAttendanceId().equals(attendanceId));
        if (removed) {
            dataManager.saveAllData();
            System.out.println("✅ Attendance record deleted: " + attendanceId);
            return true;
        } else {
            System.out.println("❌ Attendance record not found: " + attendanceId);
            return false;
        }
    }
    
    // ============================================================
    // DATE RANGE METHODS - FIXED
    // ============================================================
    
    /**
     * Get attendance by date range - FIXED
     * Dates should be in format "yyyy-MM-dd"
     */
    public List<Attendance> getAttendanceByDateRange(String startDate, String endDate) {
        if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
            return List.of();
        }
        
        try {
            // Parse String dates to LocalDate
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            return dataManager.getAttendanceRecords().stream()
                .filter(a -> {
                    LocalDateTime attendanceDateTime = a.getAttendanceDate();
                    if (attendanceDateTime == null) return false;
                    
                    // Extract date part from LocalDateTime
                    LocalDate attendanceDate = attendanceDateTime.toLocalDate();
                    
                    // Compare dates
                    return (attendanceDate.isEqual(start) || attendanceDate.isAfter(start)) &&
                           (attendanceDate.isEqual(end) || attendanceDate.isBefore(end));
                })
                .collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            System.out.println("❌ Invalid date format. Use yyyy-MM-dd");
            return List.of();
        }
    }
    
    /**
     * Get attendance summary for a specific month - FIXED
     */
    public MonthAttendanceSummary getMonthlyAttendanceSummary(int year, int month) {
        List<Attendance> records = dataManager.getAttendanceRecords().stream()
            .filter(a -> {
                LocalDateTime attendanceDateTime = a.getAttendanceDate();
                if (attendanceDateTime == null) return false;
                LocalDate date = attendanceDateTime.toLocalDate();
                return date.getYear() == year && date.getMonthValue() == month;
            })
            .collect(Collectors.toList());
        
        int total = records.size();
        long present = records.stream().filter(Attendance::isPresent).count();
        long absent = records.stream().filter(Attendance::isAbsent).count();
        long late = records.stream().filter(Attendance::isLate).count();
        long excused = records.stream().filter(Attendance::isExcused).count();
        double rate = total > 0 ? (double) present / total * 100 : 0.0;
        
        return new MonthAttendanceSummary(year, month, total, present, absent, late, excused, rate);
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    private Attendance findAttendanceById(String attendanceId) {
        if (attendanceId == null || attendanceId.isEmpty()) {
            return null;
        }
        return dataManager.getAttendanceRecords().stream()
            .filter(a -> a.getAttendanceId().equals(attendanceId))
            .findFirst()
            .orElse(null);
    }
    
    private String generateAttendanceId() {
        if (idGenerator != null) {
            String id = idGenerator.generateId("attendance", "00", LocalDate.now());
            if (id != null && !id.isEmpty()) {
                return id;
            }
        }
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = dataManager.getAttendanceRecords().size() + 1;
        return "ATT" + timestamp + String.format("%04d", count);
    }
    
    public int getTotalAttendanceCount() {
        return dataManager.getAttendanceRecords().size();
    }
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    public static class AttendanceStats {
        public final int total;
        public final long present;
        public final long absent;
        public final long late;
        public final long excused;
        public final double rate;
        
        public AttendanceStats(int total, long present, long absent, long late, 
                               long excused, double rate) {
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
    
    public static class MonthAttendanceSummary {
        public final int year;
        public final int month;
        public final int total;
        public final long present;
        public final long absent;
        public final long late;
        public final long excused;
        public final double rate;
        
        public MonthAttendanceSummary(int year, int month, int total, long present, 
                                      long absent, long late, long excused, double rate) {
            this.year = year;
            this.month = month;
            this.total = total;
            this.present = present;
            this.absent = absent;
            this.late = late;
            this.excused = excused;
            this.rate = rate;
        }
        
        @Override
        public String toString() {
            String monthName = java.time.Month.of(month).name();
            return String.format(
                "📊 MONTHLY ATTENDANCE SUMMARY\n" +
                "============================\n" +
                "Month: %s %d\n" +
                "Total Records: %d\n" +
                "Present: %d\n" +
                "Absent: %d\n" +
                "Late: %d\n" +
                "Excused: %d\n" +
                "Attendance Rate: %.1f%%",
                monthName, year, total, present, absent, late, excused, rate
            );
        }
    }
}