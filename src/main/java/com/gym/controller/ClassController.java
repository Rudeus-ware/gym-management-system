package com.gym.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.Profile;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.GymClass;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.classes.Yoga;
import com.gym.database.DatabaseManager;
import com.gym.util.IdGenerator;

/**
 * Controller for Class operations
 * Handles all gym class-related business logic
 * Updated to use String-based IDs
 */
public class ClassController {
    
    private DatabaseManager dataManager;
    private IdGenerator idGenerator;
    
    public ClassController(JsonDataManager dataManager) {
        this.dataManager = dataManager;
        this.idGenerator = new IdGenerator(null); // Will be initialized properly
    }
    
    // ============================================================
    // CREATE CLASS
    // ============================================================
    
    /**
     * Create a new Yoga class
     */
    public GymClass createYogaClass(String name, String schedule, int capacity, 
                                    String trainer, String style, String difficulty) {
        String classId = generateClassId("YGA");
        Yoga yogaClass = new Yoga(classId, name, schedule, capacity, trainer, style, difficulty);
        databaseManager.addGymClass(yogaClass);
        System.out.println("✅ Yoga class created: " + name + " (ID: " + classId + ")");
        return yogaClass;
    }
    
    /**
     * Create a new Spin class
     */
    public GymClass createSpinClass(String name, String schedule, int capacity,
                                    String trainer, String intensity, int duration) {
        String classId = generateClassId("SPN");
        Spin spinClass = new Spin(classId, name, schedule, capacity, trainer, intensity, duration, "EDM");
        databaseManager.addGymClass(spinClass);
        System.out.println("✅ Spin class created: " + name + " (ID: " + classId + ")");
        return spinClass;
    }
    
    /**
     * Create a new Strength class
     */
    public GymClass createStrengthClass(String name, String schedule, int capacity,
                                        String trainer, String focusArea, String intensity) {
        String classId = generateClassId("STR");
        Strength strengthClass = new Strength(classId, name, schedule, capacity, trainer, focusArea, intensity);
        databaseManager.addGymClass(strengthClass);
        System.out.println("✅ Strength class created: " + name + " (ID: " + classId + ")");
        return strengthClass;
    }
    
    // ============================================================
    // FIND CLASSES
    // ============================================================
    
    /**
     * Find a class by ID
     */
    public GymClass findClassById(String classId) {  // ✅ Changed to String
        if (classId == null || classId.isEmpty()) {
            System.out.println("❌ Invalid class ID");
            return null;
        }
        return databaseManager.findClassById(classId);
    }
    
    /**
     * Get all classes
     */
    public List<GymClass> getAllClasses() {
        return databaseManager.getGymClasses();
    }
    
