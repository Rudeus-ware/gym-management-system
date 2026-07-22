package com.gym.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.Profile;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.GymClass;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.classes.Yoga;
import com.gym.persistence.DataManager;

/**
 * Controller for Class operations
 * Handles all gym class-related business logic
 */
public class ClassController {
    
    private DataManager dataManager;
    
    public ClassController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ===== CREATE CLASS =====
    
    /**
     * Create a new Yoga class
     */
    public GymClass createYogaClass(String name, String schedule, int capacity, 
                                    String trainer, String style, String difficulty) {
        int classId = dataManager.getGymClasses().size() + 101;
        Yoga yogaClass = new Yoga(classId, name, schedule, capacity, trainer, style, difficulty);
        dataManager.addGymClass(yogaClass);
        return yogaClass;
    }
    
    /**
     * Create a new Spin class
     */
    public GymClass createSpinClass(String name, String schedule, int capacity,
                                    String trainer, String intensity, int duration) {
        int classId = dataManager.getGymClasses().size() + 102;
        Spin spinClass = new Spin(classId, name, schedule, capacity, trainer, intensity, duration, "EDM");
        dataManager.addGymClass(spinClass);
        return spinClass;
    }
    
    /**
     * Create a new Strength class
     */
    public GymClass createStrengthClass(String name, String schedule, int capacity,
                                        String trainer, String focusArea, String intensity) {
        int classId = dataManager.getGymClasses().size() + 103;
        Strength strengthClass = new Strength(classId, name, schedule, capacity, trainer, focusArea, intensity);
        dataManager.addGymClass(strengthClass);
        return strengthClass;
    }
    
    // ===== FIND CLASSES =====
    
    /**
     * Find a class by ID
     */
    public GymClass findClassById(int classId) {
        return dataManager.findClassById(classId);
    }
    
    /**
     * Get all classes
     */
    public List<GymClass> getAllClasses() {
        return dataManager.getGymClasses();
    }
    
    /**
     * Get classes by type
     */
    public List<GymClass> getClassesByType(String type) {
        return dataManager.getGymClasses().stream()
            .filter(c -> c.getClass().getSimpleName().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
    
    /**
     * Get classes by trainer
     */
    public List<GymClass> getClassesByTrainer(String trainerName) {
        return dataManager.getGymClasses().stream()
            .filter(c -> c.getTrainer().equalsIgnoreCase(trainerName))
            .collect(Collectors.toList());
    }
    
    /**
     * Get classes with available spots
     */
    public List<GymClass> getAvailableClasses() {
        return dataManager.getGymClasses().stream()
            .filter(c -> !c.isFull())
            .collect(Collectors.toList());
    }
    
    // ===== CLASS MANAGEMENT =====
    
    /**
     * Check if a class has available spots
     */
    public boolean isClassAvailable(int classId) {
        GymClass gymClass = findClassById(classId);
        if (gymClass == null) return false;
        return !gymClass.isFull();
    }
    
    /**
     * Get available spots in a class
     */
    public int getAvailableSpots(int classId) {
        GymClass gymClass = findClassById(classId);
        if (gymClass == null) return 0;
        return gymClass.getCapacity() - gymClass.getCurrentBookings();
    }
    
    /**
     * Book a member into a class
     */
    public boolean bookMemberInClass(int profileId, int classId) {
        Profile profile = dataManager.findProfileById(profileId);
        GymClass gymClass = findClassById(classId);
        
        if (profile == null || gymClass == null) {
            return false;
        }
        
        // Check if member has valid membership
        if (profile.getMembership() == null || !profile.getMembership().isValid()) {
            System.out.println("❌ Member has no valid membership!");
            return false;
        }
        
        // Check if class has availability
        if (!gymClass.checkAvailability()) {
            System.out.println("❌ Class is full!");
            return false;
        }
        
        // Add booking to class
        gymClass.addBooking(profile.getName());
        
        // Create booking record
        int bookingId = dataManager.getBookings().size() + 1;
        String bookingDate = LocalDate.now().toString();
        Booking booking = new Booking(bookingId, profileId, classId, 0, bookingDate, "Confirmed");
        dataManager.addBooking(booking);
        dataManager.saveAllData();
        
        return true;
    }
    
    /**
     * Cancel a member's booking in a class
     */
    public boolean cancelBooking(int profileId, int classId) {
        Profile profile = dataManager.findProfileById(profileId);
        GymClass gymClass = findClassById(classId);
        
        if (profile == null || gymClass == null) {
            return false;
        }
        
        // Remove from class
        gymClass.removeBooking(profile.getName());
        
        // Remove booking record
        dataManager.getBookings().removeIf(b -> 
            b.getProfileId() == profileId && b.getClassId() == classId
        );
        
        dataManager.saveAllData();
        return true;
    }
    
    // ===== SESSION MANAGEMENT =====
    
    /**
     * Create a session for a class
     */
    public Session createSession(int classId, String date, String startTime, 
                                 String endTime, String duration, int trainerId) {
        int sessionId = dataManager.getSessions().size() + 1;
        Session session = new Session(sessionId, classId, date, startTime, endTime, duration, trainerId);
        dataManager.addSession(session);
        return session;
    }
    
    /**
     * Get all sessions for a class
     */
    public List<Session> getSessionsForClass(int classId) {
        return dataManager.getSessions().stream()
            .filter(s -> s.getClassId() == classId)
            .collect(Collectors.toList());
    }
    
    // ===== STATISTICS =====
    
    /**
     * Get class utilization rate
     */
    public double getClassUtilizationRate() {
        List<GymClass> classes = dataManager.getGymClasses();
        if (classes.isEmpty()) return 0.0;
        
        int totalCapacity = 0;
        int totalBookings = 0;
        
        for (GymClass c : classes) {
            totalCapacity += c.getCapacity();
            totalBookings += c.getCurrentBookings();
        }
        
        return (double) totalBookings / totalCapacity * 100;
    }
    
    /**
     * Get popular classes (sorted by bookings)
     */
    public List<GymClass> getPopularClasses() {
        return dataManager.getGymClasses().stream()
            .sorted((c1, c2) -> Integer.compare(c2.getCurrentBookings(), c1.getCurrentBookings()))
            .collect(Collectors.toList());
    }
}