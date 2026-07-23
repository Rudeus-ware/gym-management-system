package com.gym;

import java.util.List;
import java.util.Scanner;

import com.gym.controller.GymController;
import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.classes.GymClass;
import com.gym.persistence.DataInitializer;
import com.gym.persistence.DataManager;

/**
 * Main Console Application Entry Point
 */
public class Main {
    
    private static GymController gymController;
    private static Scanner scanner;
    private static boolean running = true;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🏋️ GYM MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("Loading system...");
        
        gymController = new GymController();
        scanner = new Scanner(System.in);
        
        DataManager dataManager = gymController.getDataManager();
        if (dataManager.getProfiles().isEmpty()) {
            System.out.println("📌 No data found. Loading test data...");
            DataInitializer.initializeTestData(dataManager);
            System.out.println("✅ Test data loaded successfully!");
        }
        
        System.out.println("✅ System ready!");
        System.out.println("   Total Members: " + dataManager.getProfiles().size());
        System.out.println("   Total Classes: " + dataManager.getGymClasses().size());
        System.out.println("   Total Bookings: " + dataManager.getBookings().size());
        
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            handleMainMenuChoice(choice);
        }
        
        System.out.println("\n💾 Saving data...");
        gymController.saveAllData();
        System.out.println("👋 Goodbye!");
        scanner.close();
    }
    
    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📋 MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1.  👤 View All Members");
        System.out.println("2.  📚 View All Classes");
        System.out.println("3.  📋 View All Bookings");
        System.out.println("4.  📅 View All Sessions");
        System.out.println("5.  👤 Create New Member");
        System.out.println("6.  📚 Create New Class");
        System.out.println("7.  📋 Create New Booking");
        System.out.println("8.  ✅ Mark Attendance");
        System.out.println("9.  📊 View Statistics");
        System.out.println("10. 💾 Save Data");
        System.out.println("11. 🧹 Clear All Data (Caution!)");
        System.out.println("0.  🚪 Exit");
        System.out.println("=".repeat(50));
        System.out.print("Choose an option: ");
    }
    
    private static void handleMainMenuChoice(String choice) {
        switch (choice) {
            case "1": viewAllMembers(); break;
            case "2": viewAllClasses(); break;
            case "3": viewAllBookings(); break;
            case "4": viewAllSessions(); break;
            case "5": createNewMember(); break;
            case "6": createNewClass(); break;
            case "7": createNewBooking(); break;
            case "8": markAttendance(); break;
            case "9": viewStatistics(); break;
            case "10": saveData(); break;
            case "11": clearAllData(); break;
            case "0": running = false; break;
            default: System.out.println("❌ Invalid option. Please try again.");
        }
    }
    
    private static void viewAllMembers() {
        System.out.println("\n👤 ALL MEMBERS");
        System.out.println("-".repeat(40));
        
        List<Profile> members = gymController.getProfileController().getAllProfiles();
        if (members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }
        
        System.out.printf("%-5s | %-20s | %-25s | %-12s%n", "ID", "Name", "Email", "Phone");
        System.out.println("-".repeat(70));
        
        for (Profile p : members) {
            String membership = (p.getMembership() != null) ? p.getMembership().getClass().getSimpleName() : "None";
            System.out.printf("%-5d | %-20s | %-25s | %-12s | %s%n",
                p.getProfileId(),
                truncate(p.getName(), 20),
                truncate(p.getEmail(), 25),
                truncate(p.getPhone(), 12),
                membership);
        }
        System.out.println("-".repeat(70));
        System.out.println("Total: " + members.size() + " members");
    }
    
    private static void viewAllClasses() {
        System.out.println("\n📚 ALL CLASSES");
        System.out.println("-".repeat(40));
        
        List<GymClass> classes = gymController.getClassController().getAllClasses();
        if (classes.isEmpty()) {
            System.out.println("No classes found.");
            return;
        }
        
        System.out.printf("%-5s | %-20s | %-10s | %-15s | %-8s%n", "ID", "Name", "Type", "Trainer", "Bookings");
        System.out.println("-".repeat(70));
        
        for (GymClass c : classes) {
            System.out.printf("%-5d | %-20s | %-10s | %-15s | %-8s%n",
                c.getClassId(),
                truncate(c.getClassName(), 20),
                c.getClass().getSimpleName(),
                truncate(c.getTrainer(), 15),
                c.getCurrentBookings() + "/" + c.getCapacity());
        }
        System.out.println("-".repeat(70));
        System.out.println("Total: " + classes.size() + " classes");
    }
    
    private static void viewAllBookings() {
        System.out.println("\n📋 ALL BOOKINGS");
        System.out.println("-".repeat(40));
        
       List<Booking> bookings = gymController.getDataManager().getBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        
        System.out.printf("%-5s | %-8s | %-8s | %-12s | %-10s%n", "ID", "Member", "Class", "Date", "Status");
        System.out.println("-".repeat(60));
        
        for (Booking b : bookings) {
            System.out.printf("%-5d | %-8d | %-8d | %-12s | %-10s%n",
                b.getBookingId(),
                b.getProfileId(),
                b.getClassId(),
                b.getBookingDate(),
                truncate(b.getStatus(), 10));
        }
        System.out.println("-".repeat(60));
        System.out.println("Total: " + bookings.size() + " bookings");
    }
    
    private static void viewAllSessions() {
        System.out.println("\n📅 ALL SESSIONS");
        System.out.println("-".repeat(40));
        
        List<Session> sessions = gymController.getDataManager().getSessions();
        if (sessions.isEmpty()) {
            System.out.println("No sessions found.");
            return;
        }
        
        System.out.printf("%-5s | %-8s | %-12s | %-10s | %-10s%n", "ID", "Class", "Date", "Start", "Attendees");
        System.out.println("-".repeat(60));
        
        for (Session s : sessions) {
            System.out.printf("%-5d | %-8d | %-12s | %-10s | %-8d%n",
                s.getSessionId(),
                s.getClassId(),
                s.getSessionDate(),
                s.getStartTime(),
                s.getCurrentAttendees());
        }
        System.out.println("-".repeat(60));
        System.out.println("Total: " + sessions.size() + " sessions");
    }
    
    private static void createNewMember() {
        System.out.println("\n👤 CREATE NEW MEMBER");
        System.out.println("-".repeat(40));
        
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("❌ Email cannot be empty.");
            return;
        }
        
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();
        
        System.out.print("Enter address: ");
        String address = scanner.nextLine().trim();
        
        System.out.print("Enter membership type (Basic/Premium/Family, or press Enter for None): ");
        String membershipType = scanner.nextLine().trim();
        
        Profile profile = gymController.getAdminController().createMember(
            name, email, phone, address,
            membershipType.isEmpty() ? null : membershipType
        );
        
        if (profile != null) {
            System.out.println("✅ Member created successfully!");
            System.out.println("   ID: " + profile.getProfileId());
            System.out.println("   Name: " + profile.getName());
        } else {
            System.out.println("❌ Failed to create member. Email may already exist.");
        }
    }
    
    private static void createNewClass() {
        System.out.println("\n📚 CREATE NEW CLASS");
        System.out.println("-".repeat(40));
        
        System.out.print("Enter class name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Class name cannot be empty.");
            return;
        }
        
        System.out.print("Enter class type (Yoga/Spin/Strength): ");
        String type = scanner.nextLine().trim();
        if (!type.matches("(?i)Yoga|Spin|Strength")) {
            System.out.println("❌ Invalid type. Must be Yoga, Spin, or Strength.");
            return;
        }
        
        System.out.print("Enter schedule (e.g., Mon/Wed 7:00 AM): ");
        String schedule = scanner.nextLine().trim();
        
        System.out.print("Enter capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter trainer name: ");
        String trainer = scanner.nextLine().trim();
        
        // Additional fields for Yoga
        String style = "";
        String difficulty = "";
        if (type.equalsIgnoreCase("Yoga")) {
            System.out.print("Enter yoga style (Hatha/Vinyasa/Ashtanga): ");
            style = scanner.nextLine().trim();
            System.out.print("Enter difficulty (Beginner/Intermediate/Advanced): ");
            difficulty = scanner.nextLine().trim();
        }
        
        // Fix: Pass all required parameters
        GymClass gymClass = gymController.getAdminController().createClass(
            name, schedule, capacity, trainer, type, style, difficulty
        );
        
        if (gymClass != null) {
            System.out.println("✅ Class created successfully!");
            System.out.println("   ID: " + gymClass.getClassId());
            System.out.println("   Name: " + gymClass.getClassName());
        } else {
            System.out.println("❌ Failed to create class.");
        }
    }
    
    private static void createNewBooking() {
        System.out.println("\n📋 CREATE NEW BOOKING");
        System.out.println("-".repeat(40));
        
        List<Profile> members = gymController.getProfileController().getAllProfiles();
        if (members.isEmpty()) {
            System.out.println("❌ No members available. Please create a member first.");
            return;
        }
        
        System.out.println("Available Members:");
        for (Profile p : members) {
            System.out.println("  " + p.getProfileId() + ". " + p.getName());
        }
        System.out.print("Select member ID: ");
        int profileId = Integer.parseInt(scanner.nextLine().trim());
        Profile profile = gymController.getAdminController().getMember(profileId);
        
        if (profile == null) {
            System.out.println("❌ Member not found.");
            return;
        }
        
        List<GymClass> classes = gymController.getClassController().getAllClasses();
        if (classes.isEmpty()) {
            System.out.println("❌ No classes available. Please create a class first.");
            return;
        }
        
        System.out.println("Available Classes:");
        for (GymClass c : classes) {
            int available = c.getCapacity() - c.getCurrentBookings();
            System.out.println("  " + c.getClassId() + ". " + c.getClassName() + " (Available: " + available + ")");
        }
        System.out.print("Select class ID: ");
        int classId = Integer.parseInt(scanner.nextLine().trim());
        
        GymClass gymClass = gymController.getClassController().findClassById(classId);
        if (gymClass == null) {
            System.out.println("❌ Class not found.");
            return;
        }
        
        if (!gymClass.checkAvailability()) {
            System.out.println("❌ Class is full or unavailable.");
            return;
        }
        
        // Fix: createBooking returns Booking, not boolean
        Booking booking = gymController.getBookingController().createBooking(profileId, classId, 0);
        
        if (booking != null) {
            System.out.println("✅ Booking created successfully!");
            System.out.println("   Booking ID: " + booking.getBookingId());
        } else {
            System.out.println("❌ Failed to create booking.");
        }
    }
    
    private static void markAttendance() {
        System.out.println("\n✅ MARK ATTENDANCE");
        System.out.println("-".repeat(40));
        
        List<Session> sessions = gymController.getDataManager().getSessions();
        if (sessions.isEmpty()) {
            System.out.println("❌ No sessions available.");
            return;
        }
        
        System.out.println("Available Sessions:");
        for (Session s : sessions) {
            System.out.println("  " + s.getSessionId() + ". " + s.getSessionDate() + " at " + s.getStartTime() +
                " (Class: " + s.getClassId() + ")");
        }
        System.out.print("Select session ID: ");
        int sessionId = Integer.parseInt(scanner.nextLine().trim());
        
        List<Profile> members = gymController.getProfileController().getAllProfiles();
        if (members.isEmpty()) {
            System.out.println("❌ No members available.");
            return;
        }
        
        System.out.println("Available Members:");
        for (Profile p : members) {
            System.out.println("  " + p.getProfileId() + ". " + p.getName());
        }
        System.out.print("Select member ID: ");
        int profileId = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Enter status (Present/Absent/Late/Excused): ");
        String status = scanner.nextLine().trim();
        
        if (!status.matches("(?i)Present|Absent|Late|Excused")) {
            System.out.println("❌ Invalid status. Must be Present, Absent, Late, or Excused.");
            return;
        }
        
        // Fix: markAttendance returns Attendance, not boolean
        Attendance attendance = gymController.getAttendanceController().markAttendance(profileId, sessionId, status);
        
        if (attendance != null) {
            System.out.println("✅ Attendance marked successfully!");
            System.out.println("   Attendance ID: " + attendance.getAttendanceId());
        } else {
            System.out.println("❌ Failed to mark attendance.");
        }
    }
    
    private static void viewStatistics() {
        System.out.println("\n📊 SYSTEM STATISTICS");
        System.out.println("=".repeat(40));
        
        DataManager dm = gymController.getDataManager();
        System.out.println("👤 Members: " + dm.getProfiles().size());
        System.out.println("📚 Classes: " + dm.getGymClasses().size());
        System.out.println("📋 Bookings: " + dm.getBookings().size());
        System.out.println("📅 Sessions: " + dm.getSessions().size());
        System.out.println("✅ Attendance Records: " + dm.getAttendanceRecords().size());
        System.out.println("💪 Trainers: " + dm.getTrainers().size());
        System.out.println("=".repeat(40));
        
        List<Attendance> attendance = dm.getAttendanceRecords();
        if (!attendance.isEmpty()) {
            long present = attendance.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
            double rate = (double) present / attendance.size() * 100;
            System.out.printf("📊 Attendance Rate: %.1f%%\n", rate);
        }
        
        double revenue = 0;
        for (Profile p : dm.getProfiles()) {
            if (p.getMembership() != null && p.getMembership().isValid()) {
                revenue += p.getMembership().calculateFee();
            }
        }
        System.out.printf("💰 Total Revenue: $%.2f\n", revenue);
        System.out.println("=".repeat(40));
    }
    
    private static void saveData() {
        System.out.println("\n💾 Saving data...");
        gymController.saveAllData();
        System.out.println("✅ Data saved successfully!");
    }
    
    private static void clearAllData() {
        System.out.println("\n⚠️ WARNING: This will delete ALL data!");
        System.out.print("Type 'CONFIRM' to proceed: ");
        String confirm = scanner.nextLine().trim();
        
        if ("CONFIRM".equalsIgnoreCase(confirm)) {
            gymController.getDataManager().clearAllData();
            System.out.println("🧹 All data cleared!");
        } else {
            System.out.println("❌ Operation cancelled.");
        }
    }
    
    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}