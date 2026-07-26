package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;
import com.gym.model.classes.GymClass;
import com.gym.database.DatabaseManager;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * Admin Controller - Handles admin operations
 */
public class AdminController {
    
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;
    private final ProfileController profileController;
    
    public AdminController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
        // Fix: Check if connection is available
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("ADMIN");
        }
        this.profileController = new ProfileController(dataManager);
    }
    
    // ============================================================
    // MEMBER OPERATIONS
    // ============================================================
    
    public Profile createMember(String name, String email, String phone, String membershipType) {
        return profileController.createMember(name, email, phone, membershipType);
    }
    
    public Profile getMember(String id) {
        return profileController.getProfileById(id);
    }
    
    public List<Profile> getAllMembers() {
        return profileController.getAllProfiles();
    }
    
    public boolean deleteMember(String id) {
        return profileController.deleteProfile(id);
    }
    
    // ============================================================
    // TRAINER OPERATIONS
    // ============================================================
    
    public Trainer createTrainer(String name, String email, String phone, String specialization, 
                                  String hireDate, String status) {
        String trainerId = idGenerator.generateProfileId(IdGenerator.ROLE_TRAINER, LocalDate.now());
        return databaseManager.createTrainer(trainerId, name, email, phone, specialization, hireDate, status);
    }
    
    public List<Trainer> getAllTrainers() {
        return databaseManager.findAllTrainers();
    }
    
    // ============================================================
    // CLASS OPERATIONS
    // ============================================================
    
    public GymClass createClass(String name, String description, int duration, String category, 
                                 String trainerId, String schedule) {
        String classId = idGenerator.generateClassId(LocalDate.now());
        return databaseManager.createGymClass(classId, name, description, duration, category);
    }
    
    public GymClass findClassById(String id) {
        return databaseManager.findClassById(id);
    }
    
    public List<GymClass> getAllClasses() {
        return databaseManager.findAllClasses();
    }
}