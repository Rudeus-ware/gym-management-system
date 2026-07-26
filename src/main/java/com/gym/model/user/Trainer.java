package com.gym.model.user;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Trainer {
    
    // ===== FIELDS =====
    private String trainerId;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private String hireDate;
    private String status;
    private String password;
    private double hourlyRate;
    private int yearsOfExperience;
    private String certification;
    private boolean isAvailable;
    private List<String> availableTimeSlots;
    private List<String> certifications;
    private int maxClients;
    private List<String> currentClients;
    private String profileImage;
    private String bio;
    private double rating;
    private int totalSessions;
    
    // ===== CONSTANTS =====
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_ON_LEAVE = "ON_LEAVE";
    public static final String STATUS_TERMINATED = "TERMINATED";
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Main constructor - MATCHES THE CALL
     */
    public Trainer(String trainerId, String name, String email, String phone, 
                   String specialization, String hireDate, String status) {
        this.trainerId = trainerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.hireDate = hireDate != null ? hireDate : LocalDate.now().toString();
        this.status = status != null ? status : "ACTIVE";
        this.password = "default123";
        this.hourlyRate = 50.0;
        this.yearsOfExperience = 1;
        this.certification = "Certified Personal Trainer";
        this.isAvailable = true;
        this.maxClients = 10;
        this.rating = 4.0;
        this.totalSessions = 0;
        this.availableTimeSlots = new ArrayList<>();
        this.certifications = new ArrayList<>();
        this.currentClients = new ArrayList<>();
        this.bio = "Experienced fitness trainer dedicated to helping clients achieve their goals.";
    }
    
    /**
     * Constructor with basic information
     */
    public Trainer(String trainerId, String name, String email, String specialization) {
        this(trainerId, name, email, null, specialization, 
             LocalDate.now().toString(), "ACTIVE");
    }
    
    /**
     * Constructor with all details
     */
    public Trainer(String trainerId, String name, String email, String phone,
                   String specialization, String hireDate, String status,
                   String password, double hourlyRate, int yearsOfExperience) {
        this(trainerId, name, email, phone, specialization, hireDate, status);
        this.password = password;
        this.hourlyRate = hourlyRate;
        this.yearsOfExperience = yearsOfExperience;
    }
    
    // ===== GETTERS =====
    public String getTrainerId() { return trainerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSpecialization() { return specialization; }
    public String getHireDate() { return hireDate; }
    public String getStatus() { return status; }
    public String getPassword() { return password; }
    public double getHourlyRate() { return hourlyRate; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public String getCertification() { return certification; }
    public boolean isAvailable() { return isAvailable; }
    public List<String> getAvailableTimeSlots() { return new ArrayList<>(availableTimeSlots); }
    public List<String> getCertifications() { return new ArrayList<>(certifications); }
    public int getMaxClients() { return maxClients; }
    public List<String> getCurrentClients() { return new ArrayList<>(currentClients); }
    public int getCurrentClientCount() { return currentClients.size(); }
    public String getProfileImage() { return profileImage; }
    public String getBio() { return bio; }
    public double getRating() { return rating; }
    public int getTotalSessions() { return totalSessions; }
    
    // ===== SETTERS =====
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
    public void setStatus(String status) { this.status = status; }
    public void setPassword(String password) { this.password = password; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public void setCertification(String certification) { this.certification = certification; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public void setMaxClients(int maxClients) { this.maxClients = maxClients; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setBio(String bio) { this.bio = bio; }
    public void setRating(double rating) { this.rating = Math.min(5.0, Math.max(0.0, rating)); }
    
    // ===== BUSINESS METHODS =====
    
    /**
     * Check if trainer is active
     */
    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }
    
    /**
     * Activate the trainer
     */
    public void activate() {
        this.status = STATUS_ACTIVE;
        this.isAvailable = true;
        System.out.println("✅ Trainer " + trainerId + " has been activated.");
    }
    
    /**
     * Deactivate the trainer
     */
    public void deactivate() {
        this.status = STATUS_INACTIVE;
        this.isAvailable = false;
        System.out.println("⚠️ Trainer " + trainerId + " has been deactivated.");
    }
    
    /**
     * Set trainer on leave
     */
    public void setOnLeave() {
        this.status = STATUS_ON_LEAVE;
        this.isAvailable = false;
        System.out.println("⚠️ Trainer " + trainerId + " is now on leave.");
    }
    
    /**
     * Terminate trainer
     */
    public void terminate() {
        this.status = STATUS_TERMINATED;
        this.isAvailable = false;
        System.out.println("⚠️ Trainer " + trainerId + " has been terminated.");
    }
    
    /**
     * Add a certification to the trainer's profile
     */
    public boolean addCertification(String certification) {
        if (certification == null || certification.trim().isEmpty()) {
            return false;
        }
        if (!certifications.contains(certification)) {
            certifications.add(certification);
            updateRating();
            return true;
        }
        return false;
    }
    
    /**
     * Remove a certification
     */
    public boolean removeCertification(String certification) {
        boolean removed = certifications.remove(certification);
        if (removed) {
            updateRating();
        }
        return removed;
    }
    
    /**
     * Add an available time slot
     */
    public boolean addAvailableTimeSlot(String timeSlot) {
        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return false;
        }
        if (!availableTimeSlots.contains(timeSlot)) {
            availableTimeSlots.add(timeSlot);
            return true;
        }
        return false;
    }
    
    /**
     * Remove a time slot
     */
    public boolean removeAvailableTimeSlot(String timeSlot) {
        return availableTimeSlots.remove(timeSlot);
    }
    
    /**
     * Check if trainer is available at a specific time
     */
    public boolean isAvailableAt(String timeSlot) {
        return isAvailable && isActive() && availableTimeSlots.contains(timeSlot);
    }
    
    /**
     * Assign a client to this trainer
     */
    public boolean assignClient(String clientId) {
        if (!canTakeMoreClients()) {
            System.out.println("⚠️ Trainer " + trainerId + " has reached maximum client capacity.");
            return false;
        }
        if (clientId == null || clientId.trim().isEmpty()) {
            return false;
        }
        if (!currentClients.contains(clientId)) {
            currentClients.add(clientId);
            return true;
        }
        return false;
    }
    
    /**
     * Remove a client from this trainer
     */
    public boolean removeClient(String clientId) {
        return currentClients.remove(clientId);
    }
    
    /**
     * Check if trainer can take more clients
     */
    public boolean canTakeMoreClients() {
        return isAvailable && isActive() && currentClients.size() < maxClients;
    }
    
    /**
     * Get the number of available slots for new clients
     */
    public int getAvailableSlots() {
        return Math.max(0, maxClients - currentClients.size());
    }
    
    /**
     * Record a completed session
     */
    public void recordSession() {
        this.totalSessions++;
    }
    
    /**
     * Update trainer rating based on experience and certifications
     */
    private void updateRating() {
        double baseRating = 3.0;
        // Experience bonus: 0.3 per year up to 3 years, then 0.2 per year
        if (yearsOfExperience <= 3) {
            baseRating += yearsOfExperience * 0.3;
        } else {
            baseRating += 0.9 + (yearsOfExperience - 3) * 0.2;
        }
        // Certification bonus: 0.3 per certification
        baseRating += certifications.size() * 0.3;
        // Session bonus: 0.1 per 10 sessions
        baseRating += (totalSessions / 10) * 0.1;
        // Max rating is 5.0
        this.rating = Math.min(5.0, Math.round(baseRating * 10.0) / 10.0);
    }
    
    /**
     * Get formatted rating with stars
     */
    public String getRatingStars() {
        int fullStars = (int) Math.floor(rating);
        String stars = "⭐".repeat(Math.min(fullStars, 5));
        if (rating - fullStars >= 0.5 && fullStars < 5) {
            stars += "½";
        }
        return stars + " (" + String.format("%.1f", rating) + "/5.0)";
    }
    
    /**
     * Get formatted hire date
     */
    public String getFormattedHireDate() {
        try {
            LocalDate date = LocalDate.parse(hireDate);
            return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return hireDate;
        }
    }
    
    /**
     * Get days since hired
     */
    public long getDaysSinceHired() {
        try {
            LocalDate hired = LocalDate.parse(hireDate);
            return java.time.temporal.ChronoUnit.DAYS.between(hired, LocalDate.now());
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get status display
     */
    public String getStatusDisplay() {
        return switch (status) {
            case STATUS_ACTIVE -> "✅ Active";
            case STATUS_INACTIVE -> "❌ Inactive";
            case STATUS_ON_LEAVE -> "⏳ On Leave";
            case STATUS_TERMINATED -> "⛔ Terminated";
            default -> "❓ " + status;
        };
    }
    
    /**
     * Get availability display
     */
    public String getAvailabilityDisplay() {
        if (!isActive()) return "❌ Not Active";
        return isAvailable ? "🟢 Available" : "🔴 Unavailable";
    }
    
    /**
     * Validate trainer credentials
     */
    public boolean validateCredentials(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
    
    /**
     * Change password
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) {
            System.out.println("❌ Current password is incorrect.");
            return false;
        }
        if (newPassword == null || newPassword.length() < 6) {
            System.out.println("❌ New password must be at least 6 characters.");
            return false;
        }
        this.password = newPassword;
        System.out.println("✅ Password changed successfully for " + name);
        return true;
    }
    
    /**
     * Get trainer profile information
     */
    public String viewProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("         TRAINER PROFILE\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append("Trainer ID:      ").append(trainerId).append("\n");
        sb.append("Name:            ").append(name).append("\n");
        sb.append("Email:           ").append(email).append("\n");
        sb.append("Phone:           ").append(phone != null ? phone : "N/A").append("\n");
        sb.append("Specialization:  ").append(specialization).append("\n");
        sb.append("Hire Date:       ").append(getFormattedHireDate());
        sb.append(" (").append(getDaysSinceHired()).append(" days ago)\n");
        sb.append("Status:          ").append(getStatusDisplay()).append("\n");
        sb.append("Availability:    ").append(getAvailabilityDisplay()).append("\n");
        sb.append("Hourly Rate:     $").append(String.format("%.2f", hourlyRate)).append("\n");
        sb.append("Experience:      ").append(yearsOfExperience).append(" years\n");
        sb.append("Certification:   ").append(certification).append("\n");
        sb.append("Rating:          ").append(getRatingStars()).append("\n");
        sb.append("Total Sessions:  ").append(totalSessions).append("\n");
        sb.append("Clients:         ").append(currentClients.size()).append("/").append(maxClients).append("\n");
        sb.append("Available Slots: ").append(getAvailableSlots()).append("\n");
        sb.append("\nCertifications:\n");
        if (certifications.isEmpty()) {
            sb.append("  • None\n");
        } else {
            for (String cert : certifications) {
                sb.append("  • ").append(cert).append("\n");
            }
        }
        sb.append("\nAvailable Time Slots:\n");
        if (availableTimeSlots.isEmpty()) {
            sb.append("  • None\n");
        } else {
            for (String slot : availableTimeSlots) {
                sb.append("  • ").append(slot).append("\n");
            }
        }
        sb.append("\nBio:\n").append(bio).append("\n");
        sb.append("═══════════════════════════════════════\n");
        return sb.toString();
    }
    
    /**
     * Get trainer summary for display in lists
     */
    public String getSummary() {
        return String.format("%s (%s) - %s, %d years, %d clients, %s",
            name, trainerId, specialization, yearsOfExperience, 
            currentClients.size(), getStatusDisplay());
    }
    
    /**
     * Get short info for dropdowns
     */
    public String getShortInfo() {
        return String.format("%s - %s (%s)", name, specialization, getAvailabilityDisplay());
    }
    
    /**
     * Get trainer statistics
     */
    public String getStatistics() {
        return String.format("""
            Trainer Statistics:
            • Total Sessions: %d
            • Current Clients: %d/%d
            • Available Slots: %d
            • Rating: %.1f/5.0
            • Experience: %d years
            • Certifications: %d
            """, totalSessions, currentClients.size(), maxClients, 
            getAvailableSlots(), rating, yearsOfExperience, certifications.size());
    }
    
    /**
     * Get a list of available time slots as formatted string
     */
    public String getAvailableTimeSlotsFormatted() {
        if (availableTimeSlots.isEmpty()) {
            return "No time slots available";
        }
        return String.join(", ", availableTimeSlots);
    }
    
    /**
     * Check if trainer has a specific certification
     */
    public boolean hasCertification(String certification) {
        return certifications.contains(certification);
    }
    
    /**
     * Update all trainer ratings (bulk update)
     */
    public void updateAllRatings() {
        updateRating();
    }
    
    // ===== OVERRIDE METHODS =====
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Trainer trainer = (Trainer) obj;
        return trainerId != null && trainerId.equals(trainer.trainerId);
    }
    
    @Override
    public int hashCode() {
        return trainerId != null ? trainerId.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("Trainer{id='%s', name='%s', specialization='%s', status='%s', clients=%d/%d}",
            trainerId, name, specialization, status, currentClients.size(), maxClients);
    }
}