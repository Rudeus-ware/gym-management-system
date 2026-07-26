package com.gym.controller;

import com.gym.model.booking.Session;
import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SessionController {
    
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;
    
    public SessionController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("SESS");
        }
    }
    
    public SessionController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
        this.idGenerator = new IdGenerator("SESS");
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
    
    public List<Session> getAllSessions() {
        return databaseManager.findAllSessions();
    }
    
    public List<Session> getSessionsForClass(String classId) {
        if (classId == null || classId.isEmpty()) return List.of();
        return databaseManager.getSessions().stream()
            .filter(s -> s.getClassId().equals(classId))
            .collect(Collectors.toList());
    }
    
    public Session getSessionById(String id) {
        if (id == null || id.isEmpty()) return null;
        return databaseManager.getSessions().stream()
            .filter(s -> s.getSessionId().equals(id))
            .findFirst()
            .orElse(null);
    }
}