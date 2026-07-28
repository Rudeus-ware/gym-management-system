package com.gym.model.user;

import com.gym.model.Profile;
import java.util.ArrayList;
import java.util.List;

/**
 * Trainer - Represents a gym trainer/instructor
 * Extends User to inherit profile information
 */
public class Trainer extends User {
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String specialization;
    private double hourlyRate;
    private int yearsOfExperience;
    private List<String> certifications;
    private List<String> availableTimeSlots;
    private List<String> assignedClasses;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Full constructor
     */
    public Trainer(String profileId, String name, String email, String phone, String address,
                   String userId, String password, String specialization) {
        super(profileId, name, email, phone, address, userId, password);
        this.specialization = specialization;
        this.hourlyRate = 50.0;
        this.yearsOfExperience = 0;
        this.certifications = new ArrayList<>();
        this.availableTimeSlots = new ArrayList<>();
        this.assignedClasses = new ArrayList<>();
    }
    
    /**
     * Simplified constructor for migration
     */
    public Trainer(String name, String email, String phone, String address,
                   String userId, String password, String specialization) {
        this("", name, email, phone, address, userId, password, specialization);
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public String getSpecialization() { return specialization; }
    public double getHourlyRate() { return hourlyRate; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public List<String> getCertifications() { return new ArrayList<>(certifications); }
    public List<String> getAvailableTimeSlots() { return new ArrayList<>(availableTimeSlots); }
    public List<String> getAssignedClasses() { return new ArrayList<>(assignedClasses); }
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    
    public void addCertification(String certification) {
        if (certification != null && !certification.trim().isEmpty()) {
            certifications.add(certification);
        }
    }
    
    public void removeCertification(String certification) {
        certifications.remove(certification);
    }
    
    public void addAvailableTimeSlot(String timeSlot) {
        if (timeSlot != null && !timeSlot.trim().isEmpty()) {
            availableTimeSlots.add(timeSlot);
        }
    }
    
    public void removeAvailableTimeSlot(String timeSlot) {
        availableTimeSlots.remove(timeSlot);
    }
    
    public void assignClass(String className) {
        if (className != null && !className.trim().isEmpty()) {
            assignedClasses.add(className);
        }
    }
    
    public void removeClass(String className) {
        assignedClasses.remove(className);
    }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    /**
     * Update the trainer's schedule
     */
    public void updateSchedule(String className, String newSchedule) {
        System.out.println("📅 Schedule for " + className + " updated to: " + newSchedule);
    }
    
    /**
     * Check if trainer is available at a given time
     */
    public boolean isAvailable(String timeSlot) {
        return availableTimeSlots.contains(timeSlot);
    }
    
    /**
     * Calculate trainer's fee based on hourly rate and duration
     */
    public double calculateFee(double hours) {
        return hourlyRate * hours;
    }
    
    // ============================================================
    // OVERRIDE METHODS
    // ============================================================
    
    @Override
    public String viewProfile() {
        return super.viewProfile() +
               "\nRole: Trainer" +
               "\nSpecialization: " + specialization +
               "\nHourly Rate: $" + hourlyRate +
               "\nYears of Experience: " + yearsOfExperience +
               "\nCertifications: " + certifications +
               "\nAvailable Time Slots: " + availableTimeSlots;
    }
    
    @Override
    public String toString() {
        return "Trainer{" +
               "profileId='" + getProfileId() + '\'' +
               ", name='" + getName() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", specialization='" + specialization + '\'' +
               '}';
    }
}