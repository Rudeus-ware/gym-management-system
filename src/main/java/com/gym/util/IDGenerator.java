package com.gym.util;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * IdGenerator - Generates custom IDs for all entities
 * Format: [4-digit counter][YYMMDD][2-digit role code]
 * 
 * Role Codes:
 * - Admin: 00
 * - Trainer: 11
 * - Member: 22
 * - Membership: 33
 * - Class: 44
 * - Session: 55
 * - Booking: 66
 * - Attendance: 77
 * - Payment: 88
 */
public class IdGenerator {
    
    private Connection connection;
    private String prefix; // Optional prefix for fallback
    
    // Entity types for counter
    public static final String COUNTER_PROFILE = "profile";
    public static final String COUNTER_MEMBERSHIP = "membership";
    public static final String COUNTER_CLASS = "class";
    public static final String COUNTER_SESSION = "session";
    public static final String COUNTER_BOOKING = "booking";
    public static final String COUNTER_ATTENDANCE = "attendance";
    public static final String COUNTER_PAYMENT = "payment";
    
    // Role codes
    public static final String ROLE_ADMIN = "00";
    public static final String ROLE_TRAINER = "11";
    public static final String ROLE_MEMBER = "22";
    
    public IdGenerator(Connection connection) {
        this.connection = connection;
        this.prefix = null;
        initializeCounterTable();
    }
    
    /**
     * Constructor with prefix for fallback
     */
    public IdGenerator(String prefix) {
        this.connection = null;
        this.prefix = prefix;
    }
    
