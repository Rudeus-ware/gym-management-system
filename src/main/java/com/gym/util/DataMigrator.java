package com.gym.util;

import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.model.Profile;
import com.gym.model.Payment;
import com.gym.model.membership.Membership;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.booking.Attendance;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;
import com.gym.database.DatabaseConnection;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

/**
 * DataMigrator - Migrates data from JSON to Database
 */
public class DataMigrator {
    
    private JsonDataManager jsonManager;
    private DatabaseManager dbManager;
    private IdGenerator idGenerator;
    private Connection connection;
    
    
    public DataMigrator() {
        this.jsonManager = new JsonDataManager();
        this.dbManager = new DatabaseManager();
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.idGenerator = new IdGenerator(connection);
    }
    
    public void migrateAll() {
        System.out.println("📋 Starting data migration from JSON to Database...");
        System.out.println("=".repeat(50));
        
        migrateProfiles();
        migrateMemberships();
        migrateTrainers();
        migrateClasses();
        migrateSessions();
        migrateBookings();
        migrateAttendance();
        migratePayments();
        migrateAdmins();
        
        System.out.println("=".repeat(50));
        System.out.println("✅ Migration complete!");
    }
    
    private void migrateProfiles() {
        System.out.println("\n📁 Migrating profiles...");
        List<Profile> profiles = jsonManager.getProfiles();
        int count = 0;
        for (Profile p : profiles) {
            // ✅ FIX: Use createProfile with individual parameters
            dbManager.createProfile(
                p.getName(),
                p.getEmail(),
                p.getPhone(),
                p.getAddress(),
                p.getProfileId().substring(9, 11) // Extract role from ID
            );
            count++;
        }
        System.out.println("   ✅ " + count + " profiles migrated");
    }
    
    private void migrateMemberships() {
        System.out.println("\n📁 Migrating memberships...");
        List<Membership> memberships = jsonManager.getMemberships();
        int count = 0;
        for (Membership m : memberships) {
            double fee = m.getPrice();
            String type = m.getClass().getSimpleName();
            String status = "ACTIVE";  // Default status
            
            // ✅ CORRECT - All 5 parameters
            dbManager.createMembership(
                m.getProfileId(),                    // String: profileId
                type,                                // String: type
                status,                              // String: status (ADD THIS!)
                fee,                                 // double: fee
                1                                    // int: members
            );
        count++;
        }
        System.out.println("   ✅ " + count + " memberships migrated");
    }
    
    private void migrateTrainers() {
        System.out.println("\n📁 Migrating trainers...");
        List<Trainer> trainers = jsonManager.getTrainers();
        int count = 0;
        for (Trainer t : trainers) {
            // ✅ FIX: Create trainer with proper parameters
            dbManager.createTrainer(
                t.getName(),
                t.getEmail(),
                t.getPhone(),
                t.getAddress(),
                t.getUserId(),
                t.getPassword(),
                t.getSpecialization()
            );
            count++;
        }
        System.out.println("   ✅ " + count + " trainers migrated");
    }
    
    private void migrateClasses() {
        System.out.println("\n📁 Migrating gym classes...");
        List<GymClass> classes = jsonManager.getGymClasses();
        int count = 0;
        for (GymClass c : classes) {
            // ✅ FIX: Use the correct method
            dbManager.addGymClass(c);
            count++;
        }
        System.out.println("   ✅ " + count + " classes migrated");
    }
    
    private void migrateSessions() {
        System.out.println("\n📁 Migrating sessions...");
        List<Session> sessions = jsonManager.getSessions();
        int count = 0;
        for (Session s : sessions) {
            // ✅ FIX: Use the correct method
            dbManager.addSession(s);
            count++;
        }
        System.out.println("   ✅ " + count + " sessions migrated");
    }
    
    private void migrateBookings() {
        System.out.println("\n📁 Migrating bookings...");
        List<Booking> bookings = jsonManager.getBookings();
        int count = 0;
        for (Booking b : bookings) {
            // ✅ FIX: Use the correct method
            dbManager.addBooking(b);
            count++;
        }
        System.out.println("   ✅ " + count + " bookings migrated");
    }
    
    private void migrateAttendance() {
        System.out.println("\n📁 Migrating attendance records...");
        List<Attendance> attendance = jsonManager.getAttendanceRecords();
        int count = 0;
        for (Attendance a : attendance) {
            // ✅ FIX: Use the correct method
            dbManager.addAttendance(a);
            count++;
        }
        System.out.println("   ✅ " + count + " attendance records migrated");
    }
    
    private void migratePayments() {
        System.out.println("\n📁 Migrating payments...");
        // ✅ FIX: Use jsonManager (not jsonDataManager)
        List<Payment> payments = jsonManager.getPayments();
        int count = 0;
        for (Payment p : payments) {
            // ✅ FIX: Use the correct method
            dbManager.addPayment(p);
            count++;
        }
        System.out.println("   ✅ " + count + " payments migrated");
    }
    
    private void migrateAdmins() {
        System.out.println("\n📁 Migrating admins...");
        List<Admin> admins = jsonManager.getAdmins();
        int count = 0;
        for (Admin a : admins) {
            // ✅ FIX: Use the correct method
            dbManager.addAdmin(a);
            count++;
        }
        System.out.println("   ✅ " + count + " admins migrated");
    }
}