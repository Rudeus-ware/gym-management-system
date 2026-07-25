package com.gym.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IdGenerator {
    
    private Object connection;
    
    public IdGenerator(Object connection) {
        this.connection = connection;
    }
    
    // ============================================================
    // ID GENERATION METHODS
    // ============================================================
    
    /**
     * Generate a profile ID
     * Format: [3-digit counter][MMDD][roleCode]
     */
    public String generateProfileId(String roleCode, LocalDate registrationDate) {
        String mmdd = registrationDate.format(DateTimeFormatter.ofPattern("MMdd"));
        String sequence = getSequence("profile");
        return sequence + mmdd + roleCode;
    }
    
    /**
     * Generate a generic ID
     * Format: [type][MMDD][sequence]
     */
    public String generateId(String idType, String roleCode, LocalDate registrationDate) {
        String mmdd = registrationDate.format(DateTimeFormatter.ofPattern("MMdd"));
        String sequence = getSequence(idType);
        return sequence + mmdd + roleCode;
    }
    
    /**
     * Generate an attendance ID
     * Format: ATT[yyyyMMdd][sequence]
     */
    public String generateAttendanceId(LocalDate date) {
        String datePart = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequence = String.format("%04d", getNextCounter());
        return "ATT" + datePart + sequence;
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    private String getSequence(String idType) {
        // In a real app, this would query a database counter
        return String.format("%03d", getNextCounter() % 1000);
    }
    
    private int getNextCounter() {
        // Simple counter - in production, use database
        return (int)(System.currentTimeMillis() % 10000);
    }
}