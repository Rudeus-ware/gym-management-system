package com.gym.persistence;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.Profile;
import com.gym.model.attendance.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.classes.Yoga;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Premium;
import com.gym.model.user.Trainer;

public class DataInitializer {
    
    public static void initializeTestData(DataManager dataManager) {
        System.out.println("\n📋 Initializing test data...");
        System.out.println("-".repeat(40));
        
        // ===== CREATE TRAINERS =====
        List<Trainer> trainers = new ArrayList<>();
        trainers.add(new Trainer(1, "Sarah Johnson", "sarah@gym.com", "555-111-2222", 
                                "789 Yoga Lane", "T001", "trainerPass", "Yoga"));
        trainers.add(new Trainer(2, "Mike Trainer", "mike@gym.com", "555-333-4444", 
                                "456 Spin St", "T002", "trainerPass", "Spin"));
        trainers.add(new Trainer(3, "Bob Strong", "bob@gym.com", "555-555-6666", 
                                "123 Weight Ave", "T003", "trainerPass", "Strength"));
        
        for (Trainer trainer : trainers) {
            dataManager.addTrainer(trainer);
        }
        
        // ===== CREATE PROFILES (Members) =====
        List<Profile> profiles = new ArrayList<>();
        
        // Member 1: John Doe with Basic membership
        Profile john = new Profile(1, "John Doe", "john@email.com", "555-123-4567", "123 Main St");
        Basic basicPlan = new Basic(1001, 49.99, "2026-01-01", "2026-12-31", "Active");
        john.setMembership(basicPlan);
        profiles.add(john);
        dataManager.addMembership(basicPlan);
        
        // Member 2: Sarah Smith with Premium membership
        Profile sarah = new Profile(2, "Sarah Smith", "sarah@email.com", "555-987-6543", "456 Oak Ave");
        Premium premiumPlan = new Premium(2001, 99.99, "2026-01-01", "2026-12-31", "Active", "VIP Access");
        sarah.setMembership(premiumPlan);
        profiles.add(sarah);
        dataManager.addMembership(premiumPlan);
        
        // Member 3: Johnson Family with Family membership
        Profile johnson = new Profile(3, "The Johnson Family", "johnson@email.com", "555-444-3333", "789 Family Lane");
        Family familyPlan = new Family(3001, 69.99, "2026-01-01", "2026-12-31", "Active", 4);
        johnson.setMembership(familyPlan);
        profiles.add(johnson);
        dataManager.addMembership(familyPlan);
        
        // Member 4: Alice Wonder with Basic membership
        Profile alice = new Profile(4, "Alice Wonder", "alice@email.com", "555-222-1111", "321 Dream St");
        Basic basicPlan2 = new Basic(1002, 49.99, "2026-01-01", "2026-12-31", "Active");
        alice.setMembership(basicPlan2);
        profiles.add(alice);
        dataManager.addMembership(basicPlan2);
        
        // Member 5: Charlie Brown with Basic membership
        Profile charlie = new Profile(5, "Charlie Brown", "charlie@email.com", "555-333-2222", "654 Peanut Lane");
        Basic basicPlan3 = new Basic(1003, 49.99, "2026-01-01", "2026-12-31", "Active");
        charlie.setMembership(basicPlan3);
        profiles.add(charlie);
        dataManager.addMembership(basicPlan3);
        
        for (Profile profile : profiles) {
            dataManager.addProfile(profile);
        }
        
        // ===== CREATE GYM CLASSES =====
        Yoga yogaClass = new Yoga(101, "Morning Yoga", "Mon/Wed/Fri 7:00 AM", 15, 
                                   "Sarah Johnson", "Hatha", "Beginner");
        dataManager.addGymClass(yogaClass);
        
        Spin spinClass = new Spin(102, "Evening Spin", "Tue/Thu 6:00 PM", 20,
                                   "Mike Trainer", "Medium", 45, "EDM");
        dataManager.addGymClass(spinClass);
        
        Strength strengthClass = new Strength(103, "Full Body Strength", "Mon/Wed 5:00 PM", 12,
                                               "Bob Strong", "Full body", "Intermediate");
        dataManager.addGymClass(strengthClass);
        
        // ===== CREATE SESSIONS =====
        Session session1 = new Session(1, 101, "2026-07-20", "07:00", "08:00", "1 hour", 1);
        Session session2 = new Session(2, 102, "2026-07-21", "18:00", "18:45", "45 minutes", 2);
        Session session3 = new Session(3, 103, "2026-07-22", "17:00", "18:00", "1 hour", 3);
        Session session4 = new Session(4, 101, "2026-07-22", "07:00", "08:00", "1 hour", 1);
        
        dataManager.addSession(session1);
        dataManager.addSession(session2);
        dataManager.addSession(session3);
        dataManager.addSession(session4);
        
        // ===== CREATE BOOKINGS =====
        // John books Yoga class
        Booking booking1 = new Booking(1, 1, 101, 1, "2026-07-15", "Confirmed");
        dataManager.addBooking(booking1);
        
        // Sarah books Spin class
        Booking booking2 = new Booking(2, 2, 102, 2, "2026-07-16", "Confirmed");
        dataManager.addBooking(booking2);
        
        // Alice books Yoga class
        Booking booking3 = new Booking(3, 4, 101, 1, "2026-07-17", "Confirmed");
        dataManager.addBooking(booking3);
        
        // Charlie books Yoga class
        Booking booking4 = new Booking(4, 5, 101, 1, "2026-07-17", "Pending");
        dataManager.addBooking(booking4);
        
        // Johnson books Strength class
        Booking booking5 = new Booking(5, 3, 103, 3, "2026-07-18", "Confirmed");
        dataManager.addBooking(booking5);
        
        // ===== CREATE ATTENDANCE RECORDS =====
        // John attended Yoga session
        Attendance attendance1 = new Attendance(1, 1, 1, "2026-07-20", "Present");
        dataManager.addAttendance(attendance1);
        
        // Sarah attended Spin session
        Attendance attendance2 = new Attendance(2, 2, 2, "2026-07-21", "Present");
        dataManager.addAttendance(attendance2);
        
        // Alice attended Yoga session
        Attendance attendance3 = new Attendance(3, 4, 1, "2026-07-20", "Late");
        dataManager.addAttendance(attendance3);
        
        // Charlie was absent from Yoga session
        Attendance attendance4 = new Attendance(4, 5, 1, "2026-07-20", "Absent");
        dataManager.addAttendance(attendance4);
        
        // John attended Yoga session (day 2)
        Attendance attendance5 = new Attendance(5, 1, 4, "2026-07-22", "Present");
        dataManager.addAttendance(attendance5);
        
        // ===== ADD BOOKINGS TO CLASSES (for availability tracking) =====
        // This step would normally be done when booking, but we'll do it here
        // for data consistency
        System.out.println("📌 Updating class bookings...");
        yogaClass.addBooking("John Doe");
        spinClass.addBooking("Sarah Smith");
        yogaClass.addBooking("Alice Wonder");
        yogaClass.addBooking("Charlie Brown");
        strengthClass.addBooking("The Johnson Family");
        
        System.out.println("-".repeat(40));
        System.out.println("✅ Test data initialization complete!");
        System.out.println("   Profiles: " + profiles.size());
        System.out.println("   Classes: 3");
        System.out.println("   Sessions: 4");
        System.out.println("   Bookings: 5");
        System.out.println("   Attendance: 5");
    }
}