#!/bin/bash

# =============================================================================
# Script: setup-sql-connection.sh
# Purpose: Complete SQL connection setup for XAMPP MySQL
# =============================================================================

set -e

echo "🔧 SETTING UP SQL CONNECTION (XAMPP)"
echo "===================================="
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
# STEP 1: CHECK XAMPP
# =============================================================================

print_header "STEP 1: Checking XAMPP MySQL"

print_info "Checking if MySQL is running in XAMPP..."

# Check if MySQL is running on port 3306
if netstat -ano | findstr :3306 > /dev/null 2>&1; then
    print_success "MySQL is running on port 3306"
else
    print_warning "MySQL does not appear to be running on port 3306"
    echo ""
    echo "Please start MySQL in XAMPP:"
    echo "  1. Open XAMPP Control Panel"
    echo "  2. Click 'Start' on MySQL row"
    echo "  3. Wait for the status to turn green"
    echo ""
    read -p "Press Enter after starting MySQL..."
fi

# =============================================================================
# STEP 2: CREATE DATABASE
# =============================================================================

print_header "STEP 2: Creating Database"

# MySQL connection details (XAMPP defaults)
DB_USER="root"
DB_PASS=""
DB_HOST="localhost"
DB_NAME="gym_db"

print_info "Creating database '$DB_NAME'..."

mysql -u "$DB_USER" -h "$DB_HOST" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;" 2>/dev/null

if [ $? -eq 0 ]; then
    print_success "Database '$DB_NAME' created or already exists"
else
    print_warning "Could not create database automatically"
    echo "   Please create it manually in phpMyAdmin:"
    echo "   http://localhost/phpmyadmin"
    echo "   Create database named: $DB_NAME"
fi

# =============================================================================
# STEP 3: CREATE DATABASE SCHEMA
# =============================================================================

print_header "STEP 3: Creating Database Schema"

cat > src/main/resources/database/schema.sql << 'EOF'
-- =============================================================================
-- GYM MANAGEMENT SYSTEM - DATABASE SCHEMA
-- =============================================================================

-- Use the database
USE gym_db;

