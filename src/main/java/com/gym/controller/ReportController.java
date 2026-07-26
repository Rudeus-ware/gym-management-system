package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;

import java.util.List;
import java.util.stream.Collectors;

public class ReportController {
    
    private final DatabaseManager databaseManager;
    
    public ReportController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
    }
    
    public ReportController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
    }
    
    // ============================================================
    // PROFILE REPORTS
    // ============================================================
    
    public void generateProfileReport() {
        List<Profile> profiles = databaseManager.findAllProfiles();
        System.out.println("=".repeat(60));
        System.out.println("📊 PROFILE REPORT");
        System.out.println("=".repeat(60));
        System.out.printf("%-15s %-25s %-20s%n", "ID", "Name", "Email");
        System.out.println("-".repeat(60));
        for (Profile p : profiles) {
            System.out.printf("%-15s %-25s %-20s%n", 
                p.getProfileId(), 
                truncate(p.getName(), 25), 
                truncate(p.getEmail(), 20)
            );
        }
        System.out.println("-".repeat(60));
        System.out.printf("Total Profiles: %d%n", profiles.size());
        System.out.println("=".repeat(60));
    }
    
    // ============================================================
    // CLASS REPORTS
    // ============================================================
    
    public void generateClassReport() {
        List<GymClass> classes = databaseManager.findAllClasses();
        System.out.println("=".repeat(60));
        System.out.println("📊 CLASS REPORT");
        System.out.println("=".repeat(60));
        System.out.printf("%-10s %-20s %-15s %-10s%n", "ID", "Name", "Category", "Duration");
        System.out.println("-".repeat(60));
        for (GymClass c : classes) {
            System.out.printf("%-10s %-20s %-15s %-10d%n", 
                c.getClassId(), 
                truncate(c.getName(), 20), 
                c.getCategory() != null ? truncate(c.getCategory(), 15) : "N/A",
                c.getDuration()
            );
        }
        System.out.println("-".repeat(60));
        System.out.printf("Total Classes: %d%n", classes.size());
        System.out.println("=".repeat(60));
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    private String truncate(String str, int length) {
        if (str == null) return "N/A";
        if (str.length() <= length) return str;
        return str.substring(0, length - 3) + "...";
    }
}