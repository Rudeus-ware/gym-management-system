package com.gym.persistence;

import com.google.gson.reflect.TypeToken;
import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.classes.Yoga;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.attendance.Attendance;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
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
    
    public DataManager() {
        this.fileManager = new FileManager();
        loadAllData();
    }
    
    // ===== LOAD ALL DATA =====
    
    private void loadAllData() {
        System.out.println("🔄 Loading data from files...");
        System.out.println("-".repeat(40));
        
        // Load profiles
        Type profileType = new TypeToken<List<Profile>>(){}.getType();
        profiles = fileManager.loadData("profiles.json", profileType);
        
        // Load memberships
        Type membershipType = new TypeToken<List<Membership>>(){}.getType();
        memberships = fileManager.loadData("memberships.json", membershipType);
        
        // Load gym classes
        Type classType = new TypeToken<List<GymClass>>(){}.getType();
        gymClasses = fileManager.loadData("classes.json", classType);
        
        // Load sessions
        Type sessionType = new TypeToken<List<Session>>(){}.getType();
        sessions = fileManager.loadData("sessions.json", sessionType);
        
        // Load bookings
        Type bookingType = new TypeToken<List<Booking>>(){}.getType();
        bookings = fileManager.loadData("bookings.json", bookingType);
        
        // Load attendance
        Type attendanceType = new TypeToken<List<Attendance>>(){}.getType();
        attendanceRecords = fileManager.loadData("attendance.json", attendanceType);
        
        // Load trainers
        Type trainerType = new TypeToken<List<Trainer>>(){}.getType();
        trainers = fileManager.loadData("trainers.json", trainerType);
        
        System.out.println("-".repeat(40));
        System.out.println("✅ All data loaded!");
        System.out.println("   Profiles: " + profiles.size());
        System.out.println("   Memberships: " + memberships.size());
        System.out.println("   Classes: " + gymClasses.size());
        System.out.println("   Sessions: " + sessions.size());
        System.out.println("   Bookings: " + bookings.size());
        System.out.println("   Attendance: " + attendanceRecords.size());
        System.out.println("   Trainers: " + trainers.size());
    }
    
    // ===== SAVE ALL DATA =====
    
    public void saveAllData() {
        System.out.println("💾 Saving data to files...");
        System.out.println("-".repeat(40));
        
        fileManager.saveData(profiles, "profiles.json");
        fileManager.saveData(memberships, "memberships.json");
        fileManager.saveData(gymClasses, "classes.json");
        fileManager.saveData(sessions, "sessions.json");
        fileManager.saveData(bookings, "bookings.json");
        fileManager.saveData(attendanceRecords, "attendance.json");
        fileManager.saveData(trainers, "trainers.json");
        
        System.out.println("-".repeat(40));
        System.out.println("✅ All data saved!");
    }
    
    // ===== GETTERS =====
    
    public List<Profile> getProfiles() { return profiles; }
    public List<Membership> getMemberships() { return memberships; }
    public List<GymClass> getGymClasses() { return gymClasses; }
    public List<Session> getSessions() { return sessions; }
    public List<Booking> getBookings() { return bookings; }
    public List<Attendance> getAttendanceRecords() { return attendanceRecords; }
    public List<Trainer> getTrainers() { return trainers; }
    public List<Admin> getAdmins() { return admins; }
    
    // ===== ADD METHODS =====
    
    public void addProfile(Profile profile) {
        profiles.add(profile);
        System.out.println("✅ Profile added: " + profile.getName());
        saveAllData();
    }
    
    public void addMembership(Membership membership) {
        memberships.add(membership);
        System.out.println("✅ Membership added: " + membership.getClass().getSimpleName());
        saveAllData();
    }
    
    public void addGymClass(GymClass gymClass) {
        gymClasses.add(gymClass);
        System.out.println("✅ Class added: " + gymClass.getClassName());
        saveAllData();
    }
    
    public void addSession(Session session) {
        sessions.add(session);
        System.out.println("✅ Session added: " + session.getSessionId());
        saveAllData();
    }
    
    public void addBooking(Booking booking) {
        bookings.add(booking);
        System.out.println("✅ Booking added: " + booking.getBookingId());
        saveAllData();
    }
    
    public void addAttendance(Attendance attendance) {
        attendanceRecords.add(attendance);
        System.out.println("✅ Attendance recorded: " + attendance.getAttendanceId());
        saveAllData();
    }
    
    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
        System.out.println("✅ Trainer added: " + trainer.getName());
        saveAllData();
    }
    
    // ===== REMOVE METHODS =====
    
    public void removeProfile(int profileId) {
        profiles.removeIf(p -> p.getProfileId() == profileId);
        System.out.println("✅ Profile removed: " + profileId);
        saveAllData();
    }
    
    public void removeGymClass(int classId) {
        gymClasses.removeIf(c -> c.getClassId() == classId);
        System.out.println("✅ Class removed: " + classId);
        saveAllData();
    }
    
    public void removeBooking(int bookingId) {
        bookings.removeIf(b -> b.getBookingId() == bookingId);
        System.out.println("✅ Booking removed: " + bookingId);
        saveAllData();
    }
    
    // ===== CLEAR DATA =====
    
    public void clearAllData() {
        fileManager.clearAllData();
        loadAllData(); // Reload empty data
    }
    
    // ===== FIND METHODS =====
    
    public Profile findProfileById(int id) {
        for (Profile p : profiles) {
            if (p.getProfileId() == id) {
                return p;
            }
        }
        return null;
    }
    
    public GymClass findClassById(int id) {
        for (GymClass c : gymClasses) {
            if (c.getClassId() == id) {
                return c;
            }
        }
        return null;
    }
    
    public Booking findBookingById(int id) {
        for (Booking b : bookings) {
            if (b.getBookingId() == id) {
                return b;
            }
        }
        return null;
    }
    
    // ===== DISPLAY STATISTICS =====
    
    public void displayStatistics() {
        System.out.println("\n📊 DATA STORAGE STATISTICS");
        System.out.println("=".repeat(40));
        System.out.println("Profiles:      " + profiles.size());
        System.out.println("Memberships:   " + memberships.size());
        System.out.println("Gym Classes:   " + gymClasses.size());
        System.out.println("Sessions:      " + sessions.size());
        System.out.println("Bookings:      " + bookings.size());
        System.out.println("Attendance:    " + attendanceRecords.size());
        System.out.println("Trainers:      " + trainers.size());
        System.out.println("=".repeat(40));
    }
}