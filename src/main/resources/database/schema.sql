-- =============================================================================
-- GYM MANAGEMENT SYSTEM - COMPLETE DATABASE SCHEMA with Custom IDs
-- =============================================================================

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
    profile_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
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
    admin_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    profile_id VARCHAR(20) NOT NULL,   -- ✅ References profiles
    admin_level VARCHAR(50) DEFAULT 'Staff',
    password_hash VARCHAR(255) NOT NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES profiles(profile_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 3. TRAINERS TABLE
-- =============================================================================
CREATE TABLE trainers (
    trainer_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    profile_id VARCHAR(20) NOT NULL,     -- ✅ References profiles
    specialization VARCHAR(100) NOT NULL,
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
    membership_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    profile_id VARCHAR(20) NOT NULL,        -- ✅ References profiles
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
    class_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    class_name VARCHAR(100) NOT NULL,
    class_type ENUM('Yoga', 'Spin', 'Strength') NOT NULL,
    schedule VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 15,
    trainer_id VARCHAR(20) NOT NULL,   -- ✅ References trainers
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
    session_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    class_id VARCHAR(20) NOT NULL,       -- ✅ References gym_classes
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration VARCHAR(20),
    trainer_id VARCHAR(20) NOT NULL,     -- ✅ References trainers
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
    booking_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    profile_id VARCHAR(20) NOT NULL,     -- ✅ References profiles
    session_id VARCHAR(20) NOT NULL,     -- ✅ References sessions
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
    attendance_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    profile_id VARCHAR(20) NOT NULL,        -- ✅ References profiles
    session_id VARCHAR(20) NOT NULL,        -- ✅ References sessions
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
    payment_id VARCHAR(20) PRIMARY KEY,  -- ✅ Custom ID format
    profile_id VARCHAR(20) NOT NULL,     -- ✅ References profiles
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
-- 10. COUNTER TABLE FOR ID GENERATION
-- =============================================================================
CREATE TABLE id_counter (
    id_type VARCHAR(20) PRIMARY KEY,
    last_sequence INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Initialize counters
INSERT INTO id_counter (id_type, last_sequence) VALUES
('profile', 0),
('membership', 0),
('class', 0),
('session', 0),
('booking', 0),
('attendance', 0),
('payment', 0);

-- =============================================================================
-- 11. STORED PROCEDURE: Generate Custom ID
-- =============================================================================
DELIMITER //

CREATE PROCEDURE generate_custom_id(
    IN p_id_type VARCHAR(20),
    IN p_role_code VARCHAR(2),
    IN p_registration_date DATE,
    OUT p_new_id VARCHAR(20)
)
BEGIN
    DECLARE v_sequence INT;
    DECLARE v_mmdd VARCHAR(4);
    
    -- Get next sequence number
    UPDATE id_counter 
    SET last_sequence = last_sequence + 1 
    WHERE id_type = p_id_type;
    
    SELECT last_sequence INTO v_sequence 
    FROM id_counter 
    WHERE id_type = p_id_type;
    
    -- Format MMDD from registration date
    SET v_mmdd = DATE_FORMAT(p_registration_date, '%m%d');
    
    -- Generate ID: COUNTER(3) + MMDD(4) + ROLE_CODE(2)
    SET p_new_id = CONCAT(
        LPAD(v_sequence, 3, '0'),
        v_mmdd,
        p_role_code
    );
END //

DELIMITER ;

-- =============================================================================
-- 12. INSERT SAMPLE DATA
-- =============================================================================

-- =============================================================================
-- 13. USEFUL VIEWS
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
    t.trainer_id AS trainer,
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