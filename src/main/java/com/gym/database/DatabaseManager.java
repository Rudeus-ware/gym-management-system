package com.gym.database;

import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.booking.Attendance;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;
import com.gym.model.Payment;
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
    
    // ============================================================
    // PROFILE OPERATIONS
    // ============================================================
    
    public Profile createProfile(Profile profile) {
        String sql = "INSERT INTO profiles (profile_id, name, email, phone, address, registration_date, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getProfileId());
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getEmail());
            stmt.setString(4, profile.getPhone());
            stmt.setString(5, profile.getAddress());
            stmt.setDate(6, Date.valueOf(LocalDate.now()));
            stmt.setBoolean(7, profile.isActive());
            stmt.executeUpdate();
            return profile;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Profile findProfileById(String id) {
        String sql = "SELECT * FROM profiles WHERE profile_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapProfile(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Profile> findAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles ORDER BY name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                profiles.add(mapProfile(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
    
    public void updateProfile(Profile profile) {
        String sql = "UPDATE profiles SET name = ?, email = ?, phone = ?, address = ?, is_active = ? " +
                     "WHERE profile_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getEmail());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getAddress());
            stmt.setBoolean(5, profile.isActive());
            stmt.setString(6, profile.getProfileId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteProfile(String id) {
        String sql = "DELETE FROM profiles WHERE profile_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Profile mapProfile(ResultSet rs) throws SQLException {
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
    
    // ============================================================
    // MEMBERSHIP OPERATIONS
    // ============================================================
    
    public void addMembership(Membership membership) {
        String sql = "INSERT INTO memberships (membership_id, profile_id, membership_type, fee, " +
                     "start_date, expiry_date, status, benefits, number_of_members) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, membership.getMembershipId());
            stmt.setString(2, membership.getProfileId());
            stmt.setString(3, membership.getClass().getSimpleName());
            stmt.setDouble(4, membership.getFee());
            stmt.setDate(5, Date.valueOf(LocalDate.parse(membership.getStartDate())));
            stmt.setDate(6, Date.valueOf(LocalDate.parse(membership.getExpiryDate())));
            stmt.setString(7, membership.getStatus());
            stmt.setString(8, membership.getBenefits());
            stmt.setInt(9, membership.getNumberOfMembers());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // TRAINER OPERATIONS
    // ============================================================
    
    public void addTrainer(Trainer trainer) {
        String sql = "INSERT INTO trainers (trainer_id, profile_id, specialization, user_id, " +
                     "password_hash, hire_date, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trainer.getTrainerId());
            stmt.setString(2, trainer.getProfileId());
            stmt.setString(3, trainer.getSpecialization());
            stmt.setString(4, trainer.getUserId());
            stmt.setString(5, trainer.getPassword());
            stmt.setDate(6, Date.valueOf(LocalDate.now()));
            stmt.setBoolean(7, trainer.isActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // CLASS OPERATIONS
    // ============================================================
    
    public void addGymClass(GymClass gymClass) {
        String sql = "INSERT INTO gym_classes (class_id, class_name, class_type, schedule, capacity, " +
                     "trainer_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, gymClass.getClassId());
            stmt.setString(2, gymClass.getClassName());
            stmt.setString(3, gymClass.getClass().getSimpleName());
            stmt.setString(4, gymClass.getSchedule());
            stmt.setInt(5, gymClass.getCapacity());
            stmt.setString(6, gymClass.getTrainer());
            stmt.setBoolean(7, true);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // SESSION OPERATIONS
    // ============================================================
    
    public void addSession(Session session) {
        String sql = "INSERT INTO sessions (session_id, class_id, session_date, start_time, end_time, " +
                     "duration, trainer_id, max_attendees, current_attendees, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, session.getSessionId());
            stmt.setString(2, session.getClassId());
            stmt.setDate(3, Date.valueOf(LocalDate.parse(session.getSessionDate())));
            stmt.setTime(4, Time.valueOf(session.getStartTime() + ":00"));
            stmt.setTime(5, Time.valueOf(session.getExpiryTime() + ":00"));
            stmt.setString(6, session.getDuration());
            stmt.setString(7, session.getTrainerId());
            stmt.setInt(8, session.getMaxAttendees());
            stmt.setInt(9, session.getCurrentAttendees());
            stmt.setString(10, session.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // BOOKING OPERATIONS
    // ============================================================
    
    public void addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (booking_id, profile_id, session_id, booking_date, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, booking.getBookingId());
            stmt.setString(2, booking.getProfileId());
            stmt.setString(3, booking.getSessionId());
            stmt.setDate(4, Date.valueOf(LocalDate.parse(booking.getBookingDate())));
            stmt.setString(5, booking.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // ATTENDANCE OPERATIONS
    // ============================================================
    
    public void addAttendance(Attendance attendance) {
        String sql = "INSERT INTO attendance (attendance_id, profile_id, session_id, attendance_date, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, attendance.getAttendanceId());
            stmt.setString(2, attendance.getProfileId());
            stmt.setString(3, attendance.getSessionId());
            stmt.setDate(4, Date.valueOf(LocalDate.parse(attendance.getAttendanceDate())));
            stmt.setString(5, attendance.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // SAVE & LOAD (Compatibility)
    // ============================================================
    
    public void saveAllData() {
        System.out.println("✅ Data saved to database!");
    }
    
    public void loadAllData() {
        System.out.println("✅ Data loaded from database!");
    }
    
    public void clearAllData() {
        // Option: truncate all tables
        String[] tables = {"attendance", "bookings", "sessions", "gym_classes", 
                          "memberships", "trainers", "admins", "payments", "profiles"};
        try (Statement stmt = connection.createStatement()) {
            for (String table : tables) {
                stmt.executeUpdate("DELETE FROM " + table);
                stmt.executeUpdate("ALTER TABLE " + table + " AUTO_INCREMENT = 1");
            }
            System.out.println("✅ All data cleared from database!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}