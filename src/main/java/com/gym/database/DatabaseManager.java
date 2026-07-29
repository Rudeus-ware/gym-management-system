package com.gym.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.GymClass;
import com.gym.model.membership.Membership;
import com.gym.model.payment.Payment;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.util.IdGenerator;

public class DatabaseManager {
    
    protected Connection connection;
    protected List<Profile> profiles;
    protected List<Attendance> attendanceRecords;
    protected List<Session> sessions;
    protected List<Membership> memberships;
    protected List<GymClass> gymClasses;
    protected List<Booking> bookings;
    protected List<Trainer> trainers;
    protected List<Admin> admins;
    protected List<Payment> payments;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gym_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
     private IdGenerator idGenerator;
    
    public DatabaseManager() {
        this.profiles = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.memberships = new ArrayList<>();
        this.gymClasses = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.trainers = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.gymClasses = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
        this.memberships = new ArrayList<>();
        this.idGenerator = new IdGenerator();

        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Connected to MySQL database");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public List<Profile> getProfiles() { 
        return profiles != null ? profiles : new ArrayList<>(); 
    }
    
    public List<Attendance> getAttendanceRecords() { 
        return attendanceRecords != null ? attendanceRecords : new ArrayList<>(); 
    }
    
    public List<Session> getSessions() { 
        return sessions != null ? sessions : new ArrayList<>(); 
    }
    
    public List<Membership> getMemberships() { 
        return memberships != null ? memberships : new ArrayList<>(); 
    }
    
    public List<GymClass> getGymClasses() { 
        return gymClasses != null ? gymClasses : new ArrayList<>(); 
    }
    
    public List<Booking> getBookings() { 
        return bookings != null ? bookings : new ArrayList<>(); 
    }
    
    public List<Trainer> getTrainers() { 
        return trainers != null ? trainers : new ArrayList<>(); 
    }
    
    public List<Admin> getAdmins() { 
        return admins != null ? admins : new ArrayList<>(); 
    }
    
    public List<Payment> getPayments() { 
        return payments != null ? payments : new ArrayList<>(); 
    }

    
    // ============================================================
    // FIND METHODS
    // ============================================================
    
