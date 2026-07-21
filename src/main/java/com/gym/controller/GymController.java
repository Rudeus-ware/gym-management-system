package com.gym.controller;

import com.gym.persistence.DataManager;
import com.gym.service.MembershipService;

/**
 * Main Controller - Orchestrates all gym operations
 * This is the main entry point for business logic
 */
public class GymController {
    
    // ===== DATA MANAGER =====
    private DataManager dataManager;
    
    // ===== SUB-CONTROLLERS =====
    private MembershipController membershipController;
    private ClassController classController;
    private BookingController bookingController;
    private AttendanceController attendanceController;
    private PaymentController paymentController;
    private ReportController reportController;
    private MembershipService membershipService;
    
    // ===== CONSTRUCTOR =====
    public GymController() {
        this.dataManager = new DataManager();
        
        // Initialize sub-controllers
        this.membershipController = new MembershipController(dataManager);
        this.classController = new ClassController(dataManager);
        this.bookingController = new BookingController(dataManager);
        this.attendanceController = new AttendanceController(dataManager);
        this.paymentController = new PaymentController(dataManager);
        this.reportController = new ReportController(dataManager);
        this.membershipService = new MembershipService();
    }
    
    // ===== GETTERS FOR SUB-CONTROLLERS =====
    public DataManager getDataManager() { return dataManager; }
    public MembershipController getMembershipController() { return membershipController; }
    public ClassController getClassController() { return classController; }
    public BookingController getBookingController() { return bookingController; }
    public AttendanceController getAttendanceController() { return attendanceController; }
    public PaymentController getPaymentController() { return paymentController; }
    public ReportController getReportController() { return reportController; }
    public MembershipService getMembershipService() { return membershipService; }
    
    // ===== CONVENIENCE METHODS =====
    
    /**
     * Save all data to files
     */
    public void saveAllData() {
        dataManager.saveAllData();
        System.out.println("✅ All data saved successfully!");
    }
    
    /**
     * Load all data from files
     */
    public void loadAllData() {
        // DataManager loads on construction
        System.out.println("✅ All data loaded successfully!");
    }
    
    /**
     * Get total number of profiles
     */
    public int getTotalMembers() {
        return dataManager.getProfiles().size();
    }
    
    /**
     * Get total number of classes
     */
    public int getTotalClasses() {
        return dataManager.getGymClasses().size();
    }
    
    /**
     * Get total number of bookings
     */
    public int getTotalBookings() {
        return dataManager.getBookings().size();
    }
    
    /**
     * Get system statistics
     */
    public SystemStats getSystemStats() {
        return new SystemStats(
            dataManager.getProfiles().size(),
            dataManager.getGymClasses().size(),
            dataManager.getBookings().size(),
            dataManager.getSessions().size(),
            dataManager.getAttendanceRecords().size(),
            dataManager.getTrainers().size()
        );
    }
    
    // ===== INNER CLASS FOR STATS =====
    public static class SystemStats {
        public final int totalMembers;
        public final int totalClasses;
        public final int totalBookings;
        public final int totalSessions;
        public final int totalAttendance;
        public final int totalTrainers;
        
        public SystemStats(int members, int classes, int bookings, 
                          int sessions, int attendance, int trainers) {
            this.totalMembers = members;
            this.totalClasses = classes;
            this.totalBookings = bookings;
            this.totalSessions = sessions;
            this.totalAttendance = attendance;
            this.totalTrainers = trainers;
        }
        
        @Override
        public String toString() {
            return String.format(
                "📊 System Statistics:\n" +
                "   Members: %d\n" +
                "   Classes: %d\n" +
                "   Bookings: %d\n" +
                "   Sessions: %d\n" +
                "   Attendance Records: %d\n" +
                "   Trainers: %d",
                totalMembers, totalClasses, totalBookings,
                totalSessions, totalAttendance, totalTrainers
            );
        }
    }
}