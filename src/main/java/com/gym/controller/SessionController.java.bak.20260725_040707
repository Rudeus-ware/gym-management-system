package com.gym.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.booking.Session;
import com.gym.persistence.DataManager;

public class SessionController {

    private DataManager dataManager;

    public SessionController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public List<Session> getAllSessions() {
        return dataManager.getSessions();
    }

    public List<Session> getSessionsForClass(int classId) {
        return dataManager.getSessions().stream()
            .filter(session -> session.getClassId() == classId)
            .collect(Collectors.toList());
    }

    public long getActiveSessionCount() {
        return dataManager.getSessions().size();
    }

    public long getEndedSessionCount() {
        return 0;
    }
}