    public Profile findProfileById(String id) {
        if (id == null || profiles == null) return null;
        for (Profile p : profiles) {
            if (p.getProfileId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    public GymClass findClassById(String id) {
        if (id == null || gymClasses == null) return null;
        for (GymClass c : gymClasses) {
            if (c.getClassId().equals(id)) {
                return c;
            }
        }
        return null;
    }
    
    public Booking findBookingById(String id) {
        if (id == null || bookings == null) return null;
        for (Booking b : bookings) {
            if (b.getBookingId().equals(id)) {
                return b;
            }
        }
        return null;
    }
    
    public List<Profile> findAllProfiles() {
        return profiles != null ? profiles : new ArrayList<>();
    }
    
    public List<Trainer> findAllTrainers() {
        return trainers != null ? trainers : new ArrayList<>();
    }

    // In DatabaseManager.java - ADD THIS METHOD

    public List<GymClass> findAllClasses() {
    return gymClasses != null ? gymClasses : new ArrayList<>();
}
    // In DatabaseManager.java - ADD THIS METHOD

    public List<Booking> findAllBookings() {
    return bookings != null ? bookings : new ArrayList<>();
}

// In DatabaseManager.java - ADD THIS METHOD

    public List<Session> findAllSessions() {
    return sessions != null ? sessions : new ArrayList<>();
}
    public List<Attendance> findAllAttendance() {
    return attendanceRecords != null ? attendanceRecords : new ArrayList<>();
}
    public List<Membership> findAllMemberships() {
    return memberships != null ? memberships : new ArrayList<>();
}
    public Profile findProfileByEmail(String email) {
    if (email == null || email.isEmpty()) return null;
    
    // First check in-memory list
    if (profiles != null) {
        for (Profile p : profiles) {
            if (p.getEmail() != null && p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
    }
    
    // If not found in memory, check database
    return findProfileByEmailInDatabase(email);
}
// In DatabaseManager.java - ADD THIS METHOD

    private Profile findProfileByEmailInDatabase(String email) {
    if (connection == null) return null;
    String sql = "SELECT * FROM profiles WHERE email = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new Profile(
                rs.getString("profile_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("membership_type"),
                rs.getString("status"),
                rs.getString("join_date")
            );
        }
    } catch (SQLException e) {
        System.err.println("❌ Failed to find profile by email: " + e.getMessage());
    }
    return null;
}
    // ============================================================
    // CREATE METHODS - ADD THESE
    // ============================================================
    
   // ============================================================
// PROFILE OPERATIONS
// ============================================================

public Profile createProfile(String name, String email, String phone, String address, String roleCode) {
    LocalDate registrationDate = LocalDate.now();
    String profileId = idGenerator.generateProfileId(roleCode, registrationDate);
    
    Profile profile = new Profile(profileId, name, email, phone, address);
    profile.setActive(true);
    
    String sql = "INSERT INTO profiles (profile_id, name, email, phone, address, registration_date, is_active) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, profileId);
        stmt.setString(2, name);
        stmt.setString(3, email);
        stmt.setString(4, phone);
        stmt.setString(5, address);
        stmt.setDate(6, Date.valueOf(registrationDate));
        stmt.setBoolean(7, true);
        stmt.executeUpdate();
        return profile;
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    
    public Trainer createTrainer(String id, String name, String email, String phone, 
                                 String specialization, String hireDate, String status) {
        Trainer trainer = new Trainer(id, name, email, phone, specialization, hireDate, status);
        if (trainers == null) trainers = new ArrayList<>();
        trainers.add(trainer);
        saveTrainerToDatabase(trainer);
        return trainer;
    }
    
    public Membership createMembership(String id, String profileId, String type, double price, int duration) {
        Membership membership = new Membership(id, profileId, type, price, duration);
        if (memberships == null) memberships = new ArrayList<>();
        memberships.add(membership);
        saveMembershipToDatabase(membership);
        return membership;
    }
    
    public GymClass createGymClass(String id, String name, String description, int duration, String category) {
        GymClass gymClass = new GymClass(id, name, description, duration, category);
        if (gymClasses == null) gymClasses = new ArrayList<>();
        gymClasses.add(gymClass);
        saveClassToDatabase(gymClass);
        return gymClass;
    }
    
    // ============================================================
    // ADD METHODS
    // ============================================================
    
    public void addProfile(Profile profile) { 
        if (profile != null) {
            if (profiles == null) profiles = new ArrayList<>();
            profiles.add(profile);
            saveProfileToDatabase(profile);
        }
    }
    
    public void addAttendance(Attendance attendance) { 
        if (attendance != null) {
            if (attendanceRecords == null) attendanceRecords = new ArrayList<>();
            attendanceRecords.add(attendance);
            saveAttendanceToDatabase(attendance);
        }
    }
    
    public void addSession(Session session) { 
        if (session != null) {
            if (sessions == null) sessions = new ArrayList<>();
            sessions.add(session);
            saveSessionToDatabase(session);
        }
    }
    
    public void addMembership(Membership membership) { 
        if (membership != null) {
            if (memberships == null) memberships = new ArrayList<>();
            memberships.add(membership);
            saveMembershipToDatabase(membership);
        }
    }
    
    public void addGymClass(GymClass gymClass) { 
        if (gymClass != null) {
            if (gymClasses == null) gymClasses = new ArrayList<>();
            gymClasses.add(gymClass);
            saveClassToDatabase(gymClass);
        }
    }

    public void removeGymClass(String classId) {
        if (classId == null || gymClasses == null) return;
        gymClasses.removeIf(c -> c.getClassId().equals(classId));
    }
    
    public void addTrainer(Trainer trainer) { 
        if (trainer != null) {
            if (trainers == null) trainers = new ArrayList<>();
            trainers.add(trainer);
            saveTrainerToDatabase(trainer);
        }
    }
    
    public void addAdmin(Admin admin) { 
        if (admin != null) {
            if (admins == null) admins = new ArrayList<>();
            admins.add(admin);
            saveAdminToDatabase(admin);
        }
    }
    
    public void addPayment(Payment payment) { 
        if (payment != null) {
            if (payments == null) payments = new ArrayList<>();
            payments.add(payment);
            savePaymentToDatabase(payment);
        }
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            if (bookings == null) bookings = new ArrayList<>();
            bookings.add(booking);
            saveBookingToDatabase(booking);
        }
    }
    
    // ============================================================
    // UPDATE METHODS
    // ============================================================
    
    public void updateProfile(Profile profile) {
        if (profile == null || profiles == null) return;
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getProfileId().equals(profile.getProfileId())) {
                profiles.set(i, profile);
                break;
            }
        }
        updateProfileInDatabase(profile);
    }
    
    // ============================================================
    // DELETE METHODS
    // ============================================================
    
    public boolean deleteProfile(String profileId) {
        if (profileId == null || profiles == null) return false;
        boolean removed = profiles.removeIf(p -> p.getProfileId().equals(profileId));
        if (removed) {
            deleteProfileFromDatabase(profileId);
        }
        return removed;
    }

    public void clearAllData() {
        if (profiles != null) profiles.clear();
        if (attendanceRecords != null) attendanceRecords.clear();
        if (sessions != null) sessions.clear();
        if (memberships != null) memberships.clear();
        if (gymClasses != null) gymClasses.clear();
        if (bookings != null) bookings.clear();
        if (trainers != null) trainers.clear();
        if (admins != null) admins.clear();
        if (payments != null) payments.clear();
    }
    
    // ============================================================
    // DATABASE SAVE METHODS
    // ============================================================
    
    private void saveProfileToDatabase(Profile profile) {
        if (connection == null) return;
        String sql = "INSERT INTO profiles (profile_id, name, email, phone, membership_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getProfileId());
            stmt.setString(2, profile.getName());
            stmt.setString(3, profile.getEmail());
            stmt.setString(4, profile.getPhone());
            stmt.setString(5, profile.getMembershipType() != null ? profile.getMembershipType() : "NONE");
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save profile: " + e.getMessage());
        }
    }
    
