package com.gym.controller;

import com.gym.model.classes.GymClass;
import com.gym.model.booking.Session;
import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ClassController {
    
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;
    
    public ClassController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("CLASS");
        }
    }
    
    public ClassController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
        this.idGenerator = new IdGenerator("CLASS");
    }
    
    // ============================================================
    // CLASS OPERATIONS
    // ============================================================
    
    public GymClass createClass(String name, String description, int duration, String category) {
        String classId = idGenerator.generateClassId(LocalDate.now());
        return databaseManager.createGymClass(classId, name, description, duration, category);
    }
    
    public GymClass findClassById(String id) {
        return databaseManager.findClassById(id);
    }
    
    public List<GymClass> getAllClasses() {
        return databaseManager.findAllClasses();
    }
    
    public boolean deleteClass(String classId) {
        if (classId == null || classId.isEmpty()) return false;
        boolean removed = databaseManager.getGymClasses().removeIf(c -> c.getClassId().equals(classId));
        if (removed) {
            databaseManager.saveAllData();
            System.out.println("✅ Class deleted: " + classId);
        }
        return removed;
    }
    
    // ============================================================
    // SESSION OPERATIONS
    // ============================================================
    
    public Session createSession(String classId, String trainerId, String sessionDate, 
                                  String startTime, String endTime, int maxCapacity) {
        String sessionId = idGenerator.generateSessionId(LocalDate.now());
        Session session = new Session(sessionId, classId, trainerId, sessionDate, startTime, endTime, maxCapacity);
        databaseManager.addSession(session);
        databaseManager.saveAllData();
        return session;
    }
    
    public List<Session> getSessionsForClass(String classId) {
        if (classId == null || classId.isEmpty()) return List.of();
        return databaseManager.getSessions().stream()
            .filter(s -> s.getClassId().equals(classId))
            .collect(Collectors.toList());
    }
}