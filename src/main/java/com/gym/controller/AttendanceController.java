package com.gym.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.Profile;
import com.gym.model.attendance.Attendance;
import com.gym.model.booking.Session;
import com.gym.persistence.DataManager;

/**
 * Controller for Attendance operations
 */
public class AttendanceController {
    
    private final DataManager dataManager;
    
    public AttendanceController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    /**
     * Mark attendance for a profile in a session
     */
    public Attendance markAttendance(int profileId, int sessionId, String status) {
        // Check if profile exists
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found!");
            return null;
        }
        
        // Check if session exists
        Session session = null;
        for (Session s : dataManager.getSessions()) {
            if (s.getSessionId() == sessionId) {
                session = s;
                break;
            }
        }
        if (session == null) {
            System.out.println("❌ Session not found!");
            return null;
        }
        
        // Check if already marked
        boolean alreadyMarked = dataManager.getAttendanceRecords().stream()
            .anyMatch(a -> a.getProfileId() == profileId && a.getSessionId() == sessionId);
        if (alreadyMarked) {
            System.out.println("⚠️ Attendance already marked for this session!");
            return null;
        }
        
        // Create attendance record
        int attendanceId = dataManager.getAttendanceRecords().size() + 1;
        String attendanceDate = LocalDate.now().toString();
        Attendance attendance = new Attendance(attendanceId, profileId, sessionId, attendanceDate, status);
        
        dataManager.addAttendance(attendance);
        
        // Mark based on status
        switch (status.toLowerCase()) {
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
        }
        
        return attendance;
    }
    
    /**
     * Get attendance for a profile
     */
    public List<Attendance> getAttendanceForProfile(int profileId) {
        return dataManager.getAttendanceRecords().stream()
            .filter(a -> a.getProfileId() == profileId)
            .collect(Collectors.toList());
    }
    
    /**
     * Get attendance for a session
     */
    public List<Attendance> getAttendanceForSession(int sessionId) {
        return dataManager.getAttendanceRecords().stream()
            .filter(a -> a.getSessionId() == sessionId)
            .collect(Collectors.toList());
    }
    
    /**
     * Get attendance rate for a profile
     */
    public double getAttendanceRateForProfile(int profileId) {
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
        List<Attendance> records = dataManager.getAttendanceRecords();
        if (records.isEmpty()) return 0.0;
        
        long present = records.stream()
            .filter(Attendance::isPresent)
            .count();
            
        return (double) present / records.size() * 100;
    }
}