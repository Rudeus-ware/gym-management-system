package com.gym;

import com.gym.model.Profile;
import com.gym.model.attendance.Attendance;
import com.gym.model.classes.Yoga;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.booking.Session;
import com.gym.model.booking.Booking;
import com.gym.controller.BookingController;
import com.gym.persistence.DataManager;
import com.gym.persistence.DataInitializer;
import java.util.Scanner;

public class Main {
    private static DataManager dataManager;
    private static BookingController bookingController;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        System.out.println("=" .repeat(60));
        System.out.println("🏋️ GYM MANAGEMENT SYSTEM");
        System.out.println("=" .repeat(60));
        
        // ===== INITIALIZE PERSISTENCE =====
        System.out.println("\n📂 Initializing Data Manager...");
        dataManager = new DataManager();
        
        // Check if data exists, if not, initialize test data
        if (dataManager.getProfiles().isEmpty()) {
            System.out.println("\n📌 No data found. Loading test data...");
            DataInitializer.initializeTestData(dataManager);
        }
        
        // ===== CREATE BOOKING SYSTEM =====
        bookingController = new BookingController(dataManager);
        
        // Load data from DataManager into BookingSystem
        loadDataIntoBookingSystem();
        
        // ===== MAIN MENU =====
        boolean running = true;
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    viewProfiles();
                    break;
                case "2":
                    viewClasses();
                    break;
                case "3":
                    viewBookings();
                    break;
                case "4":
                    viewSessions();
                    break;
                case "5":
                    createNewProfile();
                    break;
                case "6":
                    createNewBooking();
                    break;
                case "7":
                    markAttendance();
                    break;
                case "8":
                    dataManager.displayStatistics();
                    break;
                case "9":
                    System.out.println("\n💾 Saving all data...");
                    dataManager.saveAllData();
                    System.out.println("✅ Data saved successfully!");
                    break;
                case "0":
                    System.out.println("\n💾 Saving data before exit...");
                    dataManager.saveAllData();
                    System.out.println("👋 Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("❌ Invalid option. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    // ===== LOAD DATA INTO BOOKING SYSTEM =====
    private static void loadDataIntoBookingSystem() {
        System.out.println("\n🔄 Loading data into booking system...");
        
        // Add sessions
        for (Session session : dataManager.getSessions()) {
            bookingController.addSession(session);
        }
        
        System.out.println("✅ Data loaded into booking system!");
    }
    
    // ===== DISPLAY MAIN MENU =====
    private static void displayMainMenu() {
        System.out.println("\n📋 MAIN MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. View All Profiles");
        System.out.println("2. View All Classes");
        System.out.println("3. View All Bookings");
        System.out.println("4. View All Sessions");
        System.out.println("5. Create New Profile");
        System.out.println("6. Create New Booking");
        System.out.println("7. Mark Attendance");
        System.out.println("8. View Statistics");
        System.out.println("9. Save Data");
        System.out.println("0. Exit");
        System.out.println("=".repeat(40));
        System.out.print("Choose an option: ");
    }
    
    // ===== VIEW PROFILES =====
    private static void viewProfiles() {
        System.out.println("\n📋 ALL PROFILES");
        System.out.println("-".repeat(40));
        
        if (dataManager.getProfiles().isEmpty()) {
            System.out.println("No profiles found.");
            return;
        }
        
        for (Profile profile : dataManager.getProfiles()) {
            System.out.println(profile.viewProfile());
            System.out.println("-".repeat(30));
        }
    }
    
    // ===== VIEW CLASSES =====
    private static void viewClasses() {
        System.out.println("\n📋 ALL CLASSES");
        System.out.println("-".repeat(40));
        
        if (dataManager.getGymClasses().isEmpty()) {
            System.out.println("No classes found.");
            return;
        }
        
        for (com.gym.model.classes.GymClass gymClass : dataManager.getGymClasses()) {
            System.out.println(gymClass.getClassDetails());
            System.out.println("-".repeat(30));
        }
    }
    
    // ===== VIEW BOOKINGS =====
    private static void viewBookings() {
        System.out.println("\n📋 ALL BOOKINGS");
        System.out.println("-".repeat(40));
        
        if (dataManager.getBookings().isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        
        for (Booking booking : dataManager.getBookings()) {
            System.out.println(booking.getBookingDetails());
            System.out.println("-".repeat(30));
        }
    }
    
    // ===== VIEW SESSIONS =====
    private static void viewSessions() {
        System.out.println("\n📋 ALL SESSIONS");
        System.out.println("-".repeat(40));
        
        if (dataManager.getSessions().isEmpty()) {
            System.out.println("No sessions found.");
            return;
        }
        
        for (Session session : dataManager.getSessions()) {
            System.out.println(session.getSessionDetails());
            System.out.println("-".repeat(30));
        }
    }
    
    // ===== CREATE NEW PROFILE =====
    private static void createNewProfile() {
        System.out.println("\n📝 CREATE NEW PROFILE");
        System.out.println("-".repeat(40));
        
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        
        int nextId = dataManager.getProfiles().size() + 1;
        Profile profile = new Profile(nextId, name, email, phone, address);
        dataManager.addProfile(profile);
        
        System.out.println("✅ Profile created successfully!");
        System.out.println(profile.viewProfile());
    }
    
    // ===== CREATE NEW BOOKING =====
    private static void createNewBooking() {
        System.out.println("\n📝 CREATE NEW BOOKING");
        System.out.println("-".repeat(40));
        
        // Show available profiles
        System.out.println("Available Profiles:");
        for (Profile profile : dataManager.getProfiles()) {
            System.out.println("  " + profile.getProfileId() + ". " + profile.getName());
        }
        
        System.out.print("Enter Profile ID: ");
        int profileId = Integer.parseInt(scanner.nextLine());
        Profile profile = dataManager.findProfileById(profileId);
        
        if (profile == null) {
            System.out.println("❌ Profile not found!");
            return;
        }
        
        // Show available classes
        System.out.println("\nAvailable Classes:");
        for (com.gym.model.classes.GymClass gymClass : dataManager.getGymClasses()) {
            System.out.println("  " + gymClass.getClassId() + ". " + gymClass.getClassName() + 
                             " (Available: " + (gymClass.getCapacity() - gymClass.getCurrentBookings()) + ")");
        }
        
        System.out.print("Enter Class ID: ");
        int classId = Integer.parseInt(scanner.nextLine());
        com.gym.model.classes.GymClass gymClass = dataManager.findClassById(classId);
        
        if (gymClass == null) {
            System.out.println("❌ Class not found!");
            return;
        }
        
        // Show available sessions for this class
        System.out.println("\nAvailable Sessions:");
        for (Session session : dataManager.getSessions()) {
            if (session.getClassId() == classId && session.isAvailable()) {
                System.out.println("  " + session.getSessionId() + ". " + session.getSessionDate() + 
                                 " at " + session.getStartTime());
            }
        }
        
        System.out.print("Enter Session ID: ");
        int sessionId = Integer.parseInt(scanner.nextLine());
        Session session = null;
        for (Session s : dataManager.getSessions()) {
            if (s.getSessionId() == sessionId) {
                session = s;
                break;
            }
        }
        
        if (session == null) {
            System.out.println("❌ Session not found!");
            return;
        }
        
        // Check if profile has valid membership
        if (profile.getMembership() == null || !profile.getMembership().isValid()) {
            System.out.println("❌ Cannot book: " + profile.getName() + " has no valid membership!");
            return;
        }
        
        // Check if class has availability
        if (!gymClass.checkAvailability()) {
            System.out.println("❌ Cannot book: Class is full!");
            return;
        }
        
        // Create booking
        int bookingId = dataManager.getBookings().size() + 1;
        String bookingDate = java.time.LocalDate.now().toString();
        Booking booking = new Booking(bookingId, profileId, classId, sessionId, bookingDate, "Confirmed");
        
        dataManager.addBooking(booking);
        gymClass.addBooking(profile.getName());
        session.addAttendee();
        
        System.out.println("✅ Booking created successfully!");
        System.out.println(booking.getBookingDetails());
    }
    
    // ===== MARK ATTENDANCE =====
    private static void markAttendance() {
        System.out.println("\n📝 MARK ATTENDANCE");
        System.out.println("-".repeat(40));
        
        // Show available sessions
        System.out.println("Available Sessions:");
        for (Session session : dataManager.getSessions()) {
            System.out.println("  " + session.getSessionId() + ". " + session.getSessionDate() + 
                             " at " + session.getStartTime());
        }
        
        System.out.print("Enter Session ID: ");
        int sessionId = Integer.parseInt(scanner.nextLine());
        
        // Show profiles with bookings for this session
        System.out.println("\nMembers with bookings for this session:");
        for (Booking booking : dataManager.getBookings()) {
            if (booking.getSessionId() == sessionId) {
                Profile profile = dataManager.findProfileById(booking.getProfileId());
                if (profile != null) {
                    System.out.println("  " + profile.getProfileId() + ". " + profile.getName() + 
                                     " (Booking: " + booking.getBookingId() + ")");
                }
            }
        }
        
        System.out.print("Enter Profile ID: ");
        int profileId = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Enter status (Present/Absent/Late/Excused): ");
        String status = scanner.nextLine();
        
        // Create attendance record
        int attendanceId = dataManager.getAttendanceRecords().size() + 1;
        String attendanceDate = java.time.LocalDate.now().toString();
        Attendance attendance = 
            new Attendance(attendanceId, profileId, sessionId, attendanceDate, status);
        
        dataManager.addAttendance(attendance);
        
        // Mark based on status
        switch (status.toLowerCase()) {
            case "present":
                attendance.markPresent();
                break;
            case "absent":
                attendance.markAbsent();
                break;
            case "late":
                attendance.markLate();
                break;
            case "excused":
                attendance.markExcused();
                break;
            default:
                System.out.println("⚠️ Unknown status: " + status);
        }
        
        System.out.println("✅ Attendance recorded!");
    }
}