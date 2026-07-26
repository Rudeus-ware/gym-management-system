package com.gym.controller;

import com.gym.database.DatabaseManager;
import com.gym.util.IdGenerator;

/**
 * Base controller class with common functionality
 */
public class BaseController {
    
    protected final DatabaseManager dataManager;
    protected final IdGenerator idGenerator;
    
    public BaseController(DatabaseManager dataManager) {
        this.dataManager = dataManager;
        // Fix: Check if connection is available
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("BASE");
        }
    }
    
    public DatabaseManager getDataManager() {
        return dataManager;
    }
    
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
}