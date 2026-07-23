package com.gym.database;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProfileDatabaseManager - Handles all Profile CRUD operations
 * Replaces FileManager for Profile data
 */
public class ProfileDatabaseManager {
    
    private Connection connection;
    
    public ProfileDatabaseManager() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    /**
     * Create a new profile in the database
     */
    public Profile createProfile(String name, String email, String phone, String address) {
        String sql = "INSERT INTO profiles (name, email, phone, address, registration_date) " +
                     "VALUES (?, ?, ?, ?, CURDATE())";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                System.out.println("❌ Failed to create profile.");
                return null;
            }
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("✅ Profile created with ID: " + id);
                    return findProfileById(id);
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error creating profile: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    /**
     * Find profile by ID
     */
    public Profile findProfileById(int id) {
        String sql = "SELECT * FROM profiles WHERE profile_id = ? AND is_active = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProfile(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding profile: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Find profile by email
     */
    public Profile findProfileByEmail(String email) {
        String sql = "SELECT * FROM profiles WHERE email = ? AND is_active = TRUE";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProfile(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding profile by email: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Get all profiles
     */
    public List<Profile> findAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles WHERE is_active = TRUE ORDER BY name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding all profiles: " + e.getMessage());
        }
        return profiles;
    }
    
    /**
     * Search profiles by name or email
     */
    public List<Profile> searchProfiles(String searchTerm) {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles WHERE is_active = TRUE AND " +
                     "(name LIKE ? OR email LIKE ?) ORDER BY name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error searching profiles: " + e.getMessage());
        }
        return profiles;
    }
    
    /**
     * Get active members with valid memberships
     */
    public List<Profile> findActiveMembers() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT p.* FROM profiles p " +
                     "JOIN memberships m ON p.profile_id = m.profile_id " +
                     "WHERE p.is_active = TRUE AND m.status = 'Active' " +
                     "AND m.expiry_date >= CURDATE() ORDER BY p.name";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                profiles.add(mapResultSetToProfile(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error finding active members: " + e.getMessage());
        }
        return profiles;
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    /**
     * Update profile
     */
    public boolean updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET name = ?, email = ?, phone = ?, address = ? " +
                     "WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getEmail());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getAddress());
            stmt.setInt(5, profile.getProfileId());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Profile updated: " + rowsAffected + " row(s) affected");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update profile status (active/inactive)
     */
    public boolean updateProfileStatus(int profileId, boolean isActive) {
        String sql = "UPDATE profiles SET is_active = ? WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setInt(2, profileId);
            
            int rowsAffected = stmt.executeUpdate();
            String status = isActive ? "activated" : "deactivated";
            System.out.println("✅ Profile " + status + ": " + rowsAffected + " row(s) affected");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating profile status: " + e.getMessage());
            return false;
        }
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    /**
     * Soft delete profile (set is_active = FALSE)
     */
    public boolean deleteProfile(int profileId) {
        return updateProfileStatus(profileId, false);
    }
    
    /**
     * Hard delete profile (permanent removal)
     */
    public boolean hardDeleteProfile(int profileId) {
        String sql = "DELETE FROM profiles WHERE profile_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Profile permanently deleted: " + rowsAffected + " row(s) affected");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error deleting profile: " + e.getMessage());
            return false;
        }
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    /**
     * Get profile statistics
     */
    public ProfileStats getStatistics() {
        String sql = "SELECT " +
                     "COUNT(*) as total, " +
                     "SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) as active, " +
                     "SUM(CASE WHEN is_active = FALSE THEN 1 ELSE 0 END) as inactive " +
                     "FROM profiles";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                return new ProfileStats(
                    rs.getInt("total"),
                    rs.getInt("active"),
                    rs.getInt("inactive")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting statistics: " + e.getMessage());
        }
        return new ProfileStats(0, 0, 0);
    }
    
    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    /**
     * Map ResultSet to Profile object
     */
    private Profile mapResultSetToProfile(ResultSet rs) throws SQLException {
        Profile profile = new Profile(
            rs.getInt("profile_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address")
        );
        profile.setActive(rs.getBoolean("is_active"));
        // Note: Membership is loaded separately
        return profile;
    }
    
    /**
     * Inner class for statistics
     */
    public static class ProfileStats {
        public final int total;
        public final int active;
        public final int inactive;
        
        public ProfileStats(int total, int active, int inactive) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
        }
        
        @Override
        public String toString() {
            return String.format(
                "📊 Profile Statistics:\n" +
                "   Total Profiles: %d\n" +
                "   Active: %d\n" +
                "   Inactive: %d",
                total, active, inactive
            );
        }
    }
}
