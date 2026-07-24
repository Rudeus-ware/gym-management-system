-- =============================================================================
-- GYM MANAGEMENT SYSTEM - COMPLETE DATABASE SCHEMA
-- =============================================================================

-- Drop tables in reverse order to avoid foreign key conflicts
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
-- 1. PROFILES TABLE (Base for all users)
-- =============================================================================
CREATE TABLE profiles (
    profile_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 2. ADMINS TABLE
-- =============================================================================
CREATE TABLE admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    admin_level VARCHAR(50) DEFAULT 'Staff',
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 3. TRAINERS TABLE
-- =============================================================================
CREATE TABLE trainers (
    trainer_id INT PRIMARY KEY AUTO_INCREMENT,
    profile_id INT NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    hire_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 4. MEMBERSHIPS TABLE
-- =============================================================================
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
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 5. GYM CLASSES TABLE
-- =============================================================================
CREATE TABLE gym_classes (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 6. SESSIONS TABLE
-- =============================================================================
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
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 7. BOOKINGS TABLE
-- =============================================================================
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
    UNIQUE KEY unique_booking (profile_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 8. ATTENDANCE TABLE
-- =============================================================================
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
    UNIQUE KEY unique_attendance (profile_id, session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 9. PAYMENTS TABLE
-- =============================================================================
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
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 10. INSERT SAMPLE DATA
-- =============================================================================

-- Sample Profiles
INSERT INTO profiles (name, email, phone, address, registration_date) VALUES
('John Doe', 'john@email.com', '555-123-4567', '123 Main St, City', CURDATE()),
('Sarah Smith', 'sarah@email.com', '555-987-6543', '456 Oak Ave, Town', CURDATE()),
('Admin User', 'admin@gym.com', '555-000-0000', 'Admin Office, Gym HQ', CURDATE());

-- Sample Admin
INSERT INTO admins (profile_id, admin_level, user_id, password_hash) VALUES
(3, 'Super Admin', 'A001', 'admin123_hash');

-- Sample Trainer
INSERT INTO trainers (profile_id, specialization, user_id, password_hash, hire_date) VALUES
(1, 'Yoga', 'T001', 'trainer123_hash', CURDATE());

-- Sample Memberships
INSERT INTO memberships (profile_id, membership_type, fee, start_date, expiry_date, status) VALUES
(1, 'Basic', 49.99, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active'),
(2, 'Premium', 99.99, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active');

-- Sample Gym Class
INSERT INTO gym_classes (class_name, class_type, schedule, capacity, trainer_id, yoga_style, difficulty) VALUES
('Morning Yoga', 'Yoga', 'Mon/Wed/Fri 7:00 AM', 15, 1, 'Hatha', 'Beginner');

-- Sample Sessions
INSERT INTO sessions (class_id, session_date, start_time, end_time, duration, trainer_id, max_attendees) VALUES
(1, CURDATE(), '07:00:00', '08:00:00', '1 hour', 1, 15),
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '07:00:00', '08:00:00', '1 hour', 1, 15);

-- =============================================================================
-- 11. USEFUL VIEWS
-- =============================================================================

-- Active Members View
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

-- Upcoming Sessions View
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

-- =============================================================================
-- END OF SCHEMA
-- =============================================================================