    /**
     * Get classes by type
     */
    public List<GymClass> getClassesByType(String type) {
        if (type == null || type.isEmpty()) {
            return getAllClasses();
        }
        return databaseManager.getGymClasses().stream()
            .filter(c -> c.getClass().getSimpleName().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
    
    /**
     * Get classes by trainer
     */
    public List<GymClass> getClassesByTrainer(String trainerName) {
        if (trainerName == null || trainerName.isEmpty()) {
            return getAllClasses();
        }
        return databaseManager.getGymClasses().stream()
            .filter(c -> c.getTrainer().equalsIgnoreCase(trainerName))
            .collect(Collectors.toList());
    }
    
    /**
     * Get classes with available spots
     */
    public List<GymClass> getAvailableClasses() {
        return databaseManager.getGymClasses().stream()
            .filter(c -> !c.isFull())
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // CLASS MANAGEMENT
    // ============================================================
    
    /**
     * Check if a class has available spots
     */
    public boolean isClassAvailable(String classId) {  // ✅ Changed to String
        if (classId == null || classId.isEmpty()) {
            return false;
        }
        GymClass gymClass = findClassById(classId);
        if (gymClass == null) return false;
        return !gymClass.isFull();
    }
    
    /**
     * Get available spots in a class
     */
    public int getAvailableSpots(String classId) {  // ✅ Changed to String
        if (classId == null || classId.isEmpty()) {
            return 0;
        }
        GymClass gymClass = findClassById(classId);
        if (gymClass == null) return 0;
        return gymClass.getCapacity() - gymClass.getCurrentBookings();
    }
    
    /**
     * Book a member into a class
     */
    public boolean bookMemberInClass(String profileId, String classId) {  // ✅ Changed to String
        if (profileId == null || profileId.isEmpty() || classId == null || classId.isEmpty()) {
            System.out.println("❌ Invalid profile ID or class ID");
            return false;
        }
        
        Profile profile = databaseManager.findProfileById(profileId);
        GymClass gymClass = findClassById(classId);
        
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        if (gymClass == null) {
            System.out.println("❌ Class not found: " + classId);
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
        
        // Create booking record with String ID
        String bookingId = generateBookingId();
        String bookingDate = LocalDate.now().toString();
        Booking booking = new Booking(bookingId, profileId, classId, "", bookingDate, "Confirmed");
        databaseManager.addBooking(booking);
        databaseManager.saveAllData();
        
        System.out.println("✅ Member " + profile.getName() + " booked into " + gymClass.getClassName());
        return true;
    }
    
    /**
     * Cancel a member's booking in a class
     */
    public boolean cancelBooking(String profileId, String classId) {  // ✅ Changed to String
        if (profileId == null || profileId.isEmpty() || classId == null || classId.isEmpty()) {
            System.out.println("❌ Invalid profile ID or class ID");
            return false;
        }
        
        Profile profile = databaseManager.findProfileById(profileId);
        GymClass gymClass = findClassById(classId);
        
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return false;
        }
        if (gymClass == null) {
            System.out.println("❌ Class not found: " + classId);
            return false;
        }
        
        // Remove from class
        gymClass.removeBooking(profile.getName());
        
        // Remove booking record
        databaseManager.getBookings().removeIf(b -> 
            b.getProfileId().equals(profileId) && b.getClassId().equals(classId)  // ✅ Use .equals()
        );
        
        databaseManager.saveAllData();
        System.out.println("✅ Booking cancelled for " + profile.getName());
        return true;
    }
    
    // ============================================================
    // SESSION MANAGEMENT
    // ============================================================
    
    /**
     * Create a session for a class
     */
    public Session createSession(String classId, String date, String startTime,  // ✅ Changed classId to String
                                 String endTime, String duration, String trainerId) {  // ✅ Changed trainerId to String
        if (classId == null || classId.isEmpty()) {
            System.out.println("❌ Invalid class ID");
            return null;
        }
        
        String sessionId = generateSessionId();
        Session session = new Session(sessionId, classId, date, startTime, endTime, duration, trainerId);
        databaseManager.addSession(session);
        System.out.println("✅ Session created: " + sessionId + " for class " + classId);
        return session;
    }
    
    /**
     * Get all sessions for a class
     */
    public List<Session> getSessionsForClass(String classId) {  // ✅ Changed to String
        if (classId == null || classId.isEmpty()) {
            return List.of();
        }
        return databaseManager.getSessions().stream()
            .filter(s -> s.getClassId().equals(classId))  // ✅ Use .equals()
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get class utilization rate
     */
    public double getClassUtilizationRate() {
        List<GymClass> classes = databaseManager.getGymClasses();
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
        return databaseManager.getGymClasses().stream()
            .sorted((c1, c2) -> Integer.compare(c2.getCurrentBookings(), c1.getCurrentBookings()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get class count by type
     */
    public long getClassCountByType(String type) {
        if (type == null || type.isEmpty()) {
            return 0;
        }
        return databaseManager.getGymClasses().stream()
            .filter(c -> c.getClass().getSimpleName().equalsIgnoreCase(type))
            .count();
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    /**
     * Generate a unique class ID
     */
    private String generateClassId(String prefix) {
        if (idGenerator != null) {
            return idGenerator.generateId("class", prefix, LocalDate.now());
        }
        // Fallback
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd"));
        int count = databaseManager.getGymClasses().size() + 1;
        return prefix + timestamp + String.format("%04d", count);
    }
    
    /**
     * Generate a unique session ID
     */
    private String generateSessionId() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = databaseManager.getSessions().size() + 1;
        return "SES" + timestamp + String.format("%04d", count);
    }
    
    /**
     * Generate a unique booking ID
     */
    private String generateBookingId() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = databaseManager.getBookings().size() + 1;
        return "BKG" + timestamp + String.format("%04d", count);
    }
}