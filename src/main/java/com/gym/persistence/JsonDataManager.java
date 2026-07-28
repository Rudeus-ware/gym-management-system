package com.gym.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;  // ← CORRECT: lowercase 'payment'

import com.gym.database.DatabaseManager;
import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.membership.Membership;
import com.gym.model.payment.Payment;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.util.IdGenerator;

/**
 * JsonDataManager - Manages data persistence using JSON files
 * Extends DatabaseManager to provide JSON-based storage
 */
public class JsonDataManager extends DatabaseManager {
    
    private List<Profile> profiles;
    private List<Membership> memberships;
    private List<Booking> bookings;
    private List<Session> sessions;
    private List<Attendance> attendanceRecords;
    private List<Trainer> trainers;
    private List<Admin> admins;
    private List<Payment> payments;  // ← Using correct Payment import
    private FileManager fileManager;
    private IdGenerator idGenerator;
    
    // ===== CONSTRUCTORS =====
    
    public JsonDataManager() {
        super();
        this.profiles = new ArrayList<>();
        this.memberships = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
        this.trainers = new ArrayList<>();
        this.admins = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.fileManager = new FileManager();
        
    }
    
    public JsonDataManager(FileManager fileManager) {
        this();
        this.fileManager = fileManager;
    }
    
    // ===== PROFILE METHODS =====
    
    public Profile createProfile(String name, String email, String phone, String address) {
    // ✅ Only create the ID you actually need
    String profileId = idGenerator.generateId("profile", IdGenerator.ROLE_MEMBER, LocalDate.now());
    Profile profile = new Profile(profileId, name, email, phone, address);
    profiles.add(profile);
    return profile;
}
    
    @Override
    public List<Profile> getProfiles() {
        return new ArrayList<>(profiles);
    }
    
    public Profile findProfileById(String profileId) {
        for (Profile profile : profiles) {
            if (profile.getProfileId().equals(profileId)) {
                return profile;
            }
        }
        return null;
    }
    
    public Profile findProfileByEmail(String email) {
        for (Profile profile : profiles) {
            if (profile.getEmail().equals(email)) {
                return profile;
            }
        }
        return null;
    }
    
