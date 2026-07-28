package com.gym.controller;

import com.gym.database.DatabaseManager;
import com.gym.model.classes.GymClass;
import com.gym.model.classes.Yoga;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.booking.Session;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * ClassController - Handles gym class operations
 */
public class ClassController {
    
    private GymController gymController;
    private DatabaseManager databaseManager;
    private IdGenerator idGenerator;
    
    public ClassController(GymController gymController) {
        this.gymController = gymController;
        this.databaseManager = gymController.databaseManager;
        this.idGenerator = new IdGenerator();
    }
    
    // ============================================================
    // CREATE CLASS
    // ============================================================
    
    public GymClass createClass(String name, String type, String schedule, int capacity, String trainerId) {
        String classId = idGenerator.generateClassId(LocalDate.now());
        
        GymClass gymClass = null;
        switch (type) {
            case "Yoga":
                gymClass = new Yoga(classId, name, schedule, capacity, trainerId, "Hatha", "Beginner");
                break;
            case "Spin":
                gymClass = new Spin(classId, name, schedule, capacity, trainerId, "Medium", 45, "EDM");
                break;
            case "Strength":
                gymClass = new Strength(classId, name, schedule, capacity, trainerId, "Full Body", "Intermediate");
                break;
            default:
                System.out.println("❌ Invalid class type: " + type);
                return null;
        }
        
        databaseManager.addGymClass(gymClass);
        System.out.println("✅ Class created: " + name + " (ID: " + classId + ")");
        return gymClass;
    }
    
    // ============================================================
    // FIND CLASSES
    // ============================================================
    
    public GymClass findClassById(String id) {
        return databaseManager.findClassById(id);
    }
    
    public List<GymClass> findAllClasses() {
        return databaseManager.findAllClasses();
    }
    
    public List<GymClass> getAllClasses() {
        return databaseManager.getGymClasses();
    }
    
    public boolean isClassAvailable(String classId) {
        GymClass gymClass = findClassById(classId);
        if (gymClass == null) {
            return false;
        }
        return !gymClass.isFull();
    }
    
    // ============================================================
    // SESSION MANAGEMENT
    // ============================================================
    
    public Session createSession(String classId, String date, String startTime, 
                                String endTime, String duration, String trainerId) {
        String sessionId = idGenerator.generateSessionId(LocalDate.now());
        
        Session session = new Session(
            sessionId, classId, date, startTime, endTime, duration, trainerId
        );
        
        databaseManager.addSession(session);
        System.out.println("✅ Session created: " + sessionId + " for class " + classId);
        return session;
    }
    
    public List<Session> getSessionsForClass(String classId) {
        return databaseManager.getSessionsForClass(classId);
    }
    
    public List<Session> getAllSessions() {
        return databaseManager.getSessions();
    }
}
