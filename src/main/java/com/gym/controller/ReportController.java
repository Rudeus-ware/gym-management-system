package com.gym.controller;

import java.util.List;

import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.persistence.DataManager;

/**
 * Controller for reports and statistics.
 */
public class ReportController {

    private final DataManager dataManager;

    public ReportController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public String getMemberReport() {
        List<Profile> profiles = dataManager.getProfiles();
        long active = profiles.stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .count();

        return String.format(
            "📊 MEMBER REPORT\n" +
            "=================\n" +
            "Total Members: %d\n" +
            "Active Members: %d\n" +
            "Inactive Members: %d\n" +
            "Membership Types:\n" +
            "  - Basic: %d\n" +
            "  - Premium: %d\n" +
            "  - Family: %d",
            profiles.size(),
            active,
            profiles.size() - active,
            countMembershipType("Basic"),
            countMembershipType("Premium"),
            countMembershipType("Family")
        );
    }

    public String getClassReport() {
        List<GymClass> classes = dataManager.getGymClasses();
        int totalCapacity = classes.stream().mapToInt(GymClass::getCapacity).sum();
        int totalBookings = classes.stream().mapToInt(GymClass::getCurrentBookings).sum();
        double utilization = totalCapacity > 0 ? (double) totalBookings / totalCapacity * 100 : 0;

        return String.format(
            "📊 CLASS REPORT\n" +
            "================\n" +
            "Total Classes: %d\n" +
            "Total Capacity: %d\n" +
            "Total Bookings: %d\n" +
            "Utilization Rate: %.1f%%",
            classes.size(),
            totalCapacity,
            totalBookings,
            utilization
        );
    }

    public String getBookingReport() {
        return String.format(
            "📊 BOOKING REPORT\n" +
            "=================\n" +
            "Total Bookings: %d",
            dataManager.getBookings().size()
        );
    }

    private long countMembershipType(String type) {
        return dataManager.getProfiles().stream()
            .filter(profile -> profile.getMembership() != null)
            .filter(profile -> profile.getMembership().getClass().getSimpleName().equalsIgnoreCase(type))
            .count();
    }
}
