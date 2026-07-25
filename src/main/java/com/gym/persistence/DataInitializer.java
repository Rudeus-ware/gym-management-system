package com.gym.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.classes.Yoga;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Premium;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;

/**
 * DataInitializer - Initializes test data for the application
 * Updated to use String-based IDs
 */
public class DataInitializer {
    
    private static int profileCounter = 1;
    private static int membershipCounter = 1000;
    private static int classCounter = 101;
    private static int sessionCounter = 1;
    private static int bookingCounter = 1;
    private static int attendanceCounter = 1;
    
    public static void initializeTestData(DataManager dataManager) {
        System.out.println("\n📋 Initializing test data...");
        System.out.println("-".repeat(40));
        
        // Reset counters
        profileCounter = 1;
        membershipCounter = 1000;
        classCounter = 101;
        sessionCounter = 1;
        bookingCounter = 1;
        attendanceCounter = 1;
        
        // ============================================================
        // 1. CREATE DEFAULT ADMIN USER
        // ============================================================
        Admin defaultAdmin = new Admin(
            generateProfileId("00"),                // ✅ String profileId
            "System Admin",                         // name
            "admin@gym.com",                        // email
            "555-000-0000",                         // phone
            "Admin Office, Gym HQ",                 // address
            "A001",                                 // userId
            "admin123",                             // password
            "Super Admin"                           // adminLevel
        );
        dataManager.addAdmin(defaultAdmin);
        System.out.println("✅ Default Admin created: admin@gym.com / admin123");
        
        // ============================================================
        // 2. CREATE DEFAULT TEST USERS
        // ============================================================
        
        // Default User 1: Test User with Basic membership
        Profile testUser1 = new Profile(
            generateProfileId("22"),                // ✅ String profileId
            "Test User One",
            "test1@email.com",
            "555-111-2222",
            "123 Test Street"
        );
        testUser1.setActive(true);
        Basic testMembership1 = new Basic(
            generateMembershipId("BAS"),            // ✅ String membershipId
            49.99,
            LocalDate.now().toString(),
            LocalDate.now().plusMonths(1).toString(),
            "Active"
        );
        testUser1.setMembership(testMembership1);
        dataManager.addProfile(testUser1);
        dataManager.addMembership(testMembership1);
        System.out.println("✅ Test User 1 created: test1@email.com / password");
        
        // Default User 2: Test User with Premium membership
        Profile testUser2 = new Profile(
            generateProfileId("22"),                // ✅ String profileId
            "Test User Two",
            "test2@email.com",
            "555-333-4444",
            "456 Test Avenue"
        );
        testUser2.setActive(true);
        Premium testMembership2 = new Premium(
            generateMembershipId("PRE"),            // ✅ String membershipId
            99.99,
            LocalDate.now().toString(),
            LocalDate.now().plusMonths(1).toString(),
            "Active",
            "VIP Access"
        );
        testUser2.setMembership(testMembership2);
        dataManager.addProfile(testUser2);
        dataManager.addMembership(testMembership2);
        System.out.println("✅ Test User 2 created: test2@email.com / password");
        
        // ============================================================
        // 3. CREATE REGULAR TRAINERS
        // ============================================================
        List<Trainer> trainers = new ArrayList<>();
        
        Trainer trainer1 = new Trainer(
            generateProfileId("11"),                // ✅ String profileId
            "Sarah Johnson",
            "sarah@gym.com",
            "555-111-2222",
            "789 Yoga Lane",
            "T001",
            "trainerPass",
            "Yoga"
        );
        trainers.add(trainer1);
        
        Trainer trainer2 = new Trainer(
            generateProfileId("11"),                // ✅ String profileId
            "Mike Trainer",
            "mike@gym.com",
            "555-333-4444",
            "456 Spin St",
            "T002",
            "trainerPass",
            "Spin"
        );
        trainers.add(trainer2);
        
        Trainer trainer3 = new Trainer(
            generateProfileId("11"),                // ✅ String profileId
            "Bob Strong",
            "bob@gym.com",
            "555-555-6666",
            "123 Weight Ave",
            "T003",
            "trainerPass",
            "Strength"
        );
        trainers.add(trainer3);
        
        for (Trainer trainer : trainers) {
            dataManager.addTrainer(trainer);
        }
        System.out.println("✅ " + trainers.size() + " Trainers created");
        
        // ============================================================
        // 4. CREATE REGULAR MEMBERS (Profiles)
        // ============================================================
        List<Profile> profiles = new ArrayList<>();
        
        // Member 1: John Doe with Basic membership
        Profile john = new Profile(
            generateProfileId("22"),                // ✅ String profileId
            "John Doe",
            "john@email.com",
            "555-123-4567",
            "123 Main St"
        );
        Basic basicPlan = new Basic(
            generateMembershipId("BAS"),            // ✅ String membershipId
            49.99,
            "2026-01-01",
            "2026-12-31",
            "Active"
        );
        john.setMembership(basicPlan);
        john.setActive(true);
        profiles.add(john);
        dataManager.addMembership(basicPlan);
        
        // Member 2: Sarah Smith with Premium membership
        Profile sarah = new Profile(
            generateProfileId("22"),                // ✅ String profileId
            "Sarah Smith",
            "sarah@email.com",
            "555-987-6543",
            "456 Oak Ave"
        );
        Premium premiumPlan = new Premium(
            generateMembershipId("PRE"),            // ✅ String membershipId
            99.99,
            "2026-01-01",
            "2026-12-31",
            "Active",
            "VIP Access"
        );
        sarah.setMembership(premiumPlan);
        sarah.setActive(true);
        profiles.add(sarah);
        dataManager.addMembership(premiumPlan);
        
        // Member 3: Johnson Family with Family membership
        Profile johnson = new Profile(
            generateProfileId("22"),                // ✅ String profileId
            "The Johnson Family",
            "johnson@email.com",
            "555-444-3333",
            "789 Family Lane"
        );
        Family familyPlan = new Family(
            generateMembershipId("FAM"),            // ✅ String membershipId
            69.99,
            "2026-01-01",
            "2026-12-31",
            "Active",
            4
        );
        johnson.setMembership(familyPlan);
        johnson.setActive(true);
        profiles.add(johnson);
        dataManager.addMembership(familyPlan);
        
        // Member 4: Alice Wonder with Basic membership
        Profile alice = new Profile(
            generateProfileId("22"),                // ✅ String profileId
            "Alice Wonder",
            "alice@email.com",
            "555-222-1111",
            "321 Dream St"
        );
        Basic basicPlan2 = new Basic(
            generateMembershipId("BAS"),            // ✅ String membershipId
            49.99,
            "2026-01-01",
            "2026-12-31",
            "Active"
        );
        alice.setMembership(basicPlan2);
        alice.setActive(true);
        profiles.add(alice);
        dataManager.addMembership(basicPlan2);
        
        // Member 5: Charlie Brown with Basic membership
        Profile charlie = new Profile(
            generateProfileId("22"),                // ✅ String profileId
            "Charlie Brown",
            "charlie@email.com",
            "555-333-2222",
            "654 Peanut Lane"
        );
        Basic basicPlan3 = new Basic(
            generateMembershipId("BAS"),            // ✅ String membershipId
            49.99,
            "2026-01-01",
            "2026-12-31",
            "Active"
        );
        charlie.setMembership(basicPlan3);
        charlie.setActive(true);
        profiles.add(charlie);
        dataManager.addMembership(basicPlan3);
        
        for (Profile profile : profiles) {
            dataManager.addProfile(profile);
        }
        System.out.println("✅ " + profiles.size() + " Regular Members created");
        
        // ============================================================
        // 5. CREATE GYM CLASSES
        // ============================================================
        Yoga yogaClass = new Yoga(
            generateClassId("YGA"),                 // ✅ String classId
            "Morning Yoga",
            "Mon/Wed/Fri 7:00 AM",
            15,
            "Sarah Johnson",
            "Hatha",
            "Beginner"
        );
        dataManager.addGymClass(yogaClass);
        
        Spin spinClass = new Spin(
            generateClassId("SPN"),                 // ✅ String classId
            "Evening Spin",
            "Tue/Thu 6:00 PM",
            20,
            "Mike Trainer",
            "Medium",
            45,
            "EDM"
        );
        dataManager.addGymClass(spinClass);
        
        Strength strengthClass = new Strength(
            generateClassId("STR"),                 // ✅ String classId
            "Full Body Strength",
            "Mon/Wed 5:00 PM",
            12,
            "Bob Strong",
            "Full body",
            "Intermediate"
        );
        dataManager.addGymClass(strengthClass);
        System.out.println("✅ 3 Gym Classes created");
        
        // ============================================================
        // 6. CREATE SESSIONS
        // ============================================================
        Session session1 = new Session(
            generateSessionId(),                    // ✅ String sessionId
            yogaClass.getClassId(),                 // ✅ String classId
            "2026-07-20",
            "07:00",
            "08:00",
            "1 hour",
            trainer1.getProfileId()                 // ✅ String trainerId
        );
        dataManager.addSession(session1);
        
        Session session2 = new Session(
            generateSessionId(),                    // ✅ String sessionId
            spinClass.getClassId(),                 // ✅ String classId
            "2026-07-21",
            "18:00",
            "18:45",
            "45 minutes",
            trainer2.getProfileId()                 // ✅ String trainerId
        );
        dataManager.addSession(session2);
        
        Session session3 = new Session(
            generateSessionId(),                    // ✅ String sessionId
            strengthClass.getClassId(),             // ✅ String classId
            "2026-07-22",
            "17:00",
            "18:00",
            "1 hour",
            trainer3.getProfileId()                 // ✅ String trainerId
        );
        dataManager.addSession(session3);
        
        Session session4 = new Session(
            generateSessionId(),                    // ✅ String sessionId
            yogaClass.getClassId(),                 // ✅ String classId
            "2026-07-22",
            "07:00",
            "08:00",
            "1 hour",
            trainer1.getProfileId()                 // ✅ String trainerId
        );
        dataManager.addSession(session4);
        System.out.println("✅ 4 Sessions created");
        
        // ============================================================
        // 7. CREATE BOOKINGS
        // ============================================================
        Booking booking1 = new Booking(
            generateBookingId(),                    // ✅ String bookingId
            john.getProfileId(),                    // ✅ String profileId
            yogaClass.getClassId(),                 // ✅ String classId
            session1.getSessionId(),                // ✅ String sessionId
            "2026-07-15",
            "Confirmed"
        );
        dataManager.addBooking(booking1);
        
        Booking booking2 = new Booking(
            generateBookingId(),                    // ✅ String bookingId
            sarah.getProfileId(),                   // ✅ String profileId
            spinClass.getClassId(),                 // ✅ String classId
            session2.getSessionId(),                // ✅ String sessionId
            "2026-07-16",
            "Confirmed"
        );
        dataManager.addBooking(booking2);
        
        Booking booking3 = new Booking(
            generateBookingId(),                    // ✅ String bookingId
            alice.getProfileId(),                   // ✅ String profileId
            yogaClass.getClassId(),                 // ✅ String classId
            session1.getSessionId(),                // ✅ String sessionId
            "2026-07-17",
            "Confirmed"
        );
        dataManager.addBooking(booking3);
        
        Booking booking4 = new Booking(
            generateBookingId(),                    // ✅ String bookingId
            charlie.getProfileId(),                 // ✅ String profileId
            yogaClass.getClassId(),                 // ✅ String classId
            session1.getSessionId(),                // ✅ String sessionId
            "2026-07-17",
            "Pending"
        );
        dataManager.addBooking(booking4);
        
        Booking booking5 = new Booking(
            generateBookingId(),                    // ✅ String bookingId
            johnson.getProfileId(),                 // ✅ String profileId
            strengthClass.getClassId(),             // ✅ String classId
            session3.getSessionId(),                // ✅ String sessionId
            "2026-07-18",
            "Confirmed"
        );
        dataManager.addBooking(booking5);
        System.out.println("✅ 5 Bookings created");
        
        // ============================================================
        // 8. CREATE ATTENDANCE RECORDS
        // ============================================================
        Attendance attendance1 = new Attendance(
            generateAttendanceId(),
            john.getProfileId(),
            session1.getSessionId(),
            LocalDateTime.of(2026, 7, 20, 0, 0),
            "Present"
        );
        dataManager.addAttendance(attendance1);

        Attendance attendance2 = new Attendance(
            generateAttendanceId(),
            sarah.getProfileId(),
            session2.getSessionId(),
            LocalDateTime.of(2026, 7, 21, 0, 0),
            "Present"
        );
        dataManager.addAttendance(attendance2);

        Attendance attendance3 = new Attendance(
            generateAttendanceId(),
            alice.getProfileId(),
            session1.getSessionId(),
            LocalDateTime.of(2026, 7, 20, 0, 0),
            "Late"
        );
        dataManager.addAttendance(attendance3);

        Attendance attendance4 = new Attendance(
            generateAttendanceId(),
            charlie.getProfileId(),
            session1.getSessionId(),
            LocalDateTime.of(2026, 7, 20, 0, 0),
            "Absent"
        );
        dataManager.addAttendance(attendance4);

        Attendance attendance5 = new Attendance(
            generateAttendanceId(),
            john.getProfileId(),
            session4.getSessionId(),
            LocalDateTime.of(2026, 7, 22, 0, 0),
            "Present"
        );
        dataManager.addAttendance(attendance5);
        dataManager.addAttendance(attendance5);
        System.out.println("✅ 5 Attendance records created");
        
        // ============================================================
        // 9. UPDATE CLASS BOOKINGS (for availability tracking)
        // ============================================================
        System.out.println("📌 Updating class bookings...");
        yogaClass.addBooking("John Doe");
        spinClass.addBooking("Sarah Smith");
        yogaClass.addBooking("Alice Wonder");
        yogaClass.addBooking("Charlie Brown");
        strengthClass.addBooking("The Johnson Family");
        
        // ============================================================
        // 10. SAVE ALL DATA
        // ============================================================
        dataManager.saveAllData();
        
        // ============================================================
        // 11. DISPLAY SUMMARY
        // ============================================================
        System.out.println("-".repeat(40));
        System.out.println("✅ Test data initialization complete!");
        System.out.println("   👤 Admins: 1 (admin@gym.com / admin123)");
        System.out.println("   👤 Test Users: 2 (test1@email.com, test2@email.com)");
        System.out.println("   👤 Regular Members: " + profiles.size());
        System.out.println("   👤 Trainers: " + trainers.size());
        System.out.println("   📚 Classes: 3");
        System.out.println("   📅 Sessions: 4");
        System.out.println("   📋 Bookings: 5");
        System.out.println("   ✅ Attendance: 5");
        System.out.println("-".repeat(40));
        System.out.println("\n🔑 Login Credentials:");
        System.out.println("   Admin:      admin@gym.com / admin123");
        System.out.println("   Test User 1: test1@email.com / password");
        System.out.println("   Test User 2: test2@email.com / password");
        System.out.println("   Member:     john@email.com / john");
        System.out.println("   Trainer:    sarah@gym.com / trainerPass");
        System.out.println("-".repeat(40));
    }
    
