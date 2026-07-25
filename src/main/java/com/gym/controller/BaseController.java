package com.gym.controller;

import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;

/**
 * BaseController - Abstract base controller with dual data sources
 * Primary: DatabaseManager (MySQL)
 * Secondary: JsonDataManager (JSON files - fallback)
 */
public abstract class BaseController {
    
    protected DatabaseManager databaseManager;
    protected JsonDataManager jsonDataManager;
    protected boolean useDatabase = true;
    
    public BaseController() {
        this.databaseManager = new DatabaseManager();
        this.jsonDataManager = new JsonDataManager();
    }
    
    public BaseController(boolean useDatabase) {
        this();
        this.useDatabase = useDatabase;
    }
    
    /**
     * Switch to database mode
     */
    public void switchToDatabase() {
        this.useDatabase = true;
        System.out.println("✅ Switched to Database mode");
    }
    
    /**
     * Switch to JSON mode
     */
    public void switchToJson() {
        this.useDatabase = false;
        System.out.println("✅ Switched to JSON mode");
    }
    
    /**
     * Check if using database
     */
    public boolean isUsingDatabase() {
        return useDatabase;
    }
    
    /**
     * Save all data (to both sources if possible)
     */
    public void saveAllData() {
        if (useDatabase) {
            databaseManager.saveAllData();
        }
        jsonDataManager.saveAllData();
    }
    
    /**
     * Clear all data
     */
    public void clearAllData() {
        if (useDatabase) {
            databaseManager.clearAllData();
        }
        jsonDataManager.clearAllData();
    }
}