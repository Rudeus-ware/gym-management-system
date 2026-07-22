package com.gym.controller;

import com.gym.persistence.DataManager;

public class GymController {
    
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
    
    public GymController() {
        this.dataManager = new DataManager();
        this.loginController = new LoginController(dataManager);
        this.profileController = new ProfileController(dataManager);
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
    public LoginController getLoginController() { return loginController; }
    public ProfileController getProfileController() { return profileController; }
    public AdminController getAdminController() { return adminController; }
    
    // ✅ FIX: Rename to match what's being called
    public MembershipController getMembershipController() { 
        return membershipController; 
    }
    
    // ✅ FIX: Add this if you want the name getMembershipService()
    public MembershipController getMembershipService() { 
        return membershipController; 
    }
    
    public ClassController getClassController() { return classController; }
    public BookingController getBookingController() { return bookingController; }
    public AttendanceController getAttendanceController() { return attendanceController; }
    public PaymentController getPaymentController() { return paymentController; }
    public ReportController getReportController() { return reportController; }
    
    // ===== CONVENIENCE METHODS =====
    public void saveAllData() {
        dataManager.saveAllData();
    }
}