-- =============================================================================
-- GYM MANAGEMENT SYSTEM - COMPLETE DATABASE SCHEMA
-- =============================================================================

CREATE DATABASE IF NOT EXISTS gym_db;
USE gym_db;

SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables in reverse order
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS gym_classes;
DROP TABLE IF EXISTS memberships;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS trainers;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS profiles;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1. ID COUNTER TABLE (For sequential ID generation)
-- =============================================================================
CREATE TABLE IF NOT EXISTS id_counter (
    id_type VARCHAR(20) PRIMARY KEY,
    last_sequence INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Initialize counters with 0
INSERT IGNORE INTO id_counter (id_type, last_sequence) VALUES
('profile', 0),
('membership', 0),
('class', 0),
('session', 0),
('booking', 0),
('attendance', 0),
('payment', 0);

-- =============================================================================
-- 2. PROFILES TABLE (Base for all users)
-- =============================================================================
CREATE TABLE IF NOT EXISTS profiles (
    profile_id VARCHAR(10) PRIMARY KEY,  -- Format: [4-digit counter][YYMMDD][role]
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 3. ADMINS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS admins (
    admin_id VARCHAR(10) PRIMARY KEY,
    profile_id VARCHAR(10) NOT NULL,
    admin_level VARCHAR(50) DEFAULT 'Staff',
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 4. TRAINERS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS trainers (
    trainer_id VARCHAR(10) PRIMARY KEY,
    profile_id VARCHAR(10) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    hire_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_specialization (specialization),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 5. MEMBERSHIPS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS memberships (
    membership_id VARCHAR(10) PRIMARY KEY,
    profile_id VARCHAR(10) NOT NULL,
    membership_type ENUM('Basic', 'Premium', 'Family') NOT NULL,
    fee DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status ENUM('Active', 'Expired', 'Cancelled') DEFAULT 'Active',
    benefits TEXT,
    number_of_members INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_profile_id (profile_id),
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 6. GYM CLASSES TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS gym_classes (
    class_id VARCHAR(10) PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL,
    class_type ENUM('Yoga', 'Spin', 'Strength') NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 15,
    trainer_id VARCHAR(10) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    yoga_style VARCHAR(50),
    difficulty VARCHAR(20),
    intensity VARCHAR(20),
    duration_minutes INT,
    focus_area VARCHAR(50),
    equipment_needed TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT,
    INDEX idx_class_type (class_type),
    INDEX idx_trainer_id (trainer_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 7. SESSIONS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS sessions (
    session_id VARCHAR(10) PRIMARY KEY,
    class_id VARCHAR(10) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration VARCHAR(20),
    trainer_id VARCHAR(10) NOT NULL,
    max_attendees INT DEFAULT 30,
    current_attendees INT DEFAULT 0,
    status ENUM('Scheduled', 'In Progress', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES gym_classes(class_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT,
    INDEX idx_session_date (session_date),
    INDEX idx_class_id (class_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 8. BOOKINGS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS bookings (
    booking_id VARCHAR(10) PRIMARY KEY,
    profile_id VARCHAR(10) NOT NULL,
    session_id VARCHAR(10) NOT NULL,
    booking_date DATE NOT NULL,
    status ENUM('Confirmed', 'Pending', 'Cancelled', 'Completed', 'Waitlisted') DEFAULT 'Pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_booking (profile_id, session_id),
    INDEX idx_profile_id (profile_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 9. ATTENDANCE TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id VARCHAR(10) PRIMARY KEY,
    profile_id VARCHAR(10) NOT NULL,
    session_id VARCHAR(10) NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('Present', 'Absent', 'Late', 'Excused') DEFAULT 'Present',
    check_in_time TIME,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions(session_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance (profile_id, session_id),
    INDEX idx_profile_id (profile_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 10. PAYMENTS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(10) PRIMARY KEY,
    profile_id VARCHAR(10) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status ENUM('Pending', 'Completed', 'Failed', 'Refunded') DEFAULT 'Pending',
    transaction_id VARCHAR(100) UNIQUE,
    receipt_path VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_profile_id (profile_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 11. STORED PROCEDURE: Generate Custom ID
-- =============================================================================
DELIMITER //

CREATE PROCEDURE generate_custom_id(
    IN p_id_type VARCHAR(20),
    IN p_role_code VARCHAR(2),
    IN p_registration_date DATE,
    OUT p_new_id VARCHAR(10)
)
BEGIN
    DECLARE v_sequence INT;
    DECLARE v_date_part VARCHAR(6);
    
    -- Get next sequence number
    UPDATE id_counter 
    SET last_sequence = last_sequence + 1 
    WHERE id_type = p_id_type;
    
    -- If sequence exceeds 9999, reset to 1
    UPDATE id_counter 
    SET last_sequence = 1 
    WHERE id_type = p_id_type AND last_sequence > 9999;
    
    SELECT last_sequence INTO v_sequence 
    FROM id_counter 
    WHERE id_type = p_id_type;
    
    -- Format date as YYMMDD
    SET v_date_part = DATE_FORMAT(p_registration_date, '%y%m%d');
    
    -- Generate ID: COUNTER(4) + YYMMDD(6) + ROLE_CODE(2)
    SET p_new_id = CONCAT(
        LPAD(v_sequence, 4, '0'),
        v_date_part,
        p_role_code
    );
END //

DELIMITER ;

-- =============================================================================
-- 12. SAMPLE DATA
-- =============================================================================

-- Insert sample profiles with proper IDs
INSERT INTO profiles (profile_id, name, email, phone, address, registration_date) VALUES
('000126072400', 'System Admin', 'admin@gym.com', '555-000-0000', 'Admin Office', '2026-07-24'),
('000226072411', 'Sarah Johnson', 'sarah@gym.com', '555-111-2222', '789 Yoga Lane', '2026-07-24'),
('000326072422', 'John Doe', 'john@email.com', '555-123-4567', '123 Main St', '2026-07-24');

-- Insert admin
INSERT INTO admins (admin_id, profile_id, admin_level, user_id, password_hash) VALUES
('000126072400', '000126072400', 'Super Admin', 'A001', 'admin123_hash');

-- Insert trainer
INSERT INTO trainers (trainer_id, profile_id, specialization, user_id, password_hash, hire_date) VALUES
('000226072411', '000226072411', 'Yoga', 'T001', 'trainer123_hash', '2026-07-24');

-- Insert membership
INSERT INTO memberships (membership_id, profile_id, membership_type, fee, start_date, expiry_date, status) VALUES
('000126072433', '000326072422', 'Basic', 49.99, '2026-07-24', '2027-07-24', 'Active');

-- =============================================================================
-- END OF SCHEMA
-- =============================================================================