   @Override
        public void updateProfile(Profile profile) {  // ← Change to void
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getProfileId().equals(profile.getProfileId())) {
                profiles.set(i, profile);
                return;  // ← Use return instead of return true/false
            }
        }
        System.out.println("⚠️ Profile not found: " + profile.getProfileId());
        }
    
    public boolean deleteProfile(String profileId) {
        return profiles.removeIf(p -> p.getProfileId().equals(profileId));
    }
    
    // ===== MEMBERSHIP METHODS =====
    
    public void addMembership(Membership membership) {
        memberships.add(membership);
    }
    
    @Override
    public List<Membership> getMemberships() {
        return new ArrayList<>(memberships);
    }
    
    public Membership findMembershipById(String membershipId) {
        for (Membership membership : memberships) {
            if (membership.getMembershipId().equals(membershipId)) {
                return membership;
            }
        }
        return null;
    }
    
    public boolean updateMembership(Membership membership) {
        for (int i = 0; i < memberships.size(); i++) {
            if (memberships.get(i).getMembershipId().equals(membership.getMembershipId())) {
                memberships.set(i, membership);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteMembership(String membershipId) {
        return memberships.removeIf(m -> m.getMembershipId().equals(membershipId));
    }
    
    // ===== BOOKING METHODS =====
    
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    @Override
    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }
    
    public Booking findBookingById(String bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId().equals(bookingId)) {
                return booking;
            }
        }
        return null;
    }
    
    public boolean updateBooking(Booking booking) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId().equals(booking.getBookingId())) {
                bookings.set(i, booking);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteBooking(String bookingId) {
        return bookings.removeIf(b -> b.getBookingId().equals(bookingId));
    }
    
    // ===== SESSION METHODS =====
    
    public void addSession(Session session) {
        sessions.add(session);
    }
    
    @Override
    public List<Session> getSessions() {
        return new ArrayList<>(sessions);
    }
    
    public Session findSessionById(String sessionId) {
        for (Session session : sessions) {
            if (session.getSessionId().equals(sessionId)) {
                return session;
            }
        }
        return null;
    }
    
    public boolean updateSession(Session session) {
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getSessionId().equals(session.getSessionId())) {
                sessions.set(i, session);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteSession(String sessionId) {
        return sessions.removeIf(s -> s.getSessionId().equals(sessionId));
    }
    
    // ===== ATTENDANCE METHODS =====
    
    public void addAttendance(Attendance attendance) {
        attendanceRecords.add(attendance);
    }
    
    @Override
    public List<Attendance> getAttendanceRecords() {
        return new ArrayList<>(attendanceRecords);
    }
    
    public Attendance findAttendanceById(String attendanceId) {
        for (Attendance attendance : attendanceRecords) {
            if (attendance.getAttendanceId().equals(attendanceId)) {
                return attendance;
            }
        }
        return null;
    }
    
    public boolean updateAttendance(Attendance attendance) {
        for (int i = 0; i < attendanceRecords.size(); i++) {
            if (attendanceRecords.get(i).getAttendanceId().equals(attendance.getAttendanceId())) {
                attendanceRecords.set(i, attendance);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteAttendance(String attendanceId) {
        return attendanceRecords.removeIf(a -> a.getAttendanceId().equals(attendanceId));
    }
    
    // ===== TRAINER METHODS =====
    
    public void addTrainer(Trainer trainer) {
        trainers.add(trainer);
    }
    
    @Override
    public List<Trainer> getTrainers() {
        return new ArrayList<>(trainers);
    }
    
    public Trainer findTrainerById(String trainerId) {
        for (Trainer trainer : trainers) {
            if (trainer.getTrainerId().equals(trainerId)) {
                return trainer;
            }
        }
        return null;
    }
    
    public Trainer findTrainerByEmail(String email) {
        for (Trainer trainer : trainers) {
            if (trainer.getEmail().equals(email)) {
                return trainer;
            }
        }
        return null;
    }
    
    public boolean updateTrainer(Trainer trainer) {
        for (int i = 0; i < trainers.size(); i++) {
            if (trainers.get(i).getTrainerId().equals(trainer.getTrainerId())) {
                trainers.set(i, trainer);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteTrainer(String trainerId) {
        return trainers.removeIf(t -> t.getTrainerId().equals(trainerId));
    }
    
    // ===== ADMIN METHODS =====
    
    public void addAdmin(Admin admin) {
        admins.add(admin);
    }
    
    @Override
    public List<Admin> getAdmins() {
        return new ArrayList<>(admins);
    }
    
    public Admin findAdminById(String adminId) {
        for (Admin admin : admins) {
            if (admin.getAdminId().equals(adminId)) {
                return admin;
            }
        }
        return null;
    }
    
    public Admin findAdminByEmail(String email) {
        for (Admin admin : admins) {
            if (admin.getEmail().equals(email)) {
                return admin;
            }
        }
        return null;
    }
    
    public boolean updateAdmin(Admin admin) {
        for (int i = 0; i < admins.size(); i++) {
            if (admins.get(i).getAdminId().equals(admin.getAdminId())) {
                admins.set(i, admin);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteAdmin(String adminId) {
        return admins.removeIf(a -> a.getAdminId().equals(adminId));
    }
    
    // ===== PAYMENT METHODS =====
    
    public void addPayment(Payment payment) {
        payments.add(payment);
    }
    
    /**
     * Get all payments
     * This overrides the method in DatabaseManager
     */
    @Override
    public List<Payment> getPayments() {  // ← This must match DatabaseManager exactly
        return new ArrayList<>(payments);
    }
    
    public Payment findPaymentById(String paymentId) {
        for (Payment payment : payments) {
            if (payment.getPaymentId().equals(paymentId)) {
                return payment;
            }
        }
        return null;
    }
    
    public boolean updatePayment(Payment payment) {
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getPaymentId().equals(payment.getPaymentId())) {
                payments.set(i, payment);
                return true;
            }
        }
        return false;
    }
    
    public boolean deletePayment(String paymentId) {
        return payments.removeIf(p -> p.getPaymentId().equals(paymentId));
    }
    
    // ===== DATA MANAGEMENT METHODS =====
    
    
    public void clearAllData() {
        profiles.clear();
        memberships.clear();
        bookings.clear();
        sessions.clear();
        attendanceRecords.clear();
        trainers.clear();
        admins.clear();
        payments.clear();
        System.out.println("✅ All JSON data cleared.");
    }
    
    public int getTotalRecords() {
        return profiles.size() + memberships.size() + bookings.size() + 
               sessions.size() + attendanceRecords.size() + trainers.size() + 
               admins.size() + payments.size();
    }
    
    public void printDataSummary() {
        System.out.println("\n=== JSON DATA SUMMARY ===");
        System.out.println("Profiles: " + profiles.size());
        System.out.println("Memberships: " + memberships.size());
        System.out.println("Bookings: " + bookings.size());
        System.out.println("Sessions: " + sessions.size());
        System.out.println("Attendance: " + attendanceRecords.size());
        System.out.println("Trainers: " + trainers.size());
        System.out.println("Admins: " + admins.size());
        System.out.println("Payments: " + payments.size());
        System.out.println("Total Records: " + getTotalRecords());
        System.out.println("=========================\n");
    }
}