-- =============================================================================
-- PROFILES TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS profiles (
    profile_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================================================
-- MEMBERSHIPS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS memberships (
    membership_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    membership_type ENUM('Basic', 'Premium', 'Family') NOT NULL,
    fee DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status ENUM('Active', 'Expired', 'Cancelled') DEFAULT 'Active',
    benefits TEXT,
    number_of_members INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- =============================================================================
-- TRAINERS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS trainers (
    trainer_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    hire_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- =============================================================================
-- GYM CLASSES TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS gym_classes (
    class_id INT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(100) NOT NULL,
    class_type ENUM('Yoga', 'Spin', 'Strength') NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 15,
    trainer_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    yoga_style VARCHAR(50),
    difficulty VARCHAR(20),
    intensity VARCHAR(20),
    duration_minutes INT,
    focus_area VARCHAR(50),
    equipment_needed TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT
);

-- =============================================================================
-- SESSIONS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    class_id INT NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration VARCHAR(20),
    trainer_id INT NOT NULL,
    max_attendees INT DEFAULT 30,
    current_attendees INT DEFAULT 0,
    status ENUM('Scheduled', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES gym_classes(class_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT
);

-- =============================================================================
-- BOOKINGS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    session_id INT NOT NULL,
    booking_date DATE NOT NULL,
    status ENUM('Confirmed', 'Pending', 'Cancelled', 'Completed', 'Waitlisted') DEFAULT 'Pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_booking (profile_id, session_id)
);

-- =============================================================================
-- ATTENDANCE TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    session_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('Present', 'Absent', 'Late', 'Excused') DEFAULT 'Present',
    check_in_time TIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance (profile_id, session_id)
);

-- =============================================================================
-- PAYMENTS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status ENUM('Pending', 'Completed', 'Failed', 'Refunded') DEFAULT 'Pending',
    transaction_id VARCHAR(100) UNIQUE,
    receipt_path VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- =============================================================================
-- INSERT SAMPLE DATA
-- =============================================================================
INSERT IGNORE INTO profiles (profile_id, name, email, phone, address, registration_date) VALUES
(1, 'John Doe', 'john@email.com', '555-123-4567', '123 Main St', CURDATE()),
(2, 'Sarah Smith', 'sarah@email.com', '555-987-6543', '456 Oak Ave', CURDATE()),
(3, 'Admin User', 'admin@gym.com', '555-000-0000', 'Admin Office', CURDATE());

INSERT IGNORE INTO trainers (profile_id, specialization, user_id, password_hash, hire_date) VALUES
(1, 'Yoga', 'T001', 'trainer123_hash', CURDATE());

INSERT IGNORE INTO memberships (profile_id, membership_type, fee, start_date, expiry_date, status) VALUES
(1, 'Basic', 49.99, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active'),
(2, 'Premium', 99.99, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active');

INSERT IGNORE INTO gym_classes (class_id, class_name, class_type, schedule, capacity, trainer_id, yoga_style, difficulty) VALUES
(101, 'Morning Yoga', 'Yoga', 'Mon/Wed/Fri 7:00 AM', 15, 1, 'Hatha', 'Beginner');

INSERT IGNORE INTO sessions (class_id, session_date, start_time, end_time, duration, trainer_id, max_attendees) VALUES
(101, CURDATE(), '07:00:00', '08:00:00', '1 hour', 1, 15);

EOF

print_success "Database schema created at: src/main/resources/database/schema.sql"

# =============================================================================
# STEP 4: RUN THE SCHEMA
# =============================================================================

print_header "STEP 4: Running Database Schema"

if mysql -u "$DB_USER" -h "$DB_HOST" "$DB_NAME" < src/main/resources/database/schema.sql 2>/dev/null; then
    print_success "Database schema applied successfully!"
else
    print_warning "Could not apply schema automatically"
    echo "   Please run it manually in phpMyAdmin:"
    echo "   http://localhost/phpmyadmin"
    echo "   Import: src/main/resources/database/schema.sql"
fi

# =============================================================================
# STEP 5: CREATE DATABASE CONNECTION CLASS
# =============================================================================

print_header "STEP 5: Creating DatabaseConnection.java"

mkdir -p src/main/java/com/gym/database

cat > src/main/java/com/gym/database/DatabaseConnection.java << 'EOF'
package com.gym.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseConnection - Singleton class for XAMPP MySQL connectivity
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    // XAMPP Default Settings
    private static final String URL = "jdbc:mysql://localhost:3306/gym_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";  // XAMPP default is empty
    
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found!");
            System.out.println("   Add the MySQL connector to pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            System.out.println("   Make sure MySQL is running in XAMPP.");
            System.out.println("   Check: http://localhost/phpmyadmin");
            e.printStackTrace();
        }
    }
    
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
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                reconnect();
            }
        } catch (SQLException e) {
            reconnect();
        }
        return connection;
    }
    
    private void reconnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("❌ Reconnection failed!");
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection.");
        }
    }
}
EOF

print_success "DatabaseConnection.java created"

# =============================================================================
# STEP 6: CREATE DATABASE MANAGER
# =============================================================================

print_header "STEP 6: Creating DatabaseManager.java"

cat > src/main/java/com/gym/database/DatabaseManager.java << 'EOF'
package com.gym.database;

import com.gym.model.Profile;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager - Handles all Profile CRUD operations
 */
public class DatabaseManager {
    
    private Connection connection;
    
    public DatabaseManager() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    // ===== PROFILE OPERATIONS =====
    