    // ============================================================
    // HELPER METHODS FOR ID GENERATION
    // ============================================================
    
    /**
     * Generate a unique profile ID
     * Format: [3-digit counter][MMDD][roleCode]
     * roleCode: 00=Admin, 11=Trainer, 22=Member
     */
    private static String generateProfileId(String roleCode) {
        String mmdd = LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd"));
        String counter = String.format("%03d", profileCounter++);
        return counter + mmdd + roleCode;
    }
    
    /**
     * Generate a unique membership ID
     * Format: [3-letter prefix][timestamp][4-digit counter]
     */
    private static String generateMembershipId(String prefix) {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return prefix + timestamp + String.format("%04d", membershipCounter++);
    }
    
    /**
     * Generate a unique class ID
     * Format: [3-letter prefix][MMDD][4-digit counter]
     */
    private static String generateClassId(String prefix) {
        String mmdd = LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd"));
        return prefix + mmdd + String.format("%04d", classCounter++);
    }
    
    /**
     * Generate a unique session ID
     */
    private static String generateSessionId() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "SES" + timestamp + String.format("%04d", sessionCounter++);
    }
    
    /**
     * Generate a unique booking ID
     */
    private static String generateBookingId() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "BKG" + timestamp + String.format("%04d", bookingCounter++);
    }
    
    /**
     * Generate a unique attendance ID
     */
    private static String generateAttendanceId() {
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ATT" + timestamp + String.format("%04d", attendanceCounter++);
    }
}