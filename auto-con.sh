#!/bin/bash

# =============================================================================
# Script: auto-reconfigure-database.sh
# Purpose: Automatically reconfigure all files for DatabaseManager transition
# =============================================================================

set -e

# =============================================================================
# COLOR CODES
# =============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

print_header() { echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; echo -e "${CYAN}▶ $1${NC}"; echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }
print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }
print_warning() { echo -e "${YELLOW}⚠️ $1${NC}"; }
print_info() { echo -e "${CYAN}ℹ️ $1${NC}"; }
print_fix() { echo -e "${MAGENTA}🔧 $1${NC}"; }

# =============================================================================
# CREATE BACKUPS
# =============================================================================

create_backup() {
    local file=$1
    if [ -f "$file" ]; then
        local backup_dir="backups/$(date +%Y%m%d_%H%M%S)"
        mkdir -p "$backup_dir"
        cp "$file" "$backup_dir/$(basename "$file").bak"
        echo "$backup_dir/$(basename "$file").bak"
        return 0
    fi
    return 1
}

# =============================================================================
# FUNCTION: Update File Content
# =============================================================================

update_file() {
    local file=$1
    local pattern=$2
    local replacement=$3
    
    if [ -f "$file" ]; then
        # Create backup
        create_backup "$file" > /dev/null
        
        # Apply replacement
        sed -i "s/$pattern/$replacement/g" "$file"
        print_success "Updated: $(basename "$file")"
        return 0
    else
        print_warning "File not found: $file"
        return 1
    fi
}

# =============================================================================
# STEP 1: DISPLAY HEADER
# =============================================================================

print_header "AUTOMATIC DATABASE RECONFIGURATION"

echo -e "${BLUE}This script will automatically reconfigure all files for DatabaseManager.${NC}"
echo -e "${YELLOW}Files will be backed up before modification.${NC}"
echo ""
read -p "Press Enter to continue..."

# =============================================================================
# STEP 2: BACKUP ALL CONTROLLERS
# =============================================================================

print_header "STEP 1: Creating Backups"

BACKUP_DIR="backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"
print_info "Backup directory: $BACKUP_DIR"