    public Profile createProfile(String name, String email, String phone, String address) {
        String sql = "INSERT INTO profiles (name, email, phone, address, registration_date) VALUES (?, ?, ?, ?, CURDATE())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        return findProfileById(id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Profile findProfileById(int id) {
        String sql = "SELECT * FROM profiles WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Profile profile = new Profile(
                    rs.getInt("profile_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                profile.setActive(rs.getBoolean("is_active"));
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Profile> findAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles ORDER BY name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Profile profile = new Profile(
                    rs.getInt("profile_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                profile.setActive(rs.getBoolean("is_active"));
                profiles.add(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
    
    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET name = ?, email = ?, phone = ?, address = ? WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getEmail());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getAddress());
            stmt.setInt(5, profile.getProfileId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteProfile(int id) {
        String sql = "DELETE FROM profiles WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
EOF

print_success "DatabaseManager.java created"

# =============================================================================
# STEP 7: CREATE TEST FILE
# =============================================================================

print_header "STEP 7: Creating Database Test"

mkdir -p src/main/java/com/gym/test

cat > src/main/java/com/gym/test/DatabaseTest.java << 'EOF'
package com.gym.test;

import com.gym.database.DatabaseConnection;
import com.gym.database.DatabaseManager;
import com.gym.model.Profile;

import java.util.List;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("🔍 TESTING DATABASE CONNECTION");
        System.out.println("=".repeat(50));
        
        // Test 1: Connection
        System.out.println("\n📁 Test 1: Database Connection");
        DatabaseConnection db = DatabaseConnection.getInstance();
        if (db.getConnection() != null) {
            System.out.println("✅ Connection successful!");
        } else {
            System.out.println("❌ Connection failed!");
            return;
        }
        
        // Test 2: DatabaseManager
        System.out.println("\n📁 Test 2: DatabaseManager");
        DatabaseManager dm = new DatabaseManager();
        
        // Test 2a: Create Profile
        System.out.println("   Creating test profile...");
        Profile newProfile = dm.createProfile("Test User", "test@email.com", "555-000-0000", "123 Test St");
        if (newProfile != null) {
            System.out.println("   ✅ Profile created: ID " + newProfile.getProfileId());
        }
        
        // Test 2b: List Profiles
        System.out.println("   Listing all profiles...");
        List<Profile> profiles = dm.findAllProfiles();
        System.out.println("   ✅ Found " + profiles.size() + " profiles");
        for (Profile p : profiles) {
            System.out.println("      " + p.getProfileId() + ". " + p.getName());
        }
        
        System.out.println("\n✅ All tests completed!");
    }
}
EOF

print_success "DatabaseTest.java created"

# =============================================================================
# STEP 8: UPDATE POM.XML
# =============================================================================

print_header "STEP 8: Checking pom.xml"

if ! grep -q "mysql-connector" pom.xml; then
    print_info "Adding MySQL connector to pom.xml..."
    
    sed -i '/<\/dependencies>/i\
        <!-- MySQL Connector for XAMPP -->\
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
# STEP 9: COMPILE AND TEST
# =============================================================================

print_header "STEP 9: Compiling and Testing"

mvn clean compile -q

if [ $? -eq 0 ]; then
    print_success "Compilation successful!"
else
    print_error "Compilation failed"
    exit 1
fi

echo ""
print_info "Running Database Test..."
mvn exec:java -Dexec.mainClass="com.gym.test.DatabaseTest"

# =============================================================================
# SUMMARY
# =============================================================================

print_header "SQL SETUP COMPLETE!"

echo -e "${GREEN}✅ SQL Connection setup complete!${NC}"
echo ""
echo -e "${BLUE}What was created:${NC}"
echo "  1. Database schema (gym_db)"
echo "  2. DatabaseConnection.java (XAMPP connection)"
echo "  3. DatabaseManager.java (CRUD operations)"
echo "  4. DatabaseTest.java (Test connection)"
echo "  5. Updated pom.xml (MySQL connector)"
echo ""
echo -e "${BLUE}XAMPP Connection Details:${NC}"
echo "  URL:      jdbc:mysql://localhost:3306/gym_db"
echo "  User:     root"
echo "  Password: (empty)"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Start MySQL in XAMPP"
echo "  2. Run: mvn clean compile"
echo "  3. Run: mvn exec:java -Dexec.mainClass=\"com.gym.test.DatabaseTest\""
echo "  4. Run: mvn javafx:run"
echo ""
echo -e "${GREEN}🚀 Your SQL connection is ready!${NC}"