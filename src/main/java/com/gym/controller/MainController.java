package com.gym.controller;

import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;  // ← IMPORTANT: Add this import

public class MainController {
    
    private final DatabaseManager dataManager;  // Keep as final
    private final LoginController loginController;
    private final ProfileController profileController;
    private final AdminController adminController;
    private final MembershipController membershipController;
    private final ClassController classController;
    private final BookingController bookingController;
    private final AttendanceController attendanceController;
    private final PaymentController paymentController;
    private final ReportController reportController;
    private final GymController gymController;
    
    public MainController() {
        // Initialize data manager using helper method (assigns ONCE)
        this.dataManager = initializeDataManager();
        
        // Initialize all controllers
        this.loginController = new LoginController(dataManager);
        this.profileController = new ProfileController(dataManager);
        this.adminController = new AdminController(dataManager);
        this.membershipController = new MembershipController(dataManager);
        this.classController = new ClassController(new GymController(dataManager));
        this.bookingController = new BookingController(dataManager);
        this.attendanceController = new AttendanceController(dataManager);
        this.paymentController = new PaymentController(dataManager);
        this.reportController = new ReportController(dataManager);
        this.gymController = new GymController(dataManager);
        
        System.out.println("✅ MainController initialized successfully");
        System.out.println("📊 Using: " + getDataManagerType());
    }
    
    /**
     * Initialize the data manager - Try database first, fallback to JSON
     */
    private DatabaseManager initializeDataManager() {
        // Try to connect to database
        DatabaseManager dbManager = new DatabaseManager();
        
        // Check if connection was successful
        if (dbManager.getConnection() != null) {
            System.out.println("✅ Connected to MySQL database");
            return dbManager;  // Return the database manager
        }
        
        // Fallback to JSON storage
        System.out.println("⚠️ No database connection - using JSON fallback");
        JsonDataManager jsonManager = new JsonDataManager();  // ← This now resolves
        System.out.println("✅ JSON data manager initialized");
        return jsonManager;  // Return the JSON manager
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public DatabaseManager getDataManager() {
        return dataManager;
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
    
    public PaymentController getPaymentController() {
        return paymentController;
    }
    
    public ReportController getReportController() {
        return reportController;
    }
    
    public GymController getGymController() {
        return gymController;
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    public boolean isUsingDatabase() {
        return !(dataManager instanceof JsonDataManager);
    }
    
    public String getDataManagerType() {
        return dataManager instanceof JsonDataManager ? "JSON" : "MySQL";
    }
    
    public void saveAllData() {
        dataManager.saveAllData();
    }
    
    public void loadAllData() {
        dataManager.loadAllData();
    }
    
    public void shutdown() {
        if (dataManager != null) {
            dataManager.saveAllData();
            dataManager.closeConnection();
        }
        System.out.println("👋 Application shutdown complete");
    }
}