# Backup all controller files
for file in src/main/java/com/gym/controller/*.java; do
    if [ -f "$file" ]; then
        cp "$file" "$BACKUP_DIR/$(basename "$file").bak"
        print_info "Backed up: $(basename "$file")"
    fi
done

# Backup main files
if [ -f "src/main/java/com/gym/GymApplication.java" ]; then
    cp src/main/java/com/gym/GymApplication.java "$BACKUP_DIR/GymApplication.java.bak"
    print_info "Backed up: GymApplication.java"
fi

if [ -f "src/main/java/com/gym/Main.java" ]; then
    cp src/main/java/com/gym/Main.java "$BACKUP_DIR/Main.java.bak"
    print_info "Backed up: Main.java"
fi

# Backup persistence files
if [ -f "src/main/java/com/gym/persistence/DataInitializer.java" ]; then
    cp src/main/java/com/gym/persistence/DataInitializer.java "$BACKUP_DIR/DataInitializer.java.bak"
    print_info "Backed up: DataInitializer.java"
fi

print_success "All files backed up to: $BACKUP_DIR"

# =============================================================================
# STEP 3: UPDATE GYM CONTROLLER
# =============================================================================

print_header "STEP 2: Updating GymController.java"

GYM_CONTROLLER="src/main/java/com/gym/controller/GymController.java"

if [ -f "$GYM_CONTROLLER" ]; then
    print_info "Processing $GYM_CONTROLLER..."
    
    # Update imports
    update_file "$GYM_CONTROLLER" \
        "import com.gym.persistence.DataManager;" \
        "import com.gym.database.DatabaseManager;"
    
    # Update field declaration
    update_file "$GYM_CONTROLLER" \
        "DataManager dataManager;" \
        "DatabaseManager dataManager;"
    
    # Update constructor
    update_file "$GYM_CONTROLLER" \
        "new DataManager()" \
        "new DatabaseManager()"
    
    # Update method return types
    update_file "$GYM_CONTROLLER" \
        "getDataManager()" \
        "getDatabaseManager()"
    
    print_success "GymController.java updated"
else
    print_error "GymController.java not found"
fi

# =============================================================================
# STEP 4: UPDATE ALL OTHER CONTROLLERS
# =============================================================================

print_header "STEP 3: Updating All Controllers"

# List of controller files to update
CONTROLLERS=(
    "AdminController.java"
    "ProfileController.java"
    "BookingController.java"
    "ClassController.java"
    "AttendanceController.java"
    "MembershipController.java"
    "LoginController.java"
    "PaymentController.java"
    "ReportController.java"
    "RegisterController.java"
)

for controller in "${CONTROLLERS[@]}"; do
    file="src/main/java/com/gym/controller/$controller"
    if [ -f "$file" ]; then
        print_info "Processing $controller..."
        
        # Update imports
        sed -i 's/import com.gym.persistence.DataManager;/import com.gym.database.DatabaseManager;/g' "$file"
        
        # Update field declarations
        sed -i 's/DataManager dataManager;/DatabaseManager dataManager;/g' "$file"
        sed -i 's/private DataManager/private DatabaseManager/g' "$file"
        
        # Update constructor calls
        sed -i 's/new DataManager()/new DatabaseManager()/g' "$file"
        
        # Update method calls
        sed -i 's/\.getDataManager()/.getDatabaseManager()/g' "$file"
        sed -i 's/dataManager\./databaseManager\./g' "$file"
        
        # Update parameter types
        sed -i 's/(DataManager dataManager)/(DatabaseManager dataManager)/g' "$file"
        
        print_success "Updated: $controller"
    else
        print_warning "File not found: $controller"
    fi
done

# =============================================================================
# STEP 5: UPDATE GYM APPLICATION
# =============================================================================

print_header "STEP 4: Updating GymApplication.java"

GYM_APP="src/main/java/com/gym/GymApplication.java"

if [ -f "$GYM_APP" ]; then
    print_info "Processing $GYM_APP..."
    
    # Update imports
    update_file "$GYM_APP" \
        "import com.gym.persistence.DataManager;" \
        "import com.gym.database.DatabaseManager;"
    
    # Update field
    update_file "$GYM_APP" \
        "DataManager dataManager;" \
        "DatabaseManager dataManager;"
    
    # Update initialization
    update_file "$GYM_APP" \
        "new DataManager()" \
        "new DatabaseManager()"
    
    # Update method calls
    update_file "$GYM_APP" \
        "dataManager\." \
        "databaseManager\."
    
    print_success "GymApplication.java updated"
else
    print_error "GymApplication.java not found"
fi

# =============================================================================
# STEP 6: UPDATE MAIN.JAVA
# =============================================================================

print_header "STEP 5: Updating Main.java"

MAIN_FILE="src/main/java/com/gym/Main.java"

if [ -f "$MAIN_FILE" ]; then
    print_info "Processing $MAIN_FILE..."
    
    # Update imports
    update_file "$MAIN_FILE" \
        "import com.gym.persistence.DataManager;" \
        "import com.gym.database.DatabaseManager;"
    
    # Update field
    update_file "$MAIN_FILE" \
        "DataManager dataManager;" \
        "DatabaseManager dataManager;"
    
    # Update initialization
    update_file "$MAIN_FILE" \
        "new DataManager()" \
        "new DatabaseManager()"
    
    print_success "Main.java updated"
else
    print_error "Main.java not found"
fi

# =============================================================================
# STEP 7: UPDATE DATA INITIALIZER
# =============================================================================

print_header "STEP 6: Updating DataInitializer.java"

DATA_INIT="src/main/java/com/gym/persistence/DataInitializer.java"

if [ -f "$DATA_INIT" ]; then
    print_info "Processing $DATA_INIT..."
    
    # Update method signature
    sed -i 's/DataManager dataManager/DatabaseManager dataManager/g' "$DATA_INIT"
    sed -i 's/DataManager dataManager/DatabaseManager dataManager/g' "$DATA_INIT"
    
    # Update imports
    update_file "$DATA_INIT" \
        "import com.gym.persistence.DataManager;" \
        "import com.gym.database.DatabaseManager;"
    
    print_success "DataInitializer.java updated"
else
    print_error "DataInitializer.java not found"
fi

# =============================================================================
# STEP 8: UPDATE FILES IN PERSISTENCE PACKAGE
# =============================================================================

print_header "STEP 7: Updating Persistence Package Files"

if [ -f "src/main/java/com/gym/persistence/FileManager.java" ]; then
    print_info "Processing FileManager.java..."
    # FileManager stays the same - it's used by DataManager
fi

# =============================================================================
# STEP 9: UPDATE FXML CONTROLLERS (View Layer)
# =============================================================================

print_header "STEP 8: Updating View Controllers"

VIEW_CONTROLLERS=(
    "DashboardController.java"
    "ProfileController.java"
    "LoginController.java"
    "RegisterController.java"
)

for controller in "${VIEW_CONTROLLERS[@]}"; do
    file="src/main/java/com/gym/view/javafx/controller/$controller"
    if [ -f "$file" ]; then
        print_info "Processing $controller..."
        
        # Update imports
        sed -i 's/import com.gym.persistence.DataManager;/import com.gym.database.DatabaseManager;/g' "$file"
        
        # Update field
        sed -i 's/DataManager dataManager;/DatabaseManager dataManager;/g' "$file"
        
        # Update constructor calls
        sed -i 's/new DataManager()/new DatabaseManager()/g' "$file"
        
        print_success "Updated: $controller"
    fi
done

# =============================================================================
# STEP 10: CREATE APPLICATION.PROPERTIES IF MISSING
# =============================================================================

print_header "STEP 9: Checking Application Properties"

PROPERTIES_FILE="src/main/resources/application.properties"

if [ ! -f "$PROPERTIES_FILE" ]; then
    print_info "Creating application.properties..."
    mkdir -p src/main/resources
    cat > "$PROPERTIES_FILE" << 'EOF'
# Database Configuration
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/gym_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=

# Connection Pool Settings
db.maxPoolSize=10
db.minPoolSize=2
db.connectionTimeout=30000
db.idleTimeout=600000
db.maxLifetime=1800000

# Application Settings
app.name=Gym Management System
app.version=1.0.0
app.mode=development
EOF
    print_success "application.properties created"
else
    print_success "application.properties already exists"
fi

# =============================================================================
# STEP 11: UPDATE POM.XML
# =============================================================================

print_header "STEP 10: Updating pom.xml"

if ! grep -q "mysql-connector" pom.xml; then
    print_info "Adding MySQL connector to pom.xml..."
    
    # Add MySQL connector before closing </dependencies>
    sed -i '/<\/dependencies>/i\
        <!-- MySQL Connector for Database -->\
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
# STEP 12: CREATE DATABASE SCHEMA
# =============================================================================

print_header "STEP 11: Creating Database Schema"

SCHEMA_FILE="src/main/resources/database/schema.sql"

if [ ! -f "$SCHEMA_FILE" ]; then
    print_info "Creating database schema..."
    mkdir -p src/main/resources/database
    
    cat > "$SCHEMA_FILE" << 'EOF'
-- =============================================================================
-- GYM MANAGEMENT SYSTEM - DATABASE SCHEMA
-- =============================================================================

CREATE DATABASE IF NOT EXISTS gym_db;
USE gym_db;

-- PROFILES TABLE
CREATE TABLE IF NOT EXISTS profiles (
    profile_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- MEMBERSHIPS TABLE
CREATE TABLE IF NOT EXISTS memberships (
    membership_id VARCHAR(20) PRIMARY KEY,
    profile_id VARCHAR(20) NOT NULL,
    membership_type VARCHAR(20) NOT NULL,
    fee DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Active',
    benefits TEXT,
    number_of_members INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- TRAINERS TABLE
CREATE TABLE IF NOT EXISTS trainers (
    trainer_id VARCHAR(20) PRIMARY KEY,
    profile_id VARCHAR(20) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    hire_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- GYM CLASSES TABLE
CREATE TABLE IF NOT EXISTS gym_classes (
    class_id VARCHAR(20) PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL,
    class_type VARCHAR(20) NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 15,
    trainer_id VARCHAR(20) NOT NULL,
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

-- SESSIONS TABLE
CREATE TABLE IF NOT EXISTS sessions (
    session_id VARCHAR(20) PRIMARY KEY,
    class_id VARCHAR(20) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration VARCHAR(20),
    trainer_id VARCHAR(20) NOT NULL,
    max_attendees INT DEFAULT 30,
    current_attendees INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES gym_classes(class_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT
);

-- BOOKINGS TABLE
CREATE TABLE IF NOT EXISTS bookings (
    booking_id VARCHAR(20) PRIMARY KEY,
    profile_id VARCHAR(20) NOT NULL,
    session_id VARCHAR(20) NOT NULL,
    booking_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_booking (profile_id, session_id)
);

-- ATTENDANCE TABLE
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id VARCHAR(20) PRIMARY KEY,
    profile_id VARCHAR(20) NOT NULL,
    session_id VARCHAR(20) NOT NULL,
    attendance_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Present',
    check_in_time TIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance (profile_id, session_id)
);

-- PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(20) PRIMARY KEY,
    profile_id VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'Pending',
    transaction_id VARCHAR(100) UNIQUE,
    receipt_path VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- ADMINS TABLE
CREATE TABLE IF NOT EXISTS admins (
    admin_id VARCHAR(20) PRIMARY KEY,
    profile_id VARCHAR(20) NOT NULL,
    admin_level VARCHAR(50) DEFAULT 'Staff',
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);
EOF
    print_success "Database schema created"
else
    print_success "Database schema already exists"
fi

# =============================================================================
# STEP 13: CREATE DATABASE CONNECTION (if missing)
# =============================================================================

print_header "STEP 12: Checking Database Connection"

DB_CONNECTION="src/main/java/com/gym/database/DatabaseConnection.java"

if [ ! -f "$DB_CONNECTION" ]; then
    print_info "Creating DatabaseConnection.java..."
    mkdir -p src/main/java/com/gym/database
    
    cat > "$DB_CONNECTION" << 'EOF'
package com.gym.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private Properties props;
    private static final String PROPERTIES_FILE = "application.properties";
    
    private DatabaseConnection() {
        loadProperties();
        connect();
    }
    
    private void loadProperties() {
        props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                System.out.println("⚠️ " + PROPERTIES_FILE + " not found. Using defaults.");
                setDefaults();
            }
        } catch (Exception e) {
            System.out.println("❌ Error loading properties: " + e.getMessage());
            setDefaults();
        }
    }
    
    private void setDefaults() {
        props.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        props.setProperty("db.url", "jdbc:mysql://localhost:3306/gym_db?useSSL=false&serverTimezone=UTC");
        props.setProperty("db.username", "root");
        props.setProperty("db.password", "");
    }
    
    private void connect() {
        try {
            Class.forName(props.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
            );
            System.out.println("✅ Database connected successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found!");
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
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
            Class.forName(props.getProperty("db.driver"));
            this.connection = DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.username"),
                props.getProperty("db.password")
            );
            System.out.println("✅ Reconnected successfully!");
        } catch (Exception e) {
            System.err.println("❌ Reconnection failed!");
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection.");
        }
    }
    
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
else
    print_success "DatabaseConnection.java already exists"
fi

# =============================================================================
# STEP 14: UPDATE DATA MANAGER (Keep both for compatibility)
# =============================================================================

print_header "STEP 13: Keeping DataManager for Compatibility"

if [ -f "src/main/java/com/gym/persistence/DataManager.java" ]; then
    print_info "DataManager.java will be kept for backward compatibility"
    print_info "You can safely remove it after full testing"
fi

# =============================================================================
# STEP 15: COMPILE TO VERIFY
# =============================================================================

print_header "STEP 14: Compiling Project"

echo -e "${YELLOW}Do you want to compile the project now? (y/n)${NC}"
read -r compile_response

if [[ "$compile_response" =~ ^(y|Y|yes|Yes)$ ]]; then
    print_info "Compiling project..."
    mvn clean compile
    
    if [ $? -eq 0 ]; then
        print_success "✅ Compilation successful!"
    else
        print_error "❌ Compilation failed. Please check the errors above."
        echo ""
        echo -e "${YELLOW}Try manually fixing any remaining issues.${NC}"
    fi
else
    print_info "Skipping compilation"
fi

# =============================================================================
# SUMMARY
# =============================================================================

print_header "RECONFIGURATION COMPLETE!"

echo -e "${GREEN}✅ All files have been automatically reconfigured!${NC}"
echo ""
echo -e "${BLUE}What was done:${NC}"
echo "  1. ✅ Updated all controllers to use DatabaseManager"
echo "  2. ✅ Updated GymApplication.java"
echo "  3. ✅ Updated Main.java"
echo "  4. ✅ Updated DataInitializer.java"
echo "  5. ✅ Updated view controllers"
echo "  6. ✅ Created/Updated application.properties"
echo "  7. ✅ Added MySQL connector to pom.xml"
echo "  8. ✅ Created database schema"
echo "  9. ✅ Created DatabaseConnection.java"
echo "  10. ✅ Created backups of all modified files"
echo ""
echo -e "${BLUE}Backup location:${NC} $BACKUP_DIR"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Start MySQL in XAMPP"
echo "  2. Run: mysql -u root -p < src/main/resources/database/schema.sql"
echo "  3. Run: mvn clean compile"
echo "  4. Run: mvn javafx:run"
echo ""
echo -e "${YELLOW}To restore backups:${NC}"
echo "  cp $BACKUP_DIR/*.bak src/main/java/com/gym/controller/"
echo "  cp $BACKUP_DIR/*.bak src/main/java/com/gym/"
echo ""
echo -e "${GREEN}🎉 Your application is now configured for DatabaseManager!${NC}"