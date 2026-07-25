package com.gym.controller;

import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.booking.Attendance;
import com.gym.model.user.Trainer;

import java.util.List;

/**
 * GymController - Main orchestrator for the application
 * Uses DatabaseManager as primary, JsonDataManager as backup
 */
public class GymController extends BaseController {
    
    // Sub-controllers
    private LoginController loginController;
    private AdminController adminController;
    private ProfileController profileController;
    private MembershipController membershipController;
    private ClassController classController;
    private BookingController bookingController;
    private AttendanceController attendanceController;
    private PaymentController paymentController;
    private ReportController reportController;
    
    public GymController() {
        super();
        initializeSubControllers();
    }
    
    public GymController(boolean useDatabase) {
        super(useDatabase);
        initializeSubControllers();
    }
    
    private void initializeSubControllers() {
        this.loginController = new LoginController(this);
        this.adminController = new AdminController(this);
        this.profileController = new ProfileController(this);
        this.membershipController = new MembershipController(this);
        this.classController = new ClassController(this);
        this.bookingController = new BookingController(this);
        this.attendanceController = new AttendanceController(this);
        this.paymentController = new PaymentController(this);
        this.reportController = new ReportController(this);
    }
    
    // ============================================================
    // GETTERS FOR SUB-CONTROLLERS
    // ============================================================
    
    public LoginController getLoginController() { return loginController; }
    public AdminController getAdminController() { return adminController; }
    public ProfileController getProfileController() { return profileController; }
    public MembershipController getMembershipController() { return membershipController; }
    public ClassController getClassController() { return classController; }
    public BookingController getBookingController() { return bookingController; }
    public AttendanceController getAttendanceController() { return attendanceController; }
    public PaymentController getPaymentController() { return paymentController; }
    public ReportController getReportController() { return reportController; }
    
    // ============================================================
    // DATA ACCESS METHODS (Primary Database, Fallback JSON)
    // ============================================================
    
    /**
     * Get all profiles - Primary: Database, Fallback: JSON
     */
    public List<Profile> getAllProfiles() {
        try {
            if (useDatabase) {
                List<Profile> profiles = databaseManager.findAllProfiles();
                if (!profiles.isEmpty()) {
                    return profiles;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        return jsonDataManager.getProfiles();
    }
    
    /**
     * Get profile by ID - Primary: Database, Fallback: JSON
     */
    public Profile getProfileById(String id) {
        try {
            if (useDatabase) {
                Profile profile = databaseManager.findProfileById(id);
                if (profile != null) {
                    return profile;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        return jsonDataManager.findProfileById(id);
    }
    
    /**
     * Create profile - Primary: Database, Fallback: JSON
     */
    public Profile createProfile(Profile profile) {
        try {
            if (useDatabase) {
                Profile created = databaseManager.createProfile(profile);
                if (created != null) {
                    return created;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        jsonDataManager.addProfile(profile);
        jsonDataManager.saveAllData();
        return profile;
    }
    
    /**
     * Update profile - Primary: Database, Fallback: JSON
     */
    public boolean updateProfile(Profile profile) {
        try {
            if (useDatabase) {
                databaseManager.updateProfile(profile);
                return true;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        jsonDataManager.updateProfile(profile); // Add this method to JsonDataManager
        jsonDataManager.saveAllData();
        return true;
    }
    
    /**
     * Delete profile - Primary: Database, Fallback: JSON
     */
    public boolean deleteProfile(String id) {
        try {
            if (useDatabase) {
                databaseManager.deleteProfile(id);
                return true;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        jsonDataManager.removeProfile(id);
        jsonDataManager.saveAllData();
        return true;
    }
    
    /**
     * Get all classes - Primary: Database, Fallback: JSON
     */
    public List<GymClass> getAllClasses() {
        try {
            if (useDatabase) {
                return databaseManager.findAllClasses();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        return jsonDataManager.getGymClasses();
    }
    
    /**
     * Get all bookings - Primary: Database, Fallback: JSON
     */
    public List<Booking> getAllBookings() {
        try {
            if (useDatabase) {
                return databaseManager.findAllBookings();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        return jsonDataManager.getBookings();
    }
    
    /**
     * Get all sessions - Primary: Database, Fallback: JSON
     */
    public List<Session> getAllSessions() {
        try {
            if (useDatabase) {
                return databaseManager.findAllSessions();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        return jsonDataManager.getSessions();
    }
    
    /**
     * Get all attendance - Primary: Database, Fallback: JSON
     */
    public List<Attendance> getAllAttendance() {
        try {
            if (useDatabase) {
                return databaseManager.findAllAttendance();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        return jsonDataManager.getAttendanceRecords();
    }
    
    /**
     * Get all trainers - Primary: Database, Fallback: JSON
     */
    public List<Trainer> getAllTrainers() {
        try {
            if (useDatabase) {
                return databaseManager.findAllTrainers();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Database error, falling back to JSON...");
        }
        return jsonDataManager.getTrainers();
    }
    
    // ============================================================
    // SAVE & LOAD (Override BaseController)
    // ============================================================
    
    @Override
    public void saveAllData() {
        try {
            if (useDatabase) {
                databaseManager.saveAllData();
                System.out.println("✅ Data saved to Database");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not save to Database, saving to JSON...");
        }
        jsonDataManager.saveAllData();
    }
    
    @Override
    public void clearAllData() {
        try {
            if (useDatabase) {
                databaseManager.clearAllData();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not clear Database, clearing JSON...");
        }
        jsonDataManager.clearAllData();
    }
}