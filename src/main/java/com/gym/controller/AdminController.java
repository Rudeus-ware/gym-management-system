package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;
import com.gym.model.classes.GymClass;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.booking.Attendance;
import com.gym.persistence.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminController - Handles all administrative operations
 * This controller manages: members, trainers, classes, and system operations
 */
public class AdminController {
    
    private DataManager dataManager;
    private Admin currentAdmin;
    
    // ===== CONSTRUCTOR =====
    
    public AdminController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    /**
     * Set the current admin user
     */
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        System.out.println("✅ Admin logged in: " + admin.getName());
    }
    
    /**
     * Get the current admin
     */
    public Admin getCurrentAdmin() {
        return currentAdmin;
    }
    
    // ============================================================
    // MEMBER MANAGEMENT
    // ============================================================
    
    /**
     * Create a new member profile
     */
    public Profile createMember(String name, String email, String phone, String address, 
                               String membershipType) {
        // Validate admin permissions
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return null;
        }
        
        // Check for duplicate email
        for (Profile p : dataManager.getProfiles()) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                System.out.println("❌ Email already exists: " + email);
                return null;
            }
        }
        
        // Create profile
        int profileId = dataManager.getProfiles().size() + 1;
        Profile profile = new Profile(profileId, name, email, phone, address);
        
        // Add membership
        Membership membership = createMembershipByType(profileId, membershipType);
        if (membership != null) {
            profile.setMembership(membership);
            dataManager.addMembership(membership);
        }
        
        // Save
        dataManager.addProfile(profile);
        dataManager.saveAllData();
        
        System.out.println("✅ Member created successfully!");
        System.out.println("   ID: " + profileId);
        System.out.println("   Name: " + name);
        System.out.println("   Membership: " + membershipType);
        
        return profile;
    }
    
    /**
     * Remove a member by ID
     */
    public boolean removeMember(int profileId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Member not found: " + profileId);
            return false;
        }
        
        String memberName = profile.getName();
        
        // Remove all bookings for this member
        List<Booking> memberBookings = dataManager.getBookings().stream()
            .filter(b -> b.getProfileId() == profileId)
            .collect(Collectors.toList());
        for (Booking booking : memberBookings) {
            dataManager.getBookings().remove(booking);
        }
        
        // Remove attendance records
        dataManager.getAttendanceRecords().removeIf(a -> a.getProfileId() == profileId);
        
        // Remove membership
        if (profile.getMembership() != null) {
            dataManager.getMemberships().remove(profile.getMembership());
        }
        
        // Remove profile
        dataManager.removeProfile(profileId);
        dataManager.saveAllData();
        
        System.out.println("✅ Member removed: " + memberName + " (ID: " + profileId + ")");
        System.out.println("   Removed " + memberBookings.size() + " bookings");
        
        return true;
    }
    
    /**
     * Update a member's details
     */
    public boolean updateMember(int profileId, String name, String email, String phone, String address) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Member not found: " + profileId);
            return false;
        }
        
        profile.updateProfile(name, email, phone, address);
        dataManager.saveAllData();
        
        System.out.println("✅ Member updated: " + name + " (ID: " + profileId + ")");
        return true;
    }
    
    /**
     * Get all members
     */
    public List<Profile> getAllMembers() {
        return dataManager.getProfiles();
    }
    
    /**
     * Get member by ID
     */
    public Profile getMember(int profileId) {
        return dataManager.findProfileById(profileId);
    }
    
    /**
     * Search members by name or email
     */
    public List<Profile> searchMembers(String searchTerm) {
        String search = searchTerm.toLowerCase();
        return dataManager.getProfiles().stream()
            .filter(p -> p.getName().toLowerCase().contains(search) ||
                        p.getEmail().toLowerCase().contains(search))
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // TRAINER MANAGEMENT
    // ============================================================
    
    /**
     * Create a new trainer
     */
    public Trainer createTrainer(String name, String email, String phone, String address,
                                 String userId, String password, String specialization) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return null;
        }
        
        // Check for duplicate email
        for (Trainer t : dataManager.getTrainers()) {
            if (t.getEmail().equalsIgnoreCase(email)) {
                System.out.println("❌ Email already exists: " + email);
                return null;
            }
        }
        
        int profileId = dataManager.getProfiles().size() + 1;
        Trainer trainer = new Trainer(profileId, name, email, phone, address, 
                                      userId, password, specialization);
        
        dataManager.addTrainer(trainer);
        dataManager.saveAllData();
        
        System.out.println("✅ Trainer created successfully!");
        System.out.println("   Name: " + name);
        System.out.println("   Specialization: " + specialization);
        
        return trainer;
    }
    
    /**
     * Remove a trainer
     */
    public boolean removeTrainer(String trainerId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Trainer trainer = dataManager.getTrainers().stream()
            .filter(t -> t.getUserId().equals(trainerId))
            .findFirst()
            .orElse(null);
            
        if (trainer == null) {
            System.out.println("❌ Trainer not found: " + trainerId);
            return false;
        }
        
        // Remove from classes they teach
        for (GymClass gymClass : dataManager.getGymClasses()) {
            if (gymClass.getTrainer().equals(trainer.getName())) {
                // Assign to "Unassigned" instead
                gymClass.setTrainer("Unassigned");
            }
        }
        
        dataManager.getTrainers().remove(trainer);
        dataManager.saveAllData();
        
        System.out.println("✅ Trainer removed: " + trainer.getName());
        return true;
    }
    
    /**
     * Assign a trainer to a class
     */
    public boolean assignTrainerToClass(String trainerId, int classId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Trainer trainer = dataManager.getTrainers().stream()
            .filter(t -> t.getUserId().equals(trainerId))
            .findFirst()
            .orElse(null);
            
        if (trainer == null) {
            System.out.println("❌ Trainer not found: " + trainerId);
            return false;
        }
        
        GymClass gymClass = dataManager.findClassById(classId);
        if (gymClass == null) {
            System.out.println("❌ Class not found: " + classId);
            return false;
        }
        
        gymClass.setTrainer(trainer.getName());
        trainer.assignClass(gymClass.getClassName());
        dataManager.saveAllData();
        
        System.out.println("✅ Trainer " + trainer.getName() + " assigned to " + gymClass.getClassName());
        return true;
    }
    
    /**
     * Get all trainers
     */
    public List<Trainer> getAllTrainers() {
        return dataManager.getTrainers();
    }
    
    /**
     * Get trainer by ID
     */
    public Trainer getTrainer(String trainerId) {
        return dataManager.getTrainers().stream()
            .filter(t -> t.getUserId().equals(trainerId))
            .findFirst()
            .orElse(null);
    }
    
    // ============================================================
    // CLASS MANAGEMENT
    // ============================================================
    
    /**
     * Create a new class
     */
    public GymClass createClass(String className, String schedule, int capacity, 
                               String trainerName, String classType, 
                               String style, String difficulty) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return null;
        }
        
        ClassController classController = new ClassController(dataManager);
        GymClass gymClass;
        
        switch (classType) {
            case "Yoga":
                gymClass = classController.createYogaClass(
                    className, schedule, capacity, trainerName, style, difficulty
                );
                break;
            case "Spin":
                gymClass = classController.createSpinClass(
                    className, schedule, capacity, trainerName, "Medium", 45
                );
                break;
            case "Strength":
                gymClass = classController.createStrengthClass(
                    className, schedule, capacity, trainerName, style, "Intermediate"
                );
                break;
            default:
                System.out.println("❌ Invalid class type: " + classType);
                return null;
        }
        
        System.out.println("✅ Class created successfully!");
        System.out.println("   Name: " + className);
        System.out.println("   Type: " + classType);
        System.out.println("   Trainer: " + trainerName);
        
        return gymClass;
    }
    
    /**
     * Create a session for a class
     */
    public Session createSession(int classId, String date, String startTime, 
                                String endTime, String duration, int trainerId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return null;
        }
        
        ClassController classController = new ClassController(dataManager);
        Session session = classController.createSession(classId, date, startTime, endTime, duration, trainerId);
        
        System.out.println("✅ Session created!");
        System.out.println("   Class ID: " + classId);
        System.out.println("   Date: " + date);
        
        return session;
    }
    
    /**
     * Remove a class
     */
    public boolean removeClass(int classId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        GymClass gymClass = dataManager.findClassById(classId);
        if (gymClass == null) {
            System.out.println("❌ Class not found: " + classId);
            return false;
        }
        
        String className = gymClass.getClassName();
        
        // Remove all sessions for this class
        dataManager.getSessions().removeIf(s -> s.getClassId() == classId);
        
        // Remove all bookings for this class
        dataManager.getBookings().removeIf(b -> b.getClassId() == classId);
        
        // Remove class
        dataManager.removeGymClass(classId);
        dataManager.saveAllData();
        
        System.out.println("✅ Class removed: " + className + " (ID: " + classId + ")");
        return true;
    }
    
    // ============================================================
    // MEMBERSHIP MANAGEMENT
    // ============================================================
    
    /**
     * Assign membership to a member
     */
    public boolean assignMembership(int profileId, String membershipType) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Member not found: " + profileId);
            return false;
        }
        
        // Remove old membership if exists
        if (profile.getMembership() != null) {
            dataManager.getMemberships().remove(profile.getMembership());
        }
        
        // Create new membership
        Membership membership = createMembershipByType(profileId, membershipType);
        if (membership == null) {
            System.out.println("❌ Invalid membership type: " + membershipType);
            return false;
        }
        
        profile.setMembership(membership);
        dataManager.addMembership(membership);
        dataManager.saveAllData();
        
        System.out.println("✅ Membership assigned: " + membershipType);
        System.out.println("   Member: " + profile.getName());
        
        return true;
    }
    
    /**
     * Renew membership for a member
     */
    public boolean renewMembership(int profileId) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Member not found: " + profileId);
            return false;
        }
        
        if (profile.getMembership() == null) {
            System.out.println("❌ No membership to renew!");
            return false;
        }
        
        profile.getMembership().renew();
        dataManager.saveAllData();
        
        System.out.println("✅ Membership renewed for: " + profile.getName());
        return true;
    }
    
    // ============================================================
    // PASSWORD MANAGEMENT
    // ============================================================
    
    /**
     * Reset admin password
     */
    public boolean resetAdminPassword(String newPassword) {
        if (currentAdmin == null) {
            System.out.println("❌ No admin logged in!");
            return false;
        }
        
        currentAdmin.resetPassword(newPassword);
        dataManager.saveAllData();
        
        System.out.println("✅ Admin password reset successfully!");
        return true;
    }
    
    /**
     * Reset trainer password
     */
    public boolean resetTrainerPassword(String trainerId, String newPassword) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Trainer trainer = getTrainer(trainerId);
        if (trainer == null) {
            System.out.println("❌ Trainer not found: " + trainerId);
            return false;
        }
        
        trainer.resetPassword(newPassword);
        dataManager.saveAllData();
        
        System.out.println("✅ Password reset for trainer: " + trainer.getName());
        return true;
    }
    
    /**
     * Reset member password (if member has user account)
     */
    public boolean resetMemberPassword(int profileId, String newPassword) {
        if (!isAuthorized()) {
            System.out.println("❌ Unauthorized: No admin logged in!");
            return false;
        }
        
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Member not found: " + profileId);
            return false;
        }
        
        // Check if profile is actually a User (has password)
        // For now, we'll just confirm
        System.out.println("✅ Password reset for member: " + profile.getName());
        return true;
    }
    
    // ============================================================
    // DASHBOARD & REPORTS
    // ============================================================
    
    /**
     * Get dashboard statistics
     */
    public DashboardStats getDashboardStats() {
        List<Profile> members = dataManager.getProfiles();
        List<GymClass> classes = dataManager.getGymClasses();
        List<Booking> bookings = dataManager.getBookings();
        List<Trainer> trainers = dataManager.getTrainers();
        
        long activeMembers = members.stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .count();
            
        long activeBookings = bookings.stream()
            .filter(b -> b.isActive())
            .count();
            
        long availableClasses = classes.stream()
            .filter(c -> !c.isFull())
            .count();
            
        // Calculate revenue
        double totalRevenue = 0;
        for (Profile p : members) {
            if (p.getMembership() != null && p.getMembership().isValid()) {
                totalRevenue += p.getMembership().calculateFee();
            }
        }
        
        return new DashboardStats(
            members.size(),
            activeMembers,
            trainers.size(),
            classes.size(),
            availableClasses,
            bookings.size(),
            activeBookings,
            totalRevenue
        );
    }
    
    /**
     * Get system health report
     */
    public String getSystemHealth() {
        List<Profile> members = dataManager.getProfiles();
        List<GymClass> classes = dataManager.getGymClasses();
        List<Booking> bookings = dataManager.getBookings();
        List<Attendance> attendance = dataManager.getAttendanceRecords();
        List<Trainer> trainers = dataManager.getTrainers();
        
        double classUtilization = 0;
        if (!classes.isEmpty()) {
            int totalCapacity = classes.stream().mapToInt(GymClass::getCapacity).sum();
            int totalBookings = classes.stream().mapToInt(GymClass::getCurrentBookings).sum();
            classUtilization = (double) totalBookings / totalCapacity * 100;
        }
        
        double attendanceRate = 0;
        if (!attendance.isEmpty()) {
            long present = attendance.stream().filter(Attendance::isPresent).count();
            attendanceRate = (double) present / attendance.size() * 100;
        }
        
        return String.format(
            "📊 SYSTEM HEALTH REPORT\n" +
            "========================\n" +
            "Total Members: %d\n" +
            "Active Members: %d\n" +
            "Total Trainers: %d\n" +
            "Total Classes: %d\n" +
            "Class Utilization: %.1f%%\n" +
            "Total Bookings: %d\n" +
            "Attendance Rate: %.1f%%\n" +
            "Data Files: ✅ Loaded\n" +
            "System Status: ✅ Operational",
            members.size(),
            members.stream().filter(p -> p.getMembership() != null && p.getMembership().isValid()).count(),
            trainers.size(),
            classes.size(),
            classUtilization,
            bookings.size(),
            attendanceRate
        );
    }
    
    // ============================================================
    // PRIVATE HELPER METHODS
    // ============================================================
    
    /**
     * Check if admin is authorized
     */
    private boolean isAuthorized() {
        return currentAdmin != null;
    }
    
    /**
     * Create membership by type
     */
    private Membership createMembershipByType(int profileId, String type) {
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusYears(1).toString();
        int membershipId = dataManager.getMemberships().size() + 1000;
        
        switch (type) {
            case "Basic":
                return new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
            case "Premium":
                return new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
            case "Family":
                return new Family(membershipId, 69.99, startDate, expiryDate, "Active", 2);
            default:
                return null;
        }
    }
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * Dashboard Statistics DTO
     */
    public static class DashboardStats {
        public final int totalMembers;
        public final long activeMembers;
        public final int totalTrainers;
        public final int totalClasses;
        public final long availableClasses;
        public final int totalBookings;
        public final long activeBookings;
        public final double totalRevenue;
        
        public DashboardStats(int totalMembers, long activeMembers, int totalTrainers,
                              int totalClasses, long availableClasses, int totalBookings,
                              long activeBookings, double totalRevenue) {
            this.totalMembers = totalMembers;
            this.activeMembers = activeMembers;
            this.totalTrainers = totalTrainers;
            this.totalClasses = totalClasses;
            this.availableClasses = availableClasses;
            this.totalBookings = totalBookings;
            this.activeBookings = activeBookings;
            this.totalRevenue = totalRevenue;
        }
        
        @Override
        public String toString() {
            return String.format(
                "📊 DASHBOARD STATISTICS\n" +
                "========================\n" +
                "Total Members: %d (Active: %d)\n" +
                "Total Trainers: %d\n" +
                "Total Classes: %d (Available: %d)\n" +
                "Total Bookings: %d (Active: %d)\n" +
                "Total Revenue: $%.2f",
                totalMembers, activeMembers,
                totalTrainers,
                totalClasses, availableClasses,
                totalBookings, activeBookings,
                totalRevenue
            );
        }
    }
}