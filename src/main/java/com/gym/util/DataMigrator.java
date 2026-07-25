package com.gym.util;

import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.model.*;
import com.gym.model.membership.*;
import com.gym.model.classes.*;
import com.gym.model.booking.*;
import com.gym.model.user.*;

import java.time.LocalDate;
import java.util.List;

/**
 * DataMigrator - Migrates data from JSON to Database
 */
public class DataMigrator {
    
    private JsonDataManager jsonManager;
    private DatabaseManager dbManager;
    private IdGenerator idGenerator;
    
    public DataMigrator() {
        this.jsonManager = new JsonDataManager();
        this.dbManager = new DatabaseManager();
        this.idGenerator = new IdGenerator(null);
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
            dbManager.createProfile(p);
            count++;
        }
        System.out.println("   ✅ " + count + " profiles migrated");
    }
    
    private void migrateMemberships() {
        System.out.println("\n📁 Migrating memberships...");
        List<Membership> memberships = jsonManager.getMemberships();
        int count = 0;
        for (Membership m : memberships) {
            dbManager.addMembership(m);
            count++;
        }
        System.out.println("   ✅ " + count + " memberships migrated");
    }
    
    private void migrateTrainers() {
        System.out.println("\n📁 Migrating trainers...");
        List<Trainer> trainers = jsonManager.getTrainers();
        int count = 0;
        for (Trainer t : trainers) {
            dbManager.addTrainer(t);
            count++;
        }
        System.out.println("   ✅ " + count + " trainers migrated");
    }
    
    private void migrateClasses() {
        System.out.println("\n📁 Migrating gym classes...");
        List<GymClass> classes = jsonManager.getGymClasses();
        int count = 0;
        for (GymClass c : classes) {
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
            dbManager.addAttendance(a);
            count++;
        }
        System.out.println("   ✅ " + count + " attendance records migrated");
    }
    
    private void migratePayments() {
        System.out.println("\n📁 Migrating payments...");
        List<Payment> payments = jsonManager.getPayments();
        int count = 0;
        for (Payment p : payments) {
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
            dbManager.addAdmin(a);
            count++;
        }
        System.out.println("   ✅ " + count + " admins migrated");
    }
}