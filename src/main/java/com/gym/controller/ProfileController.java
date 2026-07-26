package com.gym.controller;

import com.gym.model.Profile;
import com.gym.database.DatabaseManager;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Profile operations
 */
public class ProfileController {
    
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;
    
    public ProfileController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        // Fix: Check if connection is available
        if (databaseManager != null && databaseManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(databaseManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("PROF");
        }
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    public Profile createProfile(String name, String email, String phone, String membershipType) {
        String profileId = idGenerator.generateProfileId(IdGenerator.ROLE_MEMBER, LocalDate.now());
        return databaseManager.createProfile(profileId, name, email, phone, membershipType);
    }
    
    public Profile createMember(String name, String email, String phone, String membershipType) {
        return createProfile(name, email, phone, membershipType);
    }
    
    public Profile createTrainerProfile(String name, String email, String phone, String specialization) {
        String profileId = idGenerator.generateProfileId(IdGenerator.ROLE_TRAINER, LocalDate.now());
        return databaseManager.createProfile(profileId, name, email, phone, "TRAINER");
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    public Profile getProfileById(String id) {
        return databaseManager.findProfileById(id);
    }
    
    public Profile getProfileByEmail(String email) {
        return databaseManager.findProfileByEmail(email);
    }
    
    public List<Profile> getAllProfiles() {
        return databaseManager.findAllProfiles();
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    public boolean updateProfile(Profile profile) {
        if (profile == null || profile.getProfileId() == null) {
            return false;
        }
        databaseManager.updateProfile(profile);
        return true;
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    public boolean deleteProfile(String profileId) {
        return databaseManager.deleteProfile(profileId);
    }
}