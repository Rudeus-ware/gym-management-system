package com.gym.persistence;

import com.gym.database.DatabaseManager;
import com.gym.model.Profile;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.model.classes.Yoga;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.booking.Attendance;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DataInitializer - Initializes test data with auto-generated IDs
 */
public class DataInitializer {
    
    public static void initializeTestData(DatabaseManager dataManager) {
        System.out.println("\n📋 Initializing test data...");
        System.out.println("-".repeat(40));
        
        IdGenerator idGen = new IdGenerator(null);
        
        // ============================================================
        // 1. CREATE ADMIN
        // ============================================================
        String adminId = idGen.generateProfileId(IdGenerator.ROLE_ADMIN, LocalDate.now());
        Profile adminProfile = new Profile(adminId, "System Admin", "admin@gym.com", 
                                          "555-000-0000", "Admin Office");
        adminProfile.setActive(true);
        dataManager.createProfile(adminProfile);
        System.out.println("✅ Admin created: admin@gym.com / admin123 (ID: " + adminId + ")");
        
        // ============================================================
        // 2. CREATE TRAINER
        // ============================================================
        String trainerId = idGen.generateProfileId(IdGenerator.ROLE_TRAINER, LocalDate.now());
        Profile trainerProfile = new Profile(trainerId, "Sarah Johnson", "sarah@gym.com", 
                                            "555-111-2222", "789 Yoga Lane");
        trainerProfile.setActive(true);
        dataManager.createProfile(trainerProfile);
        System.out.println("✅ Trainer created: sarah@gym.com (ID: " + trainerId + ")");
        
        // ============================================================
        // 3. CREATE MEMBERS
        // ============================================================
        String memberId1 = idGen.generateProfileId(IdGenerator.ROLE_MEMBER, LocalDate.now());
        Profile john = new Profile(memberId1, "John Doe", "john@email.com", 
                                   "555-123-4567", "123 Main St");
        john.setActive(true);
        dataManager.createProfile(john);
        System.out.println("✅ Member created: john@email.com (ID: " + memberId1 + ")");
        
        String memberId2 = idGen.generateProfileId(IdGenerator.ROLE_MEMBER, LocalDate.now());
        Profile sarah = new Profile(memberId2, "Sarah Smith", "sarah@email.com", 
                                    "555-987-6543", "456 Oak Ave");
        sarah.setActive(true);
        dataManager.createProfile(sarah);
        System.out.println("✅ Member created: sarah@email.com (ID: " + memberId2 + ")");
        
        // ============================================================
        // 4. CREATE MEMBERSHIPS
        // ============================================================
        String membershipId1 = idGen.generateMembershipId(LocalDate.now());
        Basic basicPlan = new Basic(membershipId1, 49.99, 
                                    LocalDate.now().toString(), 
                                    LocalDate.now().plusYears(1).toString(), 
                                    "Active");
        john.setMembership(basicPlan);
        dataManager.addMembership(basicPlan);
        
        String membershipId2 = idGen.generateMembershipId(LocalDate.now());
        Premium premiumPlan = new Premium(membershipId2, 99.99, 
                                          LocalDate.now().toString(), 
                                          LocalDate.now().plusYears(1).toString(), 
                                          "Active", "VIP Access");
        sarah.setMembership(premiumPlan);
        dataManager.addMembership(premiumPlan);
        
        System.out.println("✅ Memberships created");
        
        // ============================================================
        // 5. CREATE CLASSES
        // ============================================================
        String yogaId = idGen.generateClassId(LocalDate.now());
        Yoga yogaClass = new Yoga(yogaId, "Morning Yoga", "Mon/Wed/Fri 7:00 AM", 
                                  15, "Sarah Johnson", "Hatha", "Beginner");
        dataManager.addGymClass(yogaClass);
        
        String spinId = idGen.generateClassId(LocalDate.now());
        Spin spinClass = new Spin(spinId, "Evening Spin", "Tue/Thu 6:00 PM", 
                                  20, "Mike Trainer", "Medium", 45, "EDM");
        dataManager.addGymClass(spinClass);
        
        System.out.println("✅ Classes created");
        
        // ============================================================
        // 6. CREATE SESSIONS
        // ============================================================
        String sessionId1 = idGen.generateSessionId(LocalDate.now());
        Session session1 = new Session(sessionId1, yogaId, 
                                       LocalDate.now().toString(), 
                                       "07:00", "08:00", "1 hour", trainerId);
        dataManager.addSession(session1);
        
        String sessionId2 = idGen.generateSessionId(LocalDate.now());
        Session session2 = new Session(sessionId2, spinId, 
                                       LocalDate.now().toString(), 
                                       "18:00", "18:45", "45 minutes", trainerId);
        dataManager.addSession(session2);
        
        System.out.println("✅ Sessions created");
        
        // ============================================================
        // 7. CREATE BOOKINGS
        // ============================================================
        String bookingId1 = idGen.generateBookingId(LocalDate.now());
        Booking booking1 = new Booking(bookingId1, memberId1, yogaId, sessionId1, 
                                       LocalDate.now().toString(), "Confirmed");
        dataManager.addBooking(booking1);
        yogaClass.addBooking("John Doe");
        
        String bookingId2 = idGen.generateBookingId(LocalDate.now());
        Booking booking2 = new Booking(bookingId2, memberId2, spinId, sessionId2, 
                                       LocalDate.now().toString(), "Confirmed");
        dataManager.addBooking(booking2);
        spinClass.addBooking("Sarah Smith");
        
        System.out.println("✅ Bookings created");
        
        // ============================================================
        // 8. CREATE ATTENDANCE
        // ============================================================
        String attendanceId1 = idGen.generateAttendanceId(LocalDate.now());
        Attendance attendance1 = new Attendance(attendanceId1, memberId1, sessionId1, 
                                                LocalDate.now().toString(), "Present");
        dataManager.addAttendance(attendance1);
        
        String attendanceId2 = idGen.generateAttendanceId(LocalDate.now());
        Attendance attendance2 = new Attendance(attendanceId2, memberId2, sessionId2, 
                                                LocalDate.now().toString(), "Present");
        dataManager.addAttendance(attendance2);
        
        System.out.println("✅ Attendance records created");
        
        // ============================================================
        // 9. SUMMARY
        // ============================================================
        dataManager.saveAllData();
        
        System.out.println("-".repeat(40));
        System.out.println("✅ Test data initialization complete!");
        System.out.println("   👤 Admins: 1 (admin@gym.com / admin123)");
        System.out.println("   👤 Trainers: 1 (sarah@gym.com)");
        System.out.println("   👤 Members: 2");
        System.out.println("   📚 Classes: 2");
        System.out.println("   📅 Sessions: 2");
        System.out.println("   📋 Bookings: 2");
        System.out.println("   ✅ Attendance: 2");
        System.out.println("-".repeat(40));
        System.out.println("\n🔑 Login Credentials:");
        System.out.println("   Admin:   admin@gym.com / admin123");
        System.out.println("   Member:  john@email.com / john");
        System.out.println("   Trainer: sarah@gym.com / trainerPass");
        System.out.println("-".repeat(40));
    }
}