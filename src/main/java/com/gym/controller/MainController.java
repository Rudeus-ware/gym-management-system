package com.gym.controller;

import java.util.List;

import com.gym.model.Profile;
import com.gym.model.attendance.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.GymClass;
import com.gym.model.membership.Membership;
import com.gym.model.user.Trainer;
import com.gym.persistence.DataManager;

/**
 * MainController - Central orchestrator for the entire application
 * This is the main entry point for all business operations
 * Pure business logic - NO UI code
 */
public class MainController {
    
    private DataManager dataManager;
    private LoginController loginController;
    private ProfileController profileController;
    private AdminController adminController;
    private MembershipController membershipController;
    private ClassController classController;
    private BookingController bookingController;
    private AttendanceController attendanceController;
    private PaymentController paymentController;
    private ReportController reportController;
    
    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    
    public MainController() {
        this.dataManager = new DataManager();
        
        // Initialize all sub-controllers
        this.loginController = new LoginController(dataManager);
        this.profileController = new ProfileController(dataManager);
        this.adminController = new AdminController(dataManager);
        this.membershipController = new MembershipController(dataManager);
        this.classController = new ClassController(dataManager);
        this.bookingController = new BookingController(dataManager);
        this.attendanceController = new AttendanceController(dataManager);
        this.paymentController = new PaymentController(dataManager);
        this.reportController = new ReportController(dataManager);
        
        System.out.println("✅ MainController initialized");
    }
    
    // ============================================================
    // GETTERS FOR SUB-CONTROLLERS
    // ============================================================
    
    public DataManager getDataManager() { return dataManager; }
    public LoginController getLoginController() { return loginController; }
    public ProfileController getProfileController() { return profileController; }
    public AdminController getAdminController() { return adminController; }
    public MembershipController getMembershipController() { return membershipController; }
    public ClassController getClassController() { return classController; }
    public BookingController getBookingController() { return bookingController; }
    public AttendanceController getAttendanceController() { return attendanceController; }
    public PaymentController getPaymentController() { return paymentController; }
    public ReportController getReportController() { return reportController; }
    
    // ============================================================
    // DATA MANAGEMENT
    // ============================================================
    
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
     * Clear all data
     */
    public void clearAllData() {
        dataManager.clearAllData();
        System.out.println("✅ All data cleared!");
    }
    
    // ============================================================
    // SYSTEM STATISTICS
    // ============================================================
    
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
     * Get total number of sessions
     */
    public int getTotalSessions() {
        return dataManager.getSessions().size();
    }
    
    /**
     * Get total number of attendance records
     */
    public int getTotalAttendance() {
        return dataManager.getAttendanceRecords().size();
    }
    
    /**
     * Get total number of trainers
     */
    public int getTotalTrainers() {
        return dataManager.getTrainers().size();
    }
    
    /**
     * Get complete system statistics
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
    
    // ============================================================
    // QUICK ACCESS METHODS
    // ============================================================
    
    /**
     * Get all profiles (members)
     */
    public List<Profile> getAllMembers() {
        return dataManager.getProfiles();
    }
    
    /**
     * Get all trainers
     */
    public List<Trainer> getAllTrainers() {
        return dataManager.getTrainers();
    }
    
    /**
     * Get all classes
     */
    public List<GymClass> getAllClasses() {
        return dataManager.getGymClasses();
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        return dataManager.getBookings();
    }
    
    /**
     * Get all sessions
     */
    public List<Session> getAllSessions() {
        return dataManager.getSessions();
    }
    
    /**
     * Get all attendance records
     */
    public List<Attendance> getAllAttendance() {
        return dataManager.getAttendanceRecords();
    }
    
    /**
     * Get all memberships
     */
    public List<Membership> getAllMemberships() {
        return dataManager.getMemberships();
    }
    
    // ============================================================
    // SYSTEM STATUS
    // ============================================================
    
    /**
     * Check if the system is ready
     */
    public boolean isSystemReady() {
        try {
            // Check if data manager is working
            if (dataManager == null) return false;
            
            // Check if controllers are initialized
            if (loginController == null) return false;
            if (profileController == null) return false;
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get system status
     */
    public String getSystemStatus() {
        if (!isSystemReady()) {
            return "❌ System is NOT ready";
        }
        
        return String.format(
            "✅ System is ready\n" +
            "   Data Manager: OK\n" +
            "   Controllers: %d initialized\n" +
            "   Total Data Objects: %d",
            9, // Number of controllers
            dataManager.getProfiles().size() + 
            dataManager.getGymClasses().size() + 
            dataManager.getBookings().size()
        );
    }
    
    // ============================================================
    // INNER CLASS: SystemStats
    // ============================================================
    
    /**
     * System Statistics DTO
     */
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
                "📊 SYSTEM STATISTICS\n" +
                "====================\n" +
                "Total Members: %d\n" +
                "Total Classes: %d\n" +
                "Total Bookings: %d\n" +
                "Total Sessions: %d\n" +
                "Total Attendance Records: %d\n" +
                "Total Trainers: %d",
                totalMembers, totalClasses, totalBookings,
                totalSessions, totalAttendance, totalTrainers
            );
        }
        
        /**
         * Get a formatted summary for dashboard display
         */
        public String getDashboardSummary() {
            return String.format(
                "Members: %d | Classes: %d | Bookings: %d | Sessions: %d",
                totalMembers, totalClasses, totalBookings, totalSessions
            );
        }
    }
}