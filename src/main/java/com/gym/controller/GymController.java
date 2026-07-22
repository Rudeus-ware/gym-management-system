package com.gym.controller;

import com.gym.persistence.DataManager;

/**
 * Main orchestrator for the application
 * This is the central controller that coordinates everything
 */
public class GymController {
    
    private DataManager dataManager;
    private AdminController adminController;
    private MembershipController membershipController;
    private ClassController classController;
    private BookingController bookingController;
    private AttendanceController attendanceController;
    private PaymentController paymentController;
    private ReportController reportController;
    
    public GymController() {
        this.dataManager = new DataManager();
        this.adminController = new AdminController(dataManager);
        this.membershipController = new MembershipController(dataManager);
        this.classController = new ClassController(dataManager);
        this.bookingController = new BookingController(dataManager);
        this.attendanceController = new AttendanceController(dataManager);
        this.paymentController = new PaymentController(dataManager);
        this.reportController = new ReportController(dataManager);
    }
    
    // ===== GETTERS =====
    public DataManager getDataManager() { return dataManager; }
    public AdminController getAdminController() { return adminController; }
    public MembershipController getMembershipController() { return membershipController; }
    public ClassController getClassController() { return classController; }
    public BookingController getBookingController() { return bookingController; }
    public AttendanceController getAttendanceController() { return attendanceController; }
    public PaymentController getPaymentController() { return paymentController; }
    public ReportController getReportController() { return reportController; }
    
    // ===== SAVE & LOAD =====
    public void saveAllData() {
        dataManager.saveAllData();
        System.out.println("✅ All data saved!");
    }
    
    public void loadAllData() {
        // DataManager loads on construction
        System.out.println("✅ All data loaded!");
    }
    
    // ===== STATISTICS =====
    public int getTotalMembers() {
        return dataManager.getProfiles().size();
    }
    
    public int getTotalClasses() {
        return dataManager.getGymClasses().size();
    }
    
    public int getTotalBookings() {
        return dataManager.getBookings().size();
    }
}