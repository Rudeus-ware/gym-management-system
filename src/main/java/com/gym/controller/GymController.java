package com.gym.controller;

import com.gym.database.DatabaseConnection;
import com.gym.database.ProfileDatabaseManager;
import com.gym.persistence.DataManager;
import com.gym.model.Profile;

import java.util.List;

/**
 * GymController - Main orchestrator for the application
 * Now supports both Database and File-based persistence
 */
public class GymController {
    
    private DataManager dataManager;
    private ProfileDatabaseManager profileDatabaseManager;
    private boolean useDatabase = true; // Set to false to use FileManager
    
    // Sub-controllers
    private LoginController loginController;
    private AdminController adminController;
    private MembershipController membershipController;
    private ClassController classController;
    private BookingController bookingController;
    private AttendanceController attendanceController;
    private PaymentController paymentController;
    private ReportController reportController;
    
    public GymController() {
        this.dataManager = new DataManager();
        this.profileDatabaseManager = new ProfileDatabaseManager();
        
        // Initialize sub-controllers
        this.loginController = new LoginController(dataManager);
        this.adminController = new AdminController(dataManager);
        this.membershipController = new MembershipController(dataManager);
        this.classController = new ClassController(dataManager);
        this.bookingController = new BookingController(dataManager);
        this.attendanceController = new AttendanceController(dataManager);
        this.paymentController = new PaymentController(dataManager);
        this.reportController = new ReportController(dataManager);
        
        System.out.println("✅ GymController initialized");
        if (useDatabase) {
            System.out.println("   Using Database persistence");
        } else {
            System.out.println("   Using File persistence");
        }
    }
    
    // ===== GETTERS =====
    public DataManager getDataManager() { return dataManager; }
    public ProfileDatabaseManager getProfileDatabaseManager() { return profileDatabaseManager; }
    public LoginController getLoginController() { return loginController; }
    public AdminController getAdminController() { return adminController; }
    public MembershipController getMembershipController() { return membershipController; }
    public ClassController getClassController() { return classController; }
    public BookingController getBookingController() { return bookingController; }
    public AttendanceController getAttendanceController() { return attendanceController; }
    public PaymentController getPaymentController() { return paymentController; }
    public ReportController getReportController() { return reportController; }
    
    // ===== DATABASE METHODS =====
    
    /**
     * Get all members using Database
     */
    public List<Profile> getAllMembersDatabase() {
        return profileDatabaseManager.findAllProfiles();
    }
    
    /**
     * Create member using Database
     */
    public Profile createMemberDatabase(String name, String email, String phone, String address) {
        return profileDatabaseManager.createProfile(name, email, phone, address);
    }
    
    /**
     * Find member by ID using Database
     */
    public Profile getMemberDatabase(int id) {
        return profileDatabaseManager.findProfileById(id);
    }
    
    /**
     * Update member using Database
     */
    public boolean updateMemberDatabase(Profile profile) {
        return profileDatabaseManager.updateProfile(profile);
    }
    
    /**
     * Delete member using Database
     */
    public boolean deleteMemberDatabase(int id) {
        return profileDatabaseManager.deleteProfile(id);
    }
    
    // ===== FILE-BASED METHODS (Legacy) =====
    
    public List<Profile> getAllMembers() {
        return dataManager.getProfiles();
    }
    
    public void saveAllData() {
        dataManager.saveAllData();
    }
    
    // ===== SWITCH METHODS =====
    
    public void switchToDatabase() {
        useDatabase = true;
        System.out.println("✅ Switched to Database mode");
    }
    
    public void switchToFile() {
        useDatabase = false;
        System.out.println("✅ Switched to File mode");
    }
    
    public boolean isUsingDatabase() {
        return useDatabase;
    }
}
