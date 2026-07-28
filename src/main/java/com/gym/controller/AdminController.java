package com.gym.controller;

import java.time.LocalDate;
import java.util.List;

import com.gym.database.DatabaseManager;
import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.user.Trainer;
import com.gym.util.IdGenerator;

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

    public Profile createMember(String name, String email, String phone, String address, String membershipType) {
        return profileController.createMember(name, email, phone, membershipType);
    }

    public Profile updateMember(String id, String name, String email, String phone, String address) {
        Profile profile = profileController.getProfileById(id);
        if (profile != null) {
            profile.updateProfile(name, email, phone, address);
            profileController.updateProfile(profile);
            return profile;
        }
        return null;
    }

    public boolean removeMember(String id) {
        return profileController.deleteProfile(id);
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
        GymClass gymClass = new GymClass(classId, name, description, duration, category, Math.max(10, duration));
        gymClass.setSchedule(schedule);
        gymClass.setTrainer(trainerId);
        databaseManager.addGymClass(gymClass);
        return gymClass;
    }

    public GymClass createClass(String name, String schedule, int capacity, String trainer, String type,
                                 String style, String difficulty) {
        String classId = idGenerator.generateClassId(LocalDate.now());
        GymClass gymClass;
        switch (type.toLowerCase()) {
            case "yoga":
                gymClass = new com.gym.model.classes.Yoga(
                    classId, name, schedule, 60, "YOGA", capacity, style, 20
                );
                break;
            case "spin":
                gymClass = new com.gym.model.classes.Spin(
                    classId, name, schedule, 45, "SPIN", capacity, trainer, 5
                );
                break;
            case "strength":
                gymClass = new com.gym.model.classes.Strength(
                    classId, name, schedule, 45, "STRENGTH", capacity, style, difficulty
                );
                break;
            default:
                return null;
        }
        gymClass.setTrainer(trainer);
        gymClass.setSchedule(schedule);
        databaseManager.addGymClass(gymClass);
        return gymClass;
    }
    
    public GymClass findClassById(String id) {
        return databaseManager.findClassById(id);
    }
    
    public List<GymClass> getAllClasses() {
        return databaseManager.findAllClasses();
    }
}