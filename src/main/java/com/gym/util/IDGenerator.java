package com.gym.util;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating custom IDs
 * Format: [Sequence 000-999] + [MMDD] + [Role Code]
 * 
 * Example: 000072422 (Admin), 001072411 (Trainer), 002072422 (Member)
 */
public class IdGenerator {

    private Connection connection;

    public IdGenerator(Connection connection) {
        this.connection = connection;
    }

    /**
     * Generate a new profile ID
     * 
     * @param roleCode         00 = Admin, 11 = Trainer, 22 = Member
     * @param registrationDate Date of registration
     * @return Custom formatted ID
     */
    public String generateProfileId(String roleCode, LocalDate registrationDate) {
        String sequence = getNextSequence("profile");
        String mmdd = registrationDate.format(DateTimeFormatter.ofPattern("MMdd"));
        return sequence + mmdd + roleCode;
    }

    /**
     * Get next sequence number for an ID type
     * This will be replaced with a database stored procedure later
     */
    private String getNextSequence(String idType) {
        // Temporary implementation — uses in-memory counter
        // TODO: Replace with CALL generate_custom_id(?, ?) stored procedure
        return String.format("%03d", (int)(Math.random() * 1000));
    }

    /**
     * Extract role code from ID
     * 
     * @param id Profile ID (e.g., 000072422)
     * @return ADMIN, TRAINER, MEMBER, or UNKNOWN
     */
    public String getRoleFromId(String id) {
        if (id == null || id.length() < 9) {
            return "UNKNOWN";
        }
        String roleCode = id.substring(7, 9);
        switch (roleCode) {
            case "00": return "ADMIN";
            case "11": return "TRAINER";
            case "22": return "MEMBER";
            default: return "UNKNOWN";
        }
    }

    /**
     * Extract MMDD from ID
     * 
     * @param id Profile ID (e.g., 000072422)
     * @return Date in MM/DD format
     */
    public String getDateFromId(String id) {
        if (id == null || id.length() < 7) {
            return "UNKNOWN";
        }
        String mmdd = id.substring(3, 7);
        return mmdd.substring(0, 2) + "/" + mmdd.substring(2, 4);
    }

    /**
     * Validate if an ID matches the expected format
     * 
     * @param id Profile ID to validate
     * @return true if ID matches format [0-9]{3}[0-9]{4}(00|11|22)
     */
    public boolean isValidFormat(String id) {
        if (id == null || id.length() != 9) {
            return false;
        }
        // Check first 7 characters are digits
        for (int i = 0; i < 7; i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return false;
            }
        }
        // Check last 2 characters are valid role codes
        String role = id.substring(7, 9);
        return role.equals("00") || role.equals("11") || role.equals("22");
    }
}