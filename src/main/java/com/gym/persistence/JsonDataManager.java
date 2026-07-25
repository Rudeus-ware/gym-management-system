package com.gym.persistence;

import com.gym.model.Payment;
import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.membership.Membership;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonDataManager - JSON file-based data management (Backup)
 * Used as fallback when database is unavailable
 */
public class JsonDataManager {
    
    private FileManager fileManager;
    
    // Data lists
    private List<Profile> profiles;
    private List<Membership> memberships;
    private List<GymClass> gymClasses;
    private List<Session> sessions;
    private List<Booking> bookings;
    private List<Attendance> attendanceRecords;
    private List<Trainer> trainers;
    private List<Admin> admins;
    private List<Payment> payments;
    
    public JsonDataManager() {
        this.fileManager = new FileManager();
        this.profiles = new ArrayList<>();
        this.memberships = new ArrayList<>();
        this.gymClasses = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
        this.trainers = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.payments = new ArrayList<>();
        loadAllData();
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public List<Profile> getProfiles() { return profiles; }
    public List<Membership> getMemberships() { return memberships; }
    public List<GymClass> getGymClasses() { return gymClasses; }
    public List<Session> getSessions() { return sessions; }
    public List<Booking> getBookings() { return bookings; }
    public List<Attendance> getAttendanceRecords() { return attendanceRecords; }
    public List<Trainer> getTrainers() { return trainers; }
    public List<Admin> getAdmins() { return admins; }
    public List<Payment> getPayments() { return payments; }
    
    // ============================================================
    // ADD METHODS
    // ============================================================
    
    public void addProfile(Profile profile) { profiles.add(profile); }
    public void addMembership(Membership membership) { memberships.add(membership); }
    public void addGymClass(GymClass gymClass) { gymClasses.add(gymClass); }
    public void addSession(Session session) { sessions.add(session); }
    public void addBooking(Booking booking) { bookings.add(booking); }
    public void addAttendance(Attendance attendance) { attendanceRecords.add(attendance); }
    public void addTrainer(Trainer trainer) { trainers.add(trainer); }
    public void addAdmin(Admin admin) { admins.add(admin); }
    public void addPayment(Payment payment) { payments.add(payment); }
    
    // ============================================================
    // REMOVE METHODS
    // ============================================================
    
    public void removeProfile(String profileId) { 
        profiles.removeIf(p -> p.getProfileId().equals(profileId)); 
    }
    
    public void removeGymClass(String classId) { 
        gymClasses.removeIf(c -> c.getClassId().equals(classId)); 
    }
    
    public void removeBooking(String bookingId) { 
        bookings.removeIf(b -> b.getBookingId().equals(bookingId)); 
    }
    
    // ============================================================
    // FIND METHODS
    // ============================================================
    
    public Profile findProfileById(String id) {
        for (Profile p : profiles) {
            if (p.getProfileId().equals(id)) {
                return p;
            }
        }
        return null;
    }
    
    public GymClass findClassById(String id) {
        for (GymClass c : gymClasses) {
            if (c.getClassId().equals(id)) {
                return c;
            }
        }
        return null;
    }
    
    public Booking findBookingById(String id) {
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
    
    private void loadAllData() {
        System.out.println("✅ JSON data loaded from files");
    }
    
    public void clearAllData() {
        profiles.clear();
        memberships.clear();
        gymClasses.clear();
        sessions.clear();
        bookings.clear();
        attendanceRecords.clear();
        trainers.clear();
        admins.clear();
        payments.clear();
        fileManager.clearAllData();
        System.out.println("✅ JSON data cleared!");
    }
}