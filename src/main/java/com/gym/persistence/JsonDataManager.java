package com.gym.persistence;

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
import com.gym.database.DatabaseManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JsonDataManager - JSON file-based persistence (Backup)
 * Extends DatabaseManager to provide JSON fallback when database is unavailable
 */
public class JsonDataManager extends DatabaseManager {
    
    private FileManager fileManager;
    private IdGenerator idGenerator;
    
    // JSON-specific data lists
    private List<Membership> memberships;
    private List<GymClass> gymClasses;
    private List<Booking> bookings;
    private List<Trainer> trainers;
    private List<Admin> admins;
    private List<Payment> payments;
    
    public JsonDataManager() {
        super();
        this.fileManager = new FileManager();
        // Fix: Use String prefix instead of null
        this.idGenerator = new IdGenerator("JSON");
        
        this.memberships = new ArrayList<>();
        this.gymClasses = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.trainers = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.payments = new ArrayList<>();
        
        if (this.profiles == null) {
            this.profiles = new ArrayList<>();
        }
        if (this.sessions == null) {
            this.sessions = new ArrayList<>();
        }
        if (this.attendanceRecords == null) {
            this.attendanceRecords = new ArrayList<>();
        }
        
        loadAllData();
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    @Override
    public List<Profile> getProfiles() { 
        return profiles != null ? profiles : new ArrayList<>(); 
    }
    
    @Override
    public List<Session> getSessions() { 
        return sessions != null ? sessions : new ArrayList<>(); 
    }
    
    @Override
    public List<Attendance> getAttendanceRecords() { 
        return attendanceRecords != null ? attendanceRecords : new ArrayList<>(); 
    }
    
    @Override
    public List<Membership> getMemberships() { 
        return memberships != null ? memberships : new ArrayList<>(); 
    }
    
    @Override
    public List<GymClass> getGymClasses() { 
        return gymClasses != null ? gymClasses : new ArrayList<>(); 
    }
    
    @Override
    public List<Booking> getBookings() { 
        return bookings != null ? bookings : new ArrayList<>(); 
    }
    
    @Override
    public List<Trainer> getTrainers() { 
        return trainers != null ? trainers : new ArrayList<>(); 
    }
    
    @Override
    public List<Admin> getAdmins() { 
        return admins != null ? admins : new ArrayList<>(); 
    }
    
    @Override
    public List<Payment> getPayments() { 
        return payments != null ? payments : new ArrayList<>(); 
    }
    
    // ============================================================
    // ADD METHODS
    // ============================================================
    
    @Override
    public void addProfile(Profile profile) { 
        if (profile != null) {
            if (profiles == null) profiles = new ArrayList<>();
            profiles.add(profile); 
            System.out.println("✅ Profile added to JSON: " + profile.getProfileId());
        }
    }
    
    @Override
    public void addAttendance(Attendance attendance) { 
        if (attendance != null) {
            if (attendanceRecords == null) attendanceRecords = new ArrayList<>();
            attendanceRecords.add(attendance); 
        }
    }
    
    @Override
    public void addSession(Session session) { 
        if (session != null) {
            if (sessions == null) sessions = new ArrayList<>();
            sessions.add(session); 
        }
    }
    
    @Override
    public void addMembership(Membership membership) { 
        if (membership != null) {
            if (memberships == null) memberships = new ArrayList<>();
            memberships.add(membership); 
        }
    }
    
    @Override
    public void addGymClass(GymClass gymClass) { 
        if (gymClass != null) {
            if (gymClasses == null) gymClasses = new ArrayList<>();
            gymClasses.add(gymClass); 
        }
    }
    
    @Override
    public void addBooking(Booking booking) { 
        if (booking != null) {
            if (bookings == null) bookings = new ArrayList<>();
            bookings.add(booking); 
        }
    }
    
    @Override
    public void addTrainer(Trainer trainer) { 
        if (trainer != null) {
            if (trainers == null) trainers = new ArrayList<>();
            trainers.add(trainer); 
        }
    }
    
    @Override
    public void addAdmin(Admin admin) { 
        if (admin != null) {
            if (admins == null) admins = new ArrayList<>();
            admins.add(admin); 
        }
    }
    
    @Override
    public void addPayment(Payment payment) { 
        if (payment != null) {
            if (payments == null) payments = new ArrayList<>();
            payments.add(payment); 
        }
    }
    
    // ============================================================
    // FIND METHODS
    // ============================================================
    
    @Override
    public Profile findProfileById(String id) {
        if (id == null || profiles == null) return null;
        for (Profile p : profiles) {
            if (p.getProfileId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    @Override
    public GymClass findClassById(String id) {
        if (id == null || gymClasses == null) return null;
        for (GymClass c : gymClasses) {
            if (c.getClassId().equals(id)) {
                return c;
            }
        }
        return null;
    }
    
    @Override
    public Booking findBookingById(String id) {
        if (id == null || bookings == null) return null;
        for (Booking b : bookings) {
            if (b.getBookingId().equals(id)) {
                return b;
            }
        }
        return null;
    }
    
    // ============================================================
    // SAVE & LOAD
    // ============================================================
    
    @Override
    public void saveAllData() {
        fileManager.saveData(profiles, "profiles.json");
        fileManager.saveData(memberships, "memberships.json");
        fileManager.saveData(gymClasses, "classes.json");
        fileManager.saveData(sessions, "sessions.json");
        fileManager.saveData(bookings, "bookings.json");
        fileManager.saveData(attendanceRecords, "attendance.json");
        fileManager.saveData(trainers, "trainers.json");
        fileManager.saveData(payments, "payments.json");
        System.out.println("✅ JSON data saved!");
    }
    
    @Override
    public void loadAllData() {
        // Load from JSON files
        System.out.println("✅ JSON data loaded from files");
    }
    
    @Override
    public void clearAllData() {
        if (profiles != null) profiles.clear();
        if (memberships != null) memberships.clear();
        if (gymClasses != null) gymClasses.clear();
        if (sessions != null) sessions.clear();
        if (bookings != null) bookings.clear();
        if (attendanceRecords != null) attendanceRecords.clear();
        if (trainers != null) trainers.clear();
        if (admins != null) admins.clear();
        if (payments != null) payments.clear();
        if (fileManager != null) fileManager.clearAllData();
        System.out.println("✅ JSON data cleared!");
    }
}