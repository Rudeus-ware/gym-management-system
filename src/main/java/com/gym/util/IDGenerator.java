package com.gym.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * IdGenerator - Generates custom IDs with format: [Counter][YYMMDD][Role]
 * 
 * Counter format:
 * - 000 to 999 (numeric)
 * - A00 to A99 (1000-1099)
 * - B00 to B99 (1100-1199)
 * - ...
 * - Z00 to Z99 (2500-2599)
 * 
 * Total possible: 1000 + 26*100 = 3600 per role
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
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    // Role Codes
    public static final String ROLE_ADMIN = "00";
    public static final String ROLE_TRAINER = "11";
    public static final String ROLE_MEMBER = "22";
    public static final String ROLE_MEMBERSHIP = "33";
    public static final String ROLE_CLASS = "44";
    public static final String ROLE_SESSION = "55";
    public static final String ROLE_BOOKING = "66";
    public static final String ROLE_ATTENDANCE = "77";
    public static final String ROLE_PAYMENT = "88";
    
    // Entity types for counters
    public static final String COUNTER_PROFILE = "profile";
    public static final String COUNTER_MEMBERSHIP = "membership";
    public static final String COUNTER_CLASS = "class";
    public static final String COUNTER_SESSION = "session";
    public static final String COUNTER_BOOKING = "booking";
    public static final String COUNTER_ATTENDANCE = "attendance";
    public static final String COUNTER_PAYMENT = "payment";
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private Connection connection;
    private boolean useDatabase = true;
    
    // In-memory counters for JSON fallback
    private int profileCounter = -1;
    private int membershipCounter = -1;
    private int classCounter = -1;
    private int sessionCounter = -1;
    private int bookingCounter = -1;
    private int attendanceCounter = -1;
    private int paymentCounter = -1;
    
    // Optional prefix for fallback
    private String prefix;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    public IdGenerator(Connection connection) {
        this.connection = connection;
        this.prefix = null;
        initializeCounterTable();
    }
    
    public IdGenerator() {
        this.connection = null;
        this.prefix = null;
    }
    
    public IdGenerator(String prefix) {
        this.connection = null;
        this.prefix = prefix;
    }
    
    // ============================================================
    // INITIALIZATION
    // ============================================================
    
    private void initializeCounterTable() {
        if (connection == null) return;
        try {
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS id_counter (
                    id_type VARCHAR(20) PRIMARY KEY,
                    last_sequence INT DEFAULT -1,
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
    
    public void setUseDatabase(boolean useDatabase) {
        this.useDatabase = useDatabase;
    }
    
    // ============================================================
    // PUBLIC GENERATE METHODS
    // ============================================================
    
    public String generateProfileId(String roleCode, LocalDate registrationDate) {
        String counter = getNextSequence(COUNTER_PROFILE);
        String datePart = formatDate(registrationDate);
        return counter + datePart + roleCode;
    }

    
    
    public String generateMembershipId(LocalDate date) {
        String counter = getNextSequence(COUNTER_MEMBERSHIP);
        String datePart = formatDate(date);
        return counter + datePart + ROLE_MEMBERSHIP;
    }
    
    public String generateClassId(LocalDate date) {
        String counter = getNextSequence(COUNTER_CLASS);
        String datePart = formatDate(date);
        return counter + datePart + ROLE_CLASS;
    }
    
    public String generateSessionId(LocalDate date) {
        String counter = getNextSequence(COUNTER_SESSION);
        String datePart = formatDate(date);
        return counter + datePart + ROLE_SESSION;
    }
    
    public String generateBookingId(LocalDate date) {
        String counter = getNextSequence(COUNTER_BOOKING);
        String datePart = formatDate(date);
        return counter + datePart + ROLE_BOOKING;
    }
    
    public String generateAttendanceId(LocalDate date) {
        String counter = getNextSequence(COUNTER_ATTENDANCE);
        String datePart = formatDate(date);
        return counter + datePart + ROLE_ATTENDANCE;
    }
    
    public String generatePaymentId(LocalDate date) {
        String counter = getNextSequence(COUNTER_PAYMENT);
        String datePart = formatDate(date);
        return counter + datePart + ROLE_PAYMENT;
    }
    
    // ============================================================
    // GENERIC ID GENERATOR (For Compatibility)
    // ============================================================
    
    public String generateId(String type, String suffix, LocalDate date) {
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
                return generateFallbackId(type, date);
        }
        
        String counter = getNextSequence(counterType);
        String datePart = formatDate(date);
        return counter + datePart + suffix;
    }
    
    private String generateFallbackId(String type, LocalDate date) {
        if (prefix != null) {
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyMMdd"));
            int count = getNextCount();
            return String.format("%s%s%03d", prefix, dateStr, count);
        }
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyMMdd"));
        long random = System.currentTimeMillis() % 1000;
        return String.format("%s%s%03d", type.substring(0, 3).toUpperCase(), dateStr, random);
    }
    
    private int getNextCount() {
        return (int)(System.currentTimeMillis() % 1000) + 1;
    }
    
    // ============================================================
    // SEQUENCE GENERATION
    // ============================================================
    
    private synchronized String getNextSequence(String counterType) {
        if (useDatabase && connection != null) {
            try {
                return getSequenceFromDatabase(counterType);
            } catch (SQLException e) {
                System.out.println("⚠️ Database sequence failed, using in-memory counter");
                return getSequenceFromMemory(counterType);
            }
        }
        return getSequenceFromMemory(counterType);
    }
    
    private String getSequenceFromDatabase(String counterType) throws SQLException {
        // Get current sequence
        String selectSql = "SELECT last_sequence FROM id_counter WHERE id_type = ?";
        int currentSequence = -1;
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            stmt.setString(1, counterType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentSequence = rs.getInt("last_sequence");
            }
        }
        
        // If no record exists, insert with 0
        if (currentSequence == -1) {
            String insertSql = "INSERT INTO id_counter (id_type, last_sequence) VALUES (?, 0)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
                stmt.setString(1, counterType);
                stmt.executeUpdate();
            }
            currentSequence = 0;
        }
        
        // Increment
        int newSequence = currentSequence + 1;
        
        // Update sequence
        String updateSql = "UPDATE id_counter SET last_sequence = ? WHERE id_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
            stmt.setInt(1, newSequence);
            stmt.setString(2, counterType);
            stmt.executeUpdate();
        }
        
        // Return formatted counter using the NEW value
        return formatCounter(newSequence);
    }
    
    private synchronized String getSequenceFromMemory(String counterType) {
        int sequence = getMemoryCounter(counterType);
        sequence++;
        setMemoryCounter(counterType, sequence);
        return formatCounter(sequence);
    }
    
    private int getMemoryCounter(String counterType) {
        switch (counterType) {
            case COUNTER_PROFILE: return profileCounter;
            case COUNTER_MEMBERSHIP: return membershipCounter;
            case COUNTER_CLASS: return classCounter;
            case COUNTER_SESSION: return sessionCounter;
            case COUNTER_BOOKING: return bookingCounter;
            case COUNTER_ATTENDANCE: return attendanceCounter;
            case COUNTER_PAYMENT: return paymentCounter;
            default: return 0;
        }
    }
    
    private void setMemoryCounter(String counterType, int value) {
        switch (counterType) {
            case COUNTER_PROFILE: profileCounter = value; break;
            case COUNTER_MEMBERSHIP: membershipCounter = value; break;
            case COUNTER_CLASS: classCounter = value; break;
            case COUNTER_SESSION: sessionCounter = value; break;
            case COUNTER_BOOKING: bookingCounter = value; break;
            case COUNTER_ATTENDANCE: attendanceCounter = value; break;
            case COUNTER_PAYMENT: paymentCounter = value; break;
        }
    }
    
    // ============================================================
    // COUNTER FORMATTING (3 characters)
    // ============================================================
    
    /**
     * Format a counter value to 3-character format:
     * 0-999 → 000 to 999
     * 1000 → A00, 1001 → A01, ..., 1099 → A99
     * 1100 → B00, ..., 2599 → Z99
     */
    private String formatCounter(int value) {
        if (value < 0) {
            return "000";
        }
        
        // 0-999: Direct numbers
        if (value <= 999) {
            return String.format("%03d", value);
        }
        
        // 1000+: Letter + 2 digits
        int adjusted = value - 1000;
        int letterIndex = adjusted / 100;
        int digitPart = adjusted % 100;
        
        // If we exceed Z99, wrap around
        if (letterIndex > 25) {
            // Wrap to 000
            return "000";
        }
        
        char letter = (char) ('A' + letterIndex);
        return letter + String.format("%02d", digitPart);
    }
    
    /**
     * Parse a formatted counter back to its numeric value
     */
    public int parseCounter(String formattedCounter) {
        if (formattedCounter == null || formattedCounter.length() != 3) {
            return -1;
        }
        
        char first = formattedCounter.charAt(0);
        String rest = formattedCounter.substring(1);
        
        // Check if it's numeric (000-999)
        if (Character.isDigit(first)) {
            try {
                return Integer.parseInt(formattedCounter);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        
        // It's a letter (A-Z)
        if (first >= 'A' && first <= 'Z') {
            int letterIndex = first - 'A';
            int digitPart;
            try {
                digitPart = Integer.parseInt(rest);
            } catch (NumberFormatException e) {
                return -1;
            }
            return 1000 + (letterIndex * 100) + digitPart;
        }
        
        return -1;
    }
    
    // ============================================================
    // DATE FORMATTING
    // ============================================================
    
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyMMdd"));
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    public String getRoleFromId(String id) {
        if (id == null || id.length() < 11) {
            return "UNKNOWN";
        }
        String roleCode = id.substring(9, 11);
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
    
    public String getDateFromId(String id) {
        if (id == null || id.length() < 11) {
            return "UNKNOWN";
        }
        String yymmdd = id.substring(3, 9);
        return "20" + yymmdd.substring(0, 2) + "-" + 
               yymmdd.substring(2, 4) + "-" + 
               yymmdd.substring(4, 6);
    }
    
    public String getCounterFromId(String id) {
        if (id == null || id.length() < 11) {
            return "UNKNOWN";
        }
        return id.substring(0, 3);
    }
    
    public boolean isValidFormat(String id) {
        if (id == null || id.length() != 11) {
            return false;
        }
        // Check that the role code is valid
        String role = id.substring(9, 11);
        return role.equals("00") || role.equals("11") || role.equals("22") ||
               role.equals("33") || role.equals("44") || role.equals("55") ||
               role.equals("66") || role.equals("77") || role.equals("88");
    }
    
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