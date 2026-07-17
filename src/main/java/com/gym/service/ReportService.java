package com.gym.service;

public class ReportService {
    public String generateSummary(int members, int bookings) {
        return "Members: " + members + " | Bookings: " + bookings;
    }
}