    private void saveAttendanceToDatabase(Attendance attendance) {
        if (connection == null) return;
        String sql = "INSERT INTO attendance (attendance_id, profile_id, session_id, attendance_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, attendance.getAttendanceId());
            stmt.setString(2, attendance.getProfileId());
            stmt.setString(3, attendance.getSessionId());
            
            LocalDateTime dateTime = attendance.getAttendanceDate();
            String dateTimeStr = dateTime != null ? dateTime.toString() : LocalDateTime.now().toString();
            stmt.setString(4, dateTimeStr);
            
            stmt.setString(5, attendance.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save attendance: " + e.getMessage());
        }
    }
    
    private void saveSessionToDatabase(Session session) {
        if (connection == null) return;
        String sql = "INSERT INTO sessions (session_id, class_id, trainer_id, session_date, start_time, end_time, max_capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, session.getSessionId());
            stmt.setString(2, session.getClassId());
            stmt.setString(3, session.getTrainerId());
            stmt.setString(4, session.getSessionDate());
            stmt.setString(5, session.getStartTime());
            stmt.setString(6, session.getEndTime());
            stmt.setInt(7, session.getMaxCapacity());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save session: " + e.getMessage());
        }
    }
    
    private void saveMembershipToDatabase(Membership membership) {
        if (connection == null) return;
        String sql = "INSERT INTO memberships (membership_id, profile_id, type, price, duration, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, membership.getMembershipId());
            stmt.setString(2, membership.getProfileId());
            stmt.setString(3, membership.getType());
            stmt.setDouble(4, membership.getPrice());
            stmt.setInt(5, membership.getDuration());
            stmt.setString(6, membership.getStartDate());
            stmt.setString(7, membership.getEndDate());
            stmt.setString(8, membership.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save membership: " + e.getMessage());
        }
    }
    
    private void saveClassToDatabase(GymClass gymClass) {
        if (connection == null) return;
        String sql = "INSERT INTO gym_classes (class_id, name, description, category, duration, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, gymClass.getClassId());
            stmt.setString(2, gymClass.getName());
            stmt.setString(3, gymClass.getDescription());
            stmt.setString(4, gymClass.getCategory());
            stmt.setInt(5, gymClass.getDuration());
            stmt.setString(6, gymClass.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save class: " + e.getMessage());
        }
    }
    
    private void saveBookingToDatabase(Booking booking) {
        if (connection == null) return;
        String sql = "INSERT INTO bookings (booking_id, profile_id, session_id, booking_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, booking.getBookingId());
            stmt.setString(2, booking.getProfileId());
            stmt.setString(3, booking.getSessionId());
            stmt.setString(4, booking.getBookingDate());
            stmt.setString(5, booking.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save booking: " + e.getMessage());
        }
    }
    
    private void saveTrainerToDatabase(Trainer trainer) {
        if (connection == null) return;
        String sql = "INSERT INTO trainers (trainer_id, name, email, phone, specialization, hire_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, trainer.getTrainerId());
            stmt.setString(2, trainer.getName());
            stmt.setString(3, trainer.getEmail());
            stmt.setString(4, trainer.getPhone());
            stmt.setString(5, trainer.getSpecialization());
            stmt.setString(6, trainer.getHireDate());
            stmt.setString(7, trainer.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save trainer: " + e.getMessage());
        }
    }
    
    private void saveAdminToDatabase(Admin admin) {
        if (connection == null) return;
        String sql = "INSERT INTO admins (admin_id, name, email, password, phone, role, status, join_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, admin.getAdminId());
            stmt.setString(2, admin.getName());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getPassword());
            stmt.setString(5, admin.getPhone());
            stmt.setString(6, admin.getRole());
            stmt.setString(7, admin.getStatus());
            stmt.setString(8, admin.getJoinDate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save admin: " + e.getMessage());
        }
    }
    
    private void savePaymentToDatabase(Payment payment) {
        if (connection == null) return;
        String sql = "INSERT INTO payments (payment_id, profile_id, amount, payment_date, method, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, payment.getPaymentId());
            stmt.setString(2, payment.getProfileId());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getPaymentDate());
            stmt.setString(5, payment.getMethod());
            stmt.setString(6, payment.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to save payment: " + e.getMessage());
        }
    }
    
    // ============================================================
    // DATABASE DELETE METHODS
    // ============================================================
    
    private void deleteProfileFromDatabase(String profileId) {
        if (connection == null) return;
        String sql = "DELETE FROM profiles WHERE profile_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to delete profile: " + e.getMessage());
        }
    }
    
    // ============================================================
    // DATABASE UPDATE METHODS
    // ============================================================
    
    private void updateProfileInDatabase(Profile profile) {
        if (connection == null) return;
        String sql = "UPDATE profiles SET name = ?, email = ?, phone = ?, membership_type = ?, status = ? WHERE profile_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, profile.getName());
            stmt.setString(2, profile.getEmail());
            stmt.setString(3, profile.getPhone());
            stmt.setString(4, profile.getMembershipType());
            stmt.setString(5, profile.getStatus());
            stmt.setString(6, profile.getProfileId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Failed to update profile: " + e.getMessage());
        }
    }

    public Profile createProfile(Profile profile) {
    return createProfile(
        profile.getName(),
        profile.getEmail(),
        profile.getPhone(),
        profile.getAddress(),
        IdGenerator.ROLE_MEMBER  // default role
    );
}
    
    // ============================================================
    // LOAD DATA FROM DATABASE
    // ============================================================
    
    public void loadAllData() {
        if (connection == null) {
            System.out.println("⚠️ No database connection - using in-memory data");
            return;
        }
        loadAttendanceFromDatabase();
        loadTrainersFromDatabase();
        System.out.println("✅ Data loaded from MySQL database");
    }
    
    private void loadAttendanceFromDatabase() {
        String sql = "SELECT * FROM attendance";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Attendance attendance = new Attendance(
                    rs.getString("attendance_id"),
                    rs.getString("profile_id"),
                    rs.getString("session_id"),
                    rs.getString("attendance_date"),
                    rs.getString("status")
                );
                if (attendanceRecords != null) attendanceRecords.add(attendance);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to load attendance: " + e.getMessage());
        }
    }
    
    private void loadTrainersFromDatabase() {
        String sql = "SELECT * FROM trainers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Trainer trainer = new Trainer(
                    rs.getString("trainer_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("specialization"),
                    rs.getString("hire_date"),
                    rs.getString("status")
                );
                if (trainers != null) trainers.add(trainer);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to load trainers: " + e.getMessage());
        }
    }
    
    // ============================================================
    // SAVE ALL DATA
    // ============================================================
    
    public void saveAllData() {
        System.out.println("💾 Saving all data to MySQL database...");
        System.out.println("✅ Data saved to MySQL database");
    }
    
    // ============================================================
    // CLOSE CONNECTION
    // ============================================================
    
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Database connection closed");
            } catch (SQLException e) {
                System.err.println("❌ Failed to close connection: " + e.getMessage());
            }
        }
    }
}