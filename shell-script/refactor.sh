#!/bin/bash

# =============================================================================
# Script: refactor-to-database.sh
# Purpose: Completely replace FileManager with DatabaseManager for Profile model
# =============================================================================

set -e

echo "🚀 REFACTORING TO DATABASE - PROFILE MODEL"
echo "==========================================="
echo ""

# =============================================================================
# COLOR CODES
# =============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

print_header() { echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; echo -e "${CYAN}▶ $1${NC}"; echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }
print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }
print_info() { echo -e "${CYAN}ℹ️ $1${NC}"; }
print_warning() { echo -e "${YELLOW}⚠️ $1${NC}"; }

# =============================================================================
# STEP 1: CREATE DATABASE CONNECTION CLASS
# =============================================================================

print_header "STEP 1: Creating Database Connection Class"

mkdir -p src/main/java/com/gym/database

cat > src/main/java/com/gym/database/DatabaseConnection.java << 'EOF'
package com.gym.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConnection - Singleton class for database connectivity
 * Handles connection pooling and configuration
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private Properties props;
    
    private static final String PROPERTIES_FILE = "application.properties";
    
    private DatabaseConnection() {
        loadProperties();
        connect();
    }
    
    /**
     * Load database properties from configuration file
     */
    private void loadProperties() {
        props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                // Fallback to default values
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/gym_db");
                props.setProperty("db.user", "root");
                props.setProperty("db.password", "");
                props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                System.out.println("⚠️ " + PROPERTIES_FILE + " not found. Using default values.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading properties: " + e.getMessage());
            // Set defaults
            props.setProperty("db.url", "jdbc:mysql://localhost:3306/gym_db");
            props.setProperty("db.user", "root");
            props.setProperty("db.password", "");
            props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }
    }
    
    /**
     * Establish database connection
     */
    private void connect() {
        try {
            String driver = props.getProperty("db.driver");
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Database connected successfully!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Database driver not found: " + e.getMessage());
            System.out.println("   Make sure MySQL connector is in pom.xml");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            System.out.println("   Check that MySQL is running and credentials are correct.");
        }
    }
    
    /**
     * Get singleton instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection check failed: " + e.getMessage());
            connect();
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }
    
    /**
     * Test connection
     */
    public boolean testConnection() {
        try {
            return getConnection() != null && !getConnection().isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
EOF

print_success "DatabaseConnection.java created"

# =============================================================================
# STEP 2: CREATE PROFILE DATABASE MANAGER
# =============================================================================

print_header "STEP 2: Creating ProfileDatabaseManager"

cat > src/main/java/com/gym/database/ProfileDatabaseManager.java << 'EOF'
package com.gym.database;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProfileDatabaseManager - Handles all Profile CRUD operations
 * Replaces FileManager for Profile data
 */
public class ProfileDatabaseManager {
    
    private Connection connection;
    
    public ProfileDatabaseManager() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    /**
     * Create a new profile in the database
     */
    public Profile createProfile(String name, String email, String phone, String address) {
        String sql = "INSERT INTO profiles (name, email, phone, address, registration_date) " +
                     "VALUES (?, ?, ?, ?, CURDATE())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                System.out.println("❌ Failed to create profile.");
                return null;
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("✅ Profile created with ID: " + id);
                    return findProfileById(id);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error creating profile: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    /**
     * Find profile by ID
     */
    public Profile findProfileById(int id) {
        String sql = "SELECT * FROM profiles WHERE profile_id = ? AND is_active = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProfile(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding profile: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Find profile by email
     */
    public Profile findProfileByEmail(String email) {
        String sql = "SELECT * FROM profiles WHERE email = ? AND is_active = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProfile(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding profile by email: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get all profiles
     */
    public List<Profile> findAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles WHERE is_active = TRUE ORDER BY name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding all profiles: " + e.getMessage());
        }
        return profiles;
    }
    
    /**
     * Search profiles by name or email
     */
    public List<Profile> searchProfiles(String searchTerm) {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles WHERE is_active = TRUE AND " +
                     "(name LIKE ? OR email LIKE ?) ORDER BY name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error searching profiles: " + e.getMessage());
        }
        return profiles;
    }
    
    /**
     * Get active members with valid memberships
     */
    public List<Profile> findActiveMembers() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT p.* FROM profiles p " +
                     "JOIN memberships m ON p.profile_id = m.profile_id " +
                     "WHERE p.is_active = TRUE AND m.status = 'Active' " +
                     "AND m.expiry_date >= CURDATE() ORDER BY p.name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding active members: " + e.getMessage());
        }
        return profiles;
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    /**
     * Update profile
     */
    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET name = ?, email = ?, phone = ?, address = ? " +
                     "WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getEmail());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getAddress());
            stmt.setInt(5, profile.getProfileId());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Profile updated: " + rowsAffected + " row(s) affected");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update profile status (active/inactive)
     */
    public boolean updateProfileStatus(int profileId, boolean isActive) {
        String sql = "UPDATE profiles SET is_active = ? WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, profileId);
            
            int rowsAffected = stmt.executeUpdate();
            String status = isActive ? "activated" : "deactivated";
            System.out.println("✅ Profile " + status + ": " + rowsAffected + " row(s) affected");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating profile status: " + e.getMessage());
            return false;
        }
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    /**
     * Soft delete profile (set is_active = FALSE)
     */
    public boolean deleteProfile(int profileId) {
        return updateProfileStatus(profileId, false);
    }
    
    /**
     * Hard delete profile (permanent removal)
     */
    public boolean hardDeleteProfile(int profileId) {
        String sql = "DELETE FROM profiles WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Profile permanently deleted: " + rowsAffected + " row(s) affected");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error deleting profile: " + e.getMessage());
            return false;
        }
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get profile statistics
     */
    public ProfileStats getStatistics() {
        String sql = "SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) as active, " +
                     "SUM(CASE WHEN is_active = FALSE THEN 1 ELSE 0 END) as inactive " +
                     "FROM profiles";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                return new ProfileStats(
                    rs.getInt("total"),
                    rs.getInt("active"),
                    rs.getInt("inactive")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting statistics: " + e.getMessage());
        }
        return new ProfileStats(0, 0, 0);
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    /**
     * Map ResultSet to Profile object
     */
    private Profile mapResultSetToProfile(ResultSet rs) throws SQLException {
        Profile profile = new Profile(
            rs.getInt("profile_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address")
        );
        profile.setActive(rs.getBoolean("is_active"));
        // Note: Membership is loaded separately
        return profile;
    }
    
    /**
     * Inner class for statistics
     */
    public static class ProfileStats {
        public final int total;
        public final int active;
        public final int inactive;
        
        public ProfileStats(int total, int active, int inactive) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
        }
        
        @Override
        public String toString() {
            return String.format(
                "📊 Profile Statistics:\n" +
                "   Total Profiles: %d\n" +
                "   Active: %d\n" +
                "   Inactive: %d",
                total, active, inactive
            );
        }
    }
}
EOF

print_success "ProfileDatabaseManager.java created"

# =============================================================================
# STEP 3: UPDATE GYM CONTROLLER
# =============================================================================

print_header "STEP 3: Updating GymController"

# Backup original
if [ -f "src/main/java/com/gym/controller/GymController.java" ]; then
    cp src/main/java/com/gym/controller/GymController.java \
       src/main/java/com/gym/controller/GymController.java.bak
    print_success "Backup created: GymController.java.bak"
fi

# Create updated GymController with DatabaseManager support
cat > src/main/java/com/gym/controller/GymController.java << 'EOF'
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
EOF

print_success "GymController.java updated"

# =============================================================================
# STEP 4: UPDATE PROFILE CONTROLLER
# =============================================================================

print_header "STEP 4: Creating ProfileController (Database Version)"

cat > src/main/java/com/gym/controller/ProfileController.java << 'EOF'
package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.persistence.DataManager;
import com.gym.database.ProfileDatabaseManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProfileController - Handles all profile-related operations
 * Supports both Database and File-based persistence
 */
public class ProfileController {
    
    private DataManager dataManager;
    private ProfileDatabaseManager profileDatabaseManager;
    private boolean useDatabase = true;
    
    public ProfileController(DataManager dataManager) {
        this.dataManager = dataManager;
        this.profileDatabaseManager = new ProfileDatabaseManager();
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    /**
     * Create a new member profile
     */
    public Profile createProfile(String name, String email, String phone, String address) {
        if (useDatabase) {
            return profileDatabaseManager.createProfile(name, email, phone, address);
        } else {
            // File-based fallback
            for (Profile p : dataManager.getProfiles()) {
                if (p.getEmail().equalsIgnoreCase(email)) {
                    System.out.println("❌ Email already exists: " + email);
                    return null;
                }
            }
            
            int profileId = dataManager.getProfiles().size() + 1;
            Profile profile = new Profile(profileId, name, email, phone, address);
            dataManager.addProfile(profile);
            dataManager.saveAllData();
            return profile;
        }
    }
    
    /**
     * Create a profile with membership
     */
    public Profile createProfileWithMembership(String name, String email, String phone, 
                                               String address, String membershipType) {
        Profile profile = createProfile(name, email, phone, address);
        if (profile != null) {
            assignMembership(profile.getProfileId(), membershipType);
        }
        return profile;
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    /**
     * Get profile by ID
     */
    public Profile getProfileById(int profileId) {
        if (useDatabase) {
            return profileDatabaseManager.findProfileById(profileId);
        } else {
            return dataManager.findProfileById(profileId);
        }
    }
    
    /**
     * Get profile by email
     */
    public Profile getProfileByEmail(String email) {
        if (useDatabase) {
            return profileDatabaseManager.findProfileByEmail(email);
        } else {
            for (Profile p : dataManager.getProfiles()) {
                if (p.getEmail().equalsIgnoreCase(email)) {
                    return p;
                }
            }
            return null;
        }
    }
    
    /**
     * Get all profiles
     */
    public List<Profile> getAllProfiles() {
        if (useDatabase) {
            return profileDatabaseManager.findAllProfiles();
        } else {
            return dataManager.getProfiles();
        }
    }
    
    /**
     * Get profiles with active memberships
     */
    public List<Profile> getActiveMembers() {
        if (useDatabase) {
            return profileDatabaseManager.findActiveMembers();
        } else {
            return dataManager.getProfiles().stream()
                .filter(p -> p.getMembership() != null && p.getMembership().isValid())
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Search profiles by name or email
     */
    public List<Profile> searchProfiles(String searchTerm) {
        if (useDatabase) {
            return profileDatabaseManager.searchProfiles(searchTerm);
        } else {
            String search = searchTerm.toLowerCase();
            return dataManager.getProfiles().stream()
                .filter(p -> p.getName().toLowerCase().contains(search) ||
                            p.getEmail().toLowerCase().contains(search))
                .collect(Collectors.toList());
        }
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    /**
     * Update profile details
     */
    public boolean updateProfile(int profileId, String name, String email, 
                                 String phone, String address) {
        Profile profile = getProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        profile.updateProfile(name, email, phone, address);
        
        if (useDatabase) {
            return profileDatabaseManager.updateProfile(profile);
        } else {
            dataManager.saveAllData();
            return true;
        }
    }
    
    /**
     * Assign membership to a profile
     */
    public boolean assignMembership(int profileId, String membershipType) {
        Profile profile = getProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        
        // Remove old membership if exists
        if (profile.getMembership() != null) {
            dataManager.getMemberships().remove(profile.getMembership());
        }
        
        // Create new membership
        Membership membership = createMembershipByType(profileId, membershipType);
        if (membership == null) {
            System.out.println("❌ Invalid membership type: " + membershipType);
            return false;
        }
        
        profile.setMembership(membership);
        dataManager.addMembership(membership);
        dataManager.saveAllData();
        
        System.out.println("✅ Membership assigned: " + membershipType);
        return true;
    }
    
    /**
     * Delete a profile
     */
    public boolean deleteProfile(int profileId) {
        if (useDatabase) {
            return profileDatabaseManager.deleteProfile(profileId);
        } else {
            Profile profile = dataManager.findProfileById(profileId);
            if (profile == null) return false;
            
            if (profile.getMembership() != null) {
                dataManager.getMemberships().remove(profile.getMembership());
            }
            dataManager.removeProfile(profileId);
            dataManager.saveAllData();
            return true;
        }
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get profile statistics
     */
    public ProfileDatabaseManager.ProfileStats getStats() {
        if (useDatabase) {
            return profileDatabaseManager.getStatistics();
        } else {
            List<Profile> profiles = dataManager.getProfiles();
            int total = profiles.size();
            int active = (int) profiles.stream()
                .filter(p -> p.getMembership() != null && p.getMembership().isValid())
                .count();
            int inactive = total - active;
            return new ProfileDatabaseManager.ProfileStats(total, active, inactive);
        }
    }
    
    // ============================================================
    // SWITCH METHODS
    // ============================================================
    
    public void switchToDatabase() {
        useDatabase = true;
        System.out.println("✅ ProfileController switched to Database mode");
    }
    
    public void switchToFile() {
        useDatabase = false;
        System.out.println("✅ ProfileController switched to File mode");
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    private Membership createMembershipByType(int profileId, String type) {
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        int membershipId = dataManager.getMemberships().size() + 1000;
        
        switch (type) {
            case "Basic":
                return new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
            case "Premium":
                return new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
            case "Family":
                return new Family(membershipId, 69.99, startDate, expiryDate, "Active", 2);
            default:
                return null;
        }
    }
}
EOF

print_success "ProfileController.java created"

# =============================================================================
# STEP 5: CREATE APPLICATION PROPERTIES
# =============================================================================

print_header "STEP 5: Creating Application Properties"

mkdir -p src/main/resources

cat > src/main/resources/application.properties << 'EOF'
# =============================================================================
# Gym Management System - Application Properties
# =============================================================================

# ===== Database Configuration =====
# MySQL Configuration
db.url=jdbc:mysql://localhost:3306/gym_db
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver

# PostgreSQL Configuration (comment out MySQL and uncomment this)
# db.url=jdbc:postgresql://localhost:5432/gym_db
# db.user=postgres
# db.password=your_password
# db.driver=org.postgresql.Driver

# ===== Connection Pool Settings =====
db.maxPoolSize=10
db.minPoolSize=2
db.connectionTimeout=30000
db.idleTimeout=600000
db.maxLifetime=1800000

# ===== Application Settings =====
app.name=Gym Management System
app.version=1.0.0
app.mode=development

# ===== Logging =====
logging.level=INFO
logging.file=logs/application.log
EOF

print_success "application.properties created"

# =============================================================================
# STEP 6: CREATE TEST CLASS FOR DATABASE
# =============================================================================

print_header "STEP 6: Creating Database Test Class"

mkdir -p src/main/java/com/gym/test

cat > src/main/java/com/gym/test/DatabaseTest.java << 'EOF'
package com.gym.test;

import com.gym.database.DatabaseConnection;
import com.gym.database.ProfileDatabaseManager;
import com.gym.model.Profile;

import java.util.List;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 TESTING DATABASE CONNECTION");
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Test 1: Database Connection
        System.out.println("📁 Test 1: Database Connection");
        System.out.println("-".repeat(40));
        
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (db.testConnection()) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
            System.out.println("   Check your database server and credentials.");
            return;
        }
        System.out.println();
        
        // Test 2: Profile Database Manager
        System.out.println("📁 Test 2: Profile Database Manager");
        System.out.println("-".repeat(40));
        
        ProfileDatabaseManager profileManager = new ProfileDatabaseManager();
        
        // Test 2a: Create Profile
        System.out.println("   Test 2a: Create Profile");
        Profile newProfile = profileManager.createProfile(
            "Test User",
            "test@email.com",
            "555-000-0000",
            "123 Test Street"
        );
        
        if (newProfile != null) {
            System.out.println("   ✅ Profile created:");
            System.out.println("      ID: " + newProfile.getProfileId());
            System.out.println("      Name: " + newProfile.getName());
            System.out.println("      Email: " + newProfile.getEmail());
        }
        System.out.println();
        
        // Test 2b: Find Profile by ID
        System.out.println("   Test 2b: Find Profile by ID");
        if (newProfile != null) {
            Profile found = profileManager.findProfileById(newProfile.getProfileId());
            if (found != null) {
                System.out.println("   ✅ Profile found:");
                System.out.println("      Name: " + found.getName());
                System.out.println("      Email: " + found.getEmail());
            }
        }
        System.out.println();
        
        // Test 2c: List All Profiles
        System.out.println("   Test 2c: List All Profiles");
        List<Profile> profiles = profileManager.findAllProfiles();
        System.out.println("   ✅ Found " + profiles.size() + " profiles:");
        for (Profile p : profiles) {
            System.out.println("      " + p.getProfileId() + ". " + p.getName() + 
                             " (" + p.getEmail() + ")");
        }
        System.out.println();
        
        // Test 2d: Statistics
        System.out.println("   Test 2d: Statistics");
        ProfileDatabaseManager.ProfileStats stats = profileManager.getStatistics();
        System.out.println(stats.toString());
        System.out.println();
        
        System.out.println("=".repeat(60));
        System.out.println("✅ All tests completed!");
        System.out.println("=".repeat(60));
    }
}
EOF

print_success "DatabaseTest.java created"

# =============================================================================
# STEP 7: UPDATE POM.XML IF NEEDED
# =============================================================================

print_header "STEP 7: Updating pom.xml"

if ! grep -q "mysql-connector" pom.xml; then
    print_info "Adding MySQL connector to pom.xml..."
    
    # Add MySQL connector before closing </dependencies>
    sed -i '/<\/dependencies>/i\
        <!-- MySQL Connector -->\
        <dependency>\
            <groupId>mysql</groupId>\
            <artifactId>mysql-connector-java</artifactId>\
            <version>8.0.33</version>\
        </dependency>' pom.xml
    
    print_success "MySQL connector added to pom.xml"
else
    print_success "MySQL connector already in pom.xml"
fi

# =============================================================================
# STEP 8: COMPILE AND TEST
# =============================================================================

print_header "STEP 8: Compiling Project"

echo "Running: mvn clean compile"
mvn clean compile -q

if [ $? -eq 0 ]; then
    print_success "Compilation successful!"
else
    print_error "Compilation failed. Please check the errors above."
    exit 1
fi

# =============================================================================
# STEP 9: RUN DATABASE TEST
# =============================================================================

print_header "STEP 9: Running Database Test"

echo ""
echo "Running: mvn exec:java -Dexec.mainClass=\"com.gym.test.DatabaseTest\""
echo ""
mvn exec:java -Dexec.mainClass="com.gym.test.DatabaseTest"

# =============================================================================
# SUMMARY
# =============================================================================

print_header "REFACTORING COMPLETE"

echo -e "${GREEN}✅ All tasks completed successfully!${NC}"
echo ""
echo -e "${BLUE}What was added/updated:${NC}"
echo "  1. DatabaseConnection.java - Singleton database connection manager"
echo "  2. ProfileDatabaseManager.java - Database CRUD operations for Profile"
echo "  3. Updated GymController.java - Added database support"
echo "  4. Updated ProfileController.java - Supports both database and file"
echo "  5. application.properties - Database configuration file"
echo "  6. DatabaseTest.java - Test class for database operations"
echo "  7. Updated pom.xml - Added MySQL connector"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Create the database schema by running schema.sql"
echo "  2. Update application.properties with your database credentials"
echo "  3. Run: mvn clean compile"
echo "  4. Run: mvn exec:java -Dexec.mainClass=\"com.gym.test.DatabaseTest\""
echo "  5. If tests pass, your refactoring is complete!"
echo ""
echo -e "${YELLOW}⚠️ Notes:${NC}"
echo "  • The system now supports both Database and File persistence"
echo "  • To switch modes, use: gymController.switchToDatabase() or switchToFile()"
echo "  • Default mode is Database"
echo "  • Make sure MySQL is running before testing"
echo ""

print_success "🚀 Script complete!"