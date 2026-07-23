-- =============================================================================
-- GYM MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Version: 1.0
-- Database: gym_db
-- =============================================================================

-- =============================================================================
-- 1. CREATE DATABASE
-- =============================================================================

CREATE DATABASE IF NOT EXISTS gym_db;
USE gym_db;

-- =============================================================================
-- 2. DROP EXISTING TABLES (Clean start - be careful!)
-- =============================================================================

-- Drop tables in reverse order to avoid foreign key conflicts
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS gym_classes;
DROP TABLE IF EXISTS memberships;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS trainers;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS profiles;

-- =============================================================================
-- 3. CREATE TABLES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 3.1 PROFILES TABLE (Base for all users)
-- -----------------------------------------------------------------------------
CREATE TABLE profiles (
    profile_id INT PRIMARY KEY AUTO_INCREMENT,
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

-- -----------------------------------------------------------------------------
-- 3.2 ADMINS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    admin_level VARCHAR(50) DEFAULT 'Staff',
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3.3 TRAINERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE trainers (
    trainer_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    hire_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_specialization (specialization)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3.4 MEMBERSHIPS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE memberships (
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
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_profile_id (profile_id),
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3.5 GYM CLASSES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE gym_classes (
    class_id INT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(100) NOT NULL,
    class_type ENUM('Yoga', 'Spin', 'Strength') NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 15,
    trainer_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    -- Yoga-specific fields
    yoga_style VARCHAR(50),
    difficulty VARCHAR(20),
    -- Spin-specific fields
    intensity VARCHAR(20),
    duration_minutes INT,
    music_type VARCHAR(50),
    -- Strength-specific fields
    focus_area VARCHAR(50),
    equipment_needed TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT,
    INDEX idx_class_type (class_type),
    INDEX idx_trainer_id (trainer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3.6 SESSIONS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE sessions (
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
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT,
    INDEX idx_session_date (session_date),
    INDEX idx_class_id (class_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3.7 BOOKINGS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE bookings (
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
    UNIQUE KEY unique_booking (profile_id, session_id),
    INDEX idx_profile_id (profile_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3.8 ATTENDANCE TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE attendance (
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
    UNIQUE KEY unique_attendance (profile_id, session_id),
    INDEX idx_profile_id (profile_id),
    INDEX idx_session_id (session_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------------------------
-- 3.9 PAYMENTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE payments (
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
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE,
    INDEX idx_profile_id (profile_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 4. INSERT SAMPLE DATA
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 4.1 Insert Sample Profiles
-- -----------------------------------------------------------------------------
INSERT INTO profiles (name, email, phone, address, registration_date) VALUES
('John Doe', 'john@email.com', '555-123-4567', '123 Main St, City', CURDATE()),
('Sarah Smith', 'sarah@email.com', '555-987-6543', '456 Oak Ave, Town', CURDATE()),
('Admin User', 'admin@gym.com', '555-000-0000', 'Admin Office, Gym HQ', CURDATE());

-- -----------------------------------------------------------------------------
-- 4.2 Insert Sample Admin
-- -----------------------------------------------------------------------------
INSERT INTO admins (profile_id, admin_level, user_id, password_hash) VALUES
(3, 'Super Admin', 'A001', 'admin123_hash'); -- In production, use bcrypt!

-- -----------------------------------------------------------------------------
-- 4.3 Insert Sample Trainers
-- -----------------------------------------------------------------------------
INSERT INTO trainers (profile_id, specialization, user_id, password_hash, hire_date) VALUES
(1, 'Yoga', 'T001', 'trainer123_hash', CURDATE());

-- -----------------------------------------------------------------------------
-- 4.4 Insert Sample Memberships
-- -----------------------------------------------------------------------------
INSERT INTO memberships (profile_id, membership_type, fee, start_date, expiry_date, status) VALUES
(1, 'Basic', 49.99, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active'),
(2, 'Premium', 99.99, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active');

-- -----------------------------------------------------------------------------
-- 4.5 Insert Sample Gym Classes
-- -----------------------------------------------------------------------------
INSERT INTO gym_classes (class_name, class_type, schedule, capacity, trainer_id, 
                         yoga_style, difficulty) VALUES
('Morning Yoga', 'Yoga', 'Mon/Wed/Fri 7:00 AM', 15, 1, 'Hatha', 'Beginner');

-- -----------------------------------------------------------------------------
-- 4.6 Insert Sample Sessions
-- -----------------------------------------------------------------------------
INSERT INTO sessions (class_id, session_date, start_time, end_time, duration, trainer_id, max_attendees) VALUES
(1, CURDATE(), '07:00:00', '08:00:00', '1 hour', 1, 15),
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '07:00:00', '08:00:00', '1 hour', 1, 15);

-- =============================================================================
-- 5. CREATE USEFUL VIEWS (Optional)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 5.1 View: Active Members with Membership Details
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW view_active_members AS
SELECT 
    p.profile_id,
    p.name,
    p.email,
    p.phone,
    m.membership_type,
    m.fee,
    m.start_date,
    m.expiry_date,
    DATEDIFF(m.expiry_date, CURDATE()) AS days_remaining
FROM profiles p
JOIN memberships m ON p.profile_id = m.profile_id
WHERE m.status = 'Active' 
  AND m.expiry_date >= CURDATE()
  AND p.is_active = TRUE;

-- -----------------------------------------------------------------------------
-- 5.2 View: Upcoming Sessions
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW view_upcoming_sessions AS
SELECT 
    s.session_id,
    gc.class_name,
    gc.class_type,
    s.session_date,
    s.start_time,
    s.end_time,
    t.user_id AS trainer,
    s.current_attendees,
    s.max_attendees,
    (s.max_attendees - s.current_attendees) AS available_spots
FROM sessions s
JOIN gym_classes gc ON s.class_id = gc.class_id
JOIN trainers t ON s.trainer_id = t.trainer_id
WHERE s.session_date >= CURDATE() 
  AND s.status = 'Scheduled'
ORDER BY s.session_date, s.start_time;

-- -----------------------------------------------------------------------------
-- 5.3 View: Booking Summary
-- -----------------------------------------------------------------------------
CREATE OR REPLACE VIEW view_booking_summary AS
SELECT 
    b.booking_id,
    p.name AS member_name,
    gc.class_name,
    s.session_date,
    s.start_time,
    b.status,
    b.booking_date
FROM bookings b
JOIN profiles p ON b.profile_id = p.profile_id
JOIN sessions s ON b.session_id = s.session_id
JOIN gym_classes gc ON s.class_id = gc.class_id
ORDER BY b.booking_date DESC;

-- =============================================================================
-- 6. CREATE STORED PROCEDURES (Optional)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 6.1 Procedure: Book a Member into a Session
-- -----------------------------------------------------------------------------
DELIMITER //

CREATE PROCEDURE sp_book_session(
    IN p_profile_id INT,
    IN p_session_id INT,
    OUT p_result VARCHAR(100)
)
BEGIN
    DECLARE v_current_attendees INT;
    DECLARE v_max_attendees INT;
    DECLARE v_has_valid_membership BOOLEAN;
    
    -- Check if member has valid membership
    SELECT COUNT(*) INTO v_has_valid_membership
    FROM memberships 
    WHERE profile_id = p_profile_id 
      AND status = 'Active' 
      AND expiry_date >= CURDATE();
    
    IF v_has_valid_membership = 0 THEN
        SET p_result = 'ERROR: No valid membership';
        LEAVE sp_book_session;
    END IF;
    
    -- Get current attendees and max capacity
    SELECT current_attendees, max_attendees 
    INTO v_current_attendees, v_max_attendees
    FROM sessions 
    WHERE session_id = p_session_id;
    
    IF v_current_attendees >= v_max_attendees THEN
        SET p_result = 'ERROR: Session is full';
        LEAVE sp_book_session;
    END IF;
    
    -- Check if already booked
    IF EXISTS (SELECT 1 FROM bookings WHERE profile_id = p_profile_id AND session_id = p_session_id) THEN
        SET p_result = 'ERROR: Already booked';
        LEAVE sp_book_session;
    END IF;
    
    -- Create booking
    INSERT INTO bookings (profile_id, session_id, booking_date, status)
    VALUES (p_profile_id, p_session_id, CURDATE(), 'Confirmed');
    
    -- Update attendee count
    UPDATE sessions 
    SET current_attendees = current_attendees + 1 
    WHERE session_id = p_session_id;
    
    SET p_result = 'SUCCESS: Booked successfully';
END //

DELIMITER ;

-- =============================================================================
-- 7. GRANT PERMISSIONS (Optional - Adjust as needed)
-- =============================================================================

-- Grant privileges to application user
-- CREATE USER 'gym_app'@'localhost' IDENTIFIED BY 'your_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON gym_db.* TO 'gym_app'@'localhost';
-- FLUSH PRIVILEGES;

-- =============================================================================
-- 8. VERIFICATION QUERIES
-- =============================================================================

-- Check all tables
SHOW TABLES;

-- Count records in each table
SELECT 'profiles' AS table_name, COUNT(*) AS record_count FROM profiles
UNION ALL
SELECT 'admins', COUNT(*) FROM admins
UNION ALL
SELECT 'trainers', COUNT(*) FROM trainers
UNION ALL
SELECT 'memberships', COUNT(*) FROM memberships
UNION ALL
SELECT 'gym_classes', COUNT(*) FROM gym_classes
UNION ALL
SELECT 'sessions', COUNT(*) FROM sessions
UNION ALL
SELECT 'bookings', COUNT(*) FROM bookings
UNION ALL
SELECT 'attendance', COUNT(*) FROM attendance
UNION ALL
SELECT 'payments', COUNT(*) FROM payments;

-- =============================================================================
-- END OF SCRIPT
-- =============================================================================