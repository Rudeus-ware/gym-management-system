package com.gym.database;

import com.gym.model.Profile;
import com.gym.util.IdGenerator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    
    private Connection connection;
    private IdGenerator idGenerator;
    
    public DatabaseManager() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.idGenerator = new IdGenerator(connection);
    }
    
    // ===== PROFILE CRUD =====
    
    public Profile createProfile(String name, String email, String phone, String address, 
                                 String roleCode) {
        LocalDate registrationDate = LocalDate.now();
        
        // ✅ Generate custom ID
        String profileId = idGenerator.generateProfileId(roleCode, registrationDate);
        
        if (profileId == null) {
            System.err.println("❌ Failed to generate profile ID");
            return null;
        }
        
        String sql = "INSERT INTO profiles (profile_id, name, email, phone, address, registration_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profileId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setDate(6, Date.valueOf(registrationDate));
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return findProfileById(profileId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Profile findProfileById(String id) {
        String sql = "SELECT * FROM profiles WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Profile profile = new Profile(
                    rs.getString("profile_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                profile.setActive(rs.getBoolean("is_active"));
                return profile;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Profile> findAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles ORDER BY profile_id";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Profile profile = new Profile(
                    rs.getString("profile_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address")
                );
                profile.setActive(rs.getBoolean("is_active"));
                profiles.add(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
    
    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET name = ?, email = ?, phone = ?, address = ? " +
                     "WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getEmail());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getAddress());
            stmt.setString(5, profile.getProfileId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteProfile(String id) {
        String sql = "DELETE FROM profiles WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}