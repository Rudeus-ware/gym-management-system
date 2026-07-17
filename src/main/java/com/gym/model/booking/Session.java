package com.gym.model.booking;

public class Session {
    private final String sessionId;
    private final String className;
    private final String trainerName;

    public Session(String sessionId, String className, String trainerName) {
        this.sessionId = sessionId;
        this.className = className;
        this.trainerName = trainerName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClassName() {
        return className;
    }

    public String getTrainerName() {
        return trainerName;
    }
}
