-- =============================================================================
-- GYM MANAGEMENT SYSTEM - DATABASE SCHEMA
-- ID Format: [3-char counter][YYMMDD][2-char role] = 11 characters total
-- =============================================================================

CREATE DATABASE IF NOT EXISTS gym_db;
USE gym_db;

SET FOREIGN_KEY_CHECKS = 0;

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
-- ID COUNTER TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS id_counter (
    id_type VARCHAR(20) PRIMARY KEY,
    last_sequence INT DEFAULT -1
);

INSERT IGNORE INTO id_counter (id_type, last_sequence) VALUES
('profile', -1),
('membership', -1),
('class', -1),
('session', -1),
('booking', -1),
('attendance', -1),
('payment', -1);

-- =============================================================================
-- PROFILES TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS profiles (
    profile_id VARCHAR(11) PRIMARY KEY,
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
-- ADMINS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS admins (
    admin_id VARCHAR(11) PRIMARY KEY,
    profile_id VARCHAR(11) NOT NULL,
    admin_level VARCHAR(50) DEFAULT 'Staff',
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- =============================================================================
-- TRAINERS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS trainers (
    trainer_id VARCHAR(11) PRIMARY KEY,
    profile_id VARCHAR(11) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    hire_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
);

-- =============================================================================
-- MEMBERSHIPS TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS memberships (
    membership_id VARCHAR(11) PRIMARY KEY,
    profile_id VARCHAR(11) NOT NULL,
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
-- GYM CLASSES TABLE
-- =============================================================================
CREATE TABLE IF NOT EXISTS gym_classes (
    class_id VARCHAR(11) PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL,
    class_type ENUM('Yoga', 'Spin', 'Strength') NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 15,
    trainer_id VARCHAR(11) NOT NULL,
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
    session_id VARCHAR(11) PRIMARY KEY,
    class_id VARCHAR(11) NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration VARCHAR(20),
    trainer_id VARCHAR(11) NOT NULL,
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
    booking_id VARCHAR(11) PRIMARY KEY,
    profile_id VARCHAR(11) NOT NULL,
    session_id VARCHAR(11) NOT NULL,
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
    attendance_id VARCHAR(11) PRIMARY KEY,
    profile_id VARCHAR(11) NOT NULL,
    session_id VARCHAR(11) NOT NULL,
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
    payment_id VARCHAR(11) PRIMARY KEY,
    profile_id VARCHAR(11) NOT NULL,
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
-- SAMPLE DATA
-- =============================================================================
INSERT INTO profiles (profile_id, name, email, phone, address, registration_date) VALUES
('00026072400', 'System Admin', 'admin@gym.com', '555-000-0000', 'Admin Office', '2026-07-24');

INSERT INTO admins (admin_id, profile_id, admin_level, user_id, password_hash) VALUES
('00026072400', '00026072400', 'Super Admin', 'A001', 'admin123');

-- =============================================================================
-- END OF SCHEMA
-- =============================================================================