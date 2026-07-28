package com.gym.controller;

import java.util.List;

import com.gym.database.DatabaseManager;
import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.GymClass;
import com.gym.model.membership.Membership;  // ← ADD THIS IMPORT
import com.gym.model.payment.Payment;
import com.gym.model.user.Trainer;
import com.gym.persistence.JsonDataManager;
import com.gym.util.IdGenerator;

/**
 * Main Gym Controller - Central controller for the application
 */
public class GymController {
    
    private final DatabaseManager dataManager;
    private final IdGenerator idGenerator;
    private final LoginController loginController;
    private final ProfileController profileController;
    private final AdminController adminController;
    private final MembershipController membershipController;
    private final ClassController classController;
    private final BookingController bookingController;
    private final AttendanceController attendanceController;
    private final PaymentController paymentController;    // ← ADD THIS FIELD
    private final ReportController reportController;      // ← ADD THIS FIELD
    
    public GymController() {
        this(null);
    }

    public GymController(DatabaseManager dataManager) {
        this.dataManager = dataManager;
        
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("GYM");
        }
        
        // Initialize all controllers
        this.loginController = new LoginController(dataManager);
        this.profileController = new ProfileController(dataManager);
        this.adminController = new AdminController(dataManager);
        this.membershipController = new MembershipController(dataManager);
        this.classController = new ClassController(this);
        this.bookingController = new BookingController(dataManager);
        this.attendanceController = new AttendanceController(dataManager);
        this.paymentController = new PaymentController(dataManager);    // ← ADD THIS
        this.reportController = new ReportController(dataManager);      // ← ADD THIS
    }
    
    // Constructor for JSON fallback
    public GymController(JsonDataManager dataManager) {
        this((DatabaseManager) null);
    }

    public GymController(com.gym.persistence.JsonDataManager dataManager, boolean ignored) {
        this((DatabaseManager) null);
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public DatabaseManager getDataManager() {
        return dataManager;
    }
    
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
    
    public LoginController getLoginController() {
        return loginController;
    }
    
    public ProfileController getProfileController() {
        return profileController;
    }
    
    public AdminController getAdminController() {
        return adminController;
    }
    
    public MembershipController getMembershipController() {
        return membershipController;
    }
    
    public ClassController getClassController() {
        return classController;
    }
    
    public BookingController getBookingController() {
        return bookingController;
    }
    
    public AttendanceController getAttendanceController() {
        return attendanceController;
    }
    
    public PaymentController getPaymentController() {    // ← ADD THIS
        return paymentController;
    }
    
    public ReportController getReportController() {      // ← ADD THIS
        return reportController;
    }
    
    // ============================================================
    // DATA ACCESS METHODS
    // ============================================================
    
    public List<Profile> getAllProfiles() {
        return dataManager.findAllProfiles();
    }
    
    public Profile getProfileById(String id) {
        return dataManager.findProfileById(id);
    }
    
    public List<GymClass> getAllClasses() {
        return dataManager.findAllClasses();
    }
    
    public List<Booking> getAllBookings() {
        return dataManager.findAllBookings();
    }
    
    public List<Session> getAllSessions() {
        return dataManager.findAllSessions();
    }
    
    public List<Attendance> getAllAttendance() {
        return dataManager.findAllAttendance();
    }
    
    public List<Trainer> getAllTrainers() {
        return dataManager.findAllTrainers();
    }
    
    public List<Membership> getAllMemberships() {
        return dataManager.findAllMemberships();
    }
    
    public List<Payment> getAllPayments() {              // ← ADD THIS
        return dataManager.getPayments();
    }
    
    // ============================================================
    // SAVE OPERATIONS
    // ============================================================
    
    public void saveAllData() {
        if (dataManager != null) {
            dataManager.saveAllData();
        }
    }
}