    /**
     * Initialize the counter table if it doesn't exist
     */
    private void initializeCounterTable() {
        if (connection == null) return;
        try {
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS id_counter (
                    id_type VARCHAR(20) PRIMARY KEY,
                    last_sequence INT DEFAULT 0,
                    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """;
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSql);
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Could not initialize counter table: " + e.getMessage());
        }
    }
    
    // ============================================================
    // GENERATE SPECIFIC IDS
    // ============================================================
    
    /**
     * Generate a profile ID
     */
    public String generateProfileId(String roleCode, LocalDate registrationDate) {
        String sequence = getNextSequence(COUNTER_PROFILE);
        String datePart = formatDate(registrationDate);
        return sequence + datePart + roleCode;
    }
    
    /**
     * Generate a membership ID
     */
    public String generateMembershipId(LocalDate date) {
        String sequence = getNextSequence(COUNTER_MEMBERSHIP);
        String datePart = formatDate(date);
        return sequence + datePart + "33";
    }
    
    /**
     * Generate a class ID
     */
    public String generateClassId(LocalDate date) {
        String sequence = getNextSequence(COUNTER_CLASS);
        String datePart = formatDate(date);
        return sequence + datePart + "44";
    }
    
    /**
     * Generate a session ID
     */
    public String generateSessionId(LocalDate date) {
        String sequence = getNextSequence(COUNTER_SESSION);
        String datePart = formatDate(date);
        return sequence + datePart + "55";
    }
    
    /**
     * Generate a booking ID
     */
    public String generateBookingId(LocalDate date) {
        String sequence = getNextSequence(COUNTER_BOOKING);
        String datePart = formatDate(date);
        return sequence + datePart + "66";
    }
    
    /**
     * Generate an attendance ID
     */
    public String generateAttendanceId(LocalDate date) {
        String sequence = getNextSequence(COUNTER_ATTENDANCE);
        String datePart = formatDate(date);
        return sequence + datePart + "77";
    }
    
    /**
     * Generate a payment ID
     */
    public String generatePaymentId(LocalDate date) {
        String sequence = getNextSequence(COUNTER_PAYMENT);
        String datePart = formatDate(date);
        return sequence + datePart + "88";
    }
    
    // ============================================================
    // GENERIC ID GENERATOR (For AttendanceController compatibility)
    // ============================================================
    
    /**
     * Generate a generic ID with prefix, suffix, and date
     * This is the method that AttendanceController calls
     */
    public String generateId(String type, String suffix, LocalDate date) {
        // Map type to counter
        String counterType;
        switch (type.toLowerCase()) {
            case "attendance":
                counterType = COUNTER_ATTENDANCE;
                break;
            case "profile":
                counterType = COUNTER_PROFILE;
                break;
            case "membership":
                counterType = COUNTER_MEMBERSHIP;
                break;
            case "class":
                counterType = COUNTER_CLASS;
                break;
            case "session":
                counterType = COUNTER_SESSION;
                break;
            case "booking":
                counterType = COUNTER_BOOKING;
                break;
            case "payment":
                counterType = COUNTER_PAYMENT;
                break;
            default:
                // Fallback: use random
                return generateFallbackId(type, date);
        }
        
        String sequence = getNextSequence(counterType);
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Format: PREFIX-DATE-SEQUENCE
        // Example: ATT-20240615-0001
        return String.format("%s-%s-%04d", 
            type.substring(0, 3).toUpperCase(), 
            dateStr, 
            Integer.parseInt(sequence)
        );
    }
    
    /**
     * Generate a fallback ID when database is not available
     */
    private String generateFallbackId(String type, LocalDate date) {
        if (prefix != null) {
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            int count = getNextCount();
            return String.format("%s-%s-%04d", prefix, dateStr, count);
        }
        
        // Ultimate fallback: timestamp-based
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long random = System.currentTimeMillis() % 10000;
        return String.format("%s-%s-%04d", 
            type.substring(0, 3).toUpperCase(), 
            timestamp, 
            random
        );
    }
    
    /**
     * Get next count for fallback (in-memory counter)
     */
    private int getNextCount() {
        // Simple in-memory counter - resets per instance
        return (int)(System.currentTimeMillis() % 10000) + 1;
    }
    
    // ============================================================
    // SEQUENCE MANAGEMENT (MySQL)
    // ============================================================
    
    /**
     * Get next sequence number for a counter type
     */
    private synchronized String getNextSequence(String counterType) {
        if (connection == null) {
            // Fallback for when connection is not available
            return String.format("%04d", (int)(System.currentTimeMillis() % 10000));
        }
        
        try {
            // Use transaction to ensure atomicity
            connection.setAutoCommit(false);
            
            // Try to get and update in one operation using MySQL's INSERT ... ON DUPLICATE KEY
            String upsertSql = """
                INSERT INTO id_counter (id_type, last_sequence) 
                VALUES (?, 1) 
                ON DUPLICATE KEY UPDATE 
                    last_sequence = last_sequence + 1
            """;
            
            try (PreparedStatement stmt = connection.prepareStatement(upsertSql)) {
                stmt.setString(1, counterType);
                stmt.executeUpdate();
            }
            
            // Get the updated value
            String selectSql = "SELECT last_sequence FROM id_counter WHERE id_type = ?";
            int sequence = 0;
            try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
                stmt.setString(1, counterType);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    sequence = rs.getInt("last_sequence");
                }
            }
            
            connection.commit();
            
            // Wrap around if exceeds 9999
            if (sequence > 9999) {
                sequence = 1;
                // Reset the counter
                String resetSql = "UPDATE id_counter SET last_sequence = 1 WHERE id_type = ?";
                try (PreparedStatement stmt = connection.prepareStatement(resetSql)) {
                    stmt.setString(1, counterType);
                    stmt.executeUpdate();
                }
                connection.commit();
            }
            
            return String.format("%04d", sequence);
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting sequence for " + counterType + ": " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("❌ Rollback failed: " + ex.getMessage());
            }
            // Fallback: use timestamp-based ID
            return String.format("%04d", System.currentTimeMillis() % 10000);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("❌ Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Format date as YYMMDD
     */
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyMMdd"));
    }
    
    /**
     * Extract role from ID
     */
    public String getRoleFromId(String id) {
        if (id == null || id.length() < 10) {
            return "UNKNOWN";
        }
        String roleCode = id.substring(8, 10);
        switch (roleCode) {
            case "00": return "ADMIN";
            case "11": return "TRAINER";
            case "22": return "MEMBER";
            case "33": return "MEMBERSHIP";
            case "44": return "CLASS";
            case "55": return "SESSION";
            case "66": return "BOOKING";
            case "77": return "ATTENDANCE";
            case "88": return "PAYMENT";
            default: return "UNKNOWN";
        }
    }
    
    /**
     * Extract date from ID
     */
    public String getDateFromId(String id) {
        if (id == null || id.length() < 10) {
            return "UNKNOWN";
        }
        String yymmdd = id.substring(4, 10);
        return "20" + yymmdd.substring(0, 2) + "-" + 
               yymmdd.substring(2, 4) + "-" + 
               yymmdd.substring(4, 6);
    }
    
    /**
     * Validate if an ID matches the expected format
     */
    public boolean isValidFormat(String id) {
        if (id == null || id.length() != 10) {
            return false;
        }
        // Check first 8 characters are digits
        for (int i = 0; i < 8; i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return false;
            }
        }
        // Check last 2 characters are valid role codes
        String role = id.substring(8, 10);
        return role.equals("00") || role.equals("11") || role.equals("22") ||
               role.equals("33") || role.equals("44") || role.equals("55") ||
               role.equals("66") || role.equals("77") || role.equals("88");
    }
    
    /**
     * Reset a specific counter (for testing/administration)
     */
    public void resetCounter(String counterType) {
        if (connection == null) return;
        String sql = "UPDATE id_counter SET last_sequence = 0 WHERE id_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, counterType);
            stmt.executeUpdate();
            System.out.println("✅ Counter reset for: " + counterType);
        } catch (SQLException e) {
            System.err.println("❌ Failed to reset counter: " + e.getMessage());
        }
    }
    
    /**
     * Get current sequence value (for monitoring)
     */
    public int getCurrentSequence(String counterType) {
        if (connection == null) return 0;
        String sql = "SELECT last_sequence FROM id_counter WHERE id_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, counterType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("last_sequence");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to get sequence: " + e.getMessage());
        }
        return 0;
    }
}