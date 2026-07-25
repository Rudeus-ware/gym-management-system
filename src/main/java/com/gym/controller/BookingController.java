package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.booking.Attendance;
import com.gym.database.DatabaseManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Booking operations
 * Handles all booking-related business logic
 */
public class BookingController {
    
    private DatabaseManager dataManager;
    
    public BookingController(JsonDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void addSession(Session session) {
        boolean exists = databaseManager.getSessions().stream()
            .anyMatch(existing -> existing.getSessionId().equals(session.getSessionId()));
        
        if (!exists) {
            databaseManager.addSession(session);
        }
    }
    
    // ===== CREATE BOOKING =====
    
    /**
     * Create a new booking
     */
    public Booking createBooking(String profileId, String classId, String sessionId) {
        // Check if profile exists
        Profile profile = databaseManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found!");
            return null;
        }
        
        // Check if class exists
        GymClass gymClass = databaseManager.findClassById(classId);
        if (gymClass == null) {
            System.out.println("❌ Class not found!");
            return null;
        }
        
        // Check if session exists
        Session session = null;
        for (Session s : databaseManager.getSessions()) {
            if (s.getSessionId().equals(sessionId)) {
                session = s;
                break;
            }
        }
        if (session == null) {
            System.out.println("❌ Session not found!");
            return null;
        }
        
        // Check if profile has valid membership
        if (profile.getMembership() == null || !profile.getMembership().isValid()) {
            System.out.println("❌ No valid membership!");
            return null;
        }
        
        // Check if class has availability
        if (!gymClass.checkAvailability()) {
            System.out.println("❌ Class is full!");
            return null;
        }
        
        // Check if already booked
        boolean alreadyBooked = databaseManager.getBookings().stream()
            .anyMatch(b -> b.getProfileId().equals(profileId) && b.getSessionId().equals(sessionId));
        if (alreadyBooked) {
            System.out.println("❌ Already booked for this session!");
            return null;
        }
        
        // Create booking
        int bookingId = databaseManager.getBookings().size() + 1;
        String bookingDate = LocalDate.now().toString();
        Booking booking = new Booking(bookingId, profileId, classId, sessionId, bookingDate, "Confirmed");
        
        databaseManager.addBooking(booking);
        gymClass.addBooking(profile.getName());
        session.addAttendee();
        databaseManager.saveAllData();
        
        System.out.println("✅ Booking created successfully!");
        return booking;
    }
    
    // ===== CANCEL BOOKING =====
    
    /**
     * Cancel a booking
     */
    public boolean cancelBooking(String bookingId) {
        Booking booking = databaseManager.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("❌ Booking not found!");
            return false;
        }
        
        // Remove from class
        GymClass gymClass = databaseManager.findClassById(booking.getClassId());
        Profile profile = databaseManager.findProfileById(booking.getProfileId());
        
        if (gymClass != null && profile != null) {
            gymClass.removeBooking(profile.getName());
        }
        
        // Remove from session
        for (Session session : databaseManager.getSessions()) {
            if (session.getSessionId().equals(booking.getSessionId())) {
                session.removeAttendee();
                break;
            }
        }
        
        // Remove booking
        databaseManager.getBookings().remove(booking);
        databaseManager.saveAllData();
        
        System.out.println("✅ Booking cancelled successfully!");
        return true;
    }
    
    /**
     * Cancel all bookings for a profile
     */
    public int cancelAllBookingsForProfile(String profileId) {
        List<Booking> toRemove = databaseManager.getBookings().stream()
            .filter(b -> b.getProfileId().equals(profileId))
            .collect(Collectors.toList());
            
        for (Booking booking : toRemove) {
            cancelBooking(booking.getBookingId());
        }
        
        return toRemove.size();
    }
    
    // ===== UPDATE BOOKING =====
    
    /**
     * Change booking status
     */
    public boolean changeBookingStatus(String bookingId, String newStatus) {
        Booking booking = databaseManager.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("❌ Booking not found!");
            return false;
        }
        
        booking.changeStatus(newStatus);
        databaseManager.saveAllData();
        return true;
    }
    
    /**
     * Confirm a booking
     */
    public boolean confirmBooking(String bookingId) {
        Booking booking = databaseManager.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("❌ Booking not found!");
            return false;
        }
        
        booking.confirmBooking();
        databaseManager.saveAllData();
        return true;
    }
    
    // ===== QUERIES =====
    
    /**
     * Get all bookings for a profile
     */
    public List<Booking> getBookingsForProfile(String profileId) {
        return databaseManager.getBookings().stream()
            .filter(b -> b.getProfileId().equals(profileId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all bookings for a class
     */
    public List<Booking> getBookingsForClass(String classId) {
        return databaseManager.getBookings().stream()
            .filter(b -> b.getClassId().equals(classId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get all bookings for a session
     */
    public List<Booking> getBookingsForSession(String sessionId) {
        return databaseManager.getBookings().stream()
            .filter(b -> b.getSessionId().equals(sessionId))
            .collect(Collectors.toList());
    }
    
    /**
     * Get active bookings for a profile
     */
    public List<Booking> getActiveBookingsForProfile(String profileId) {
        return databaseManager.getBookings().stream()
            .filter(b -> b.getProfileId().equals(profileId) && b.isActive())
            .collect(Collectors.toList());
    }
    
    /**
     * Get confirmed bookings
     */
    public List<Booking> getConfirmedBookings() {
        return databaseManager.getBookings().stream()
            .filter(Booking::isConfirmed)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a profile is booked for a session
     */
    public boolean isBookedForSession(String profileId, String sessionId) {
        return databaseManager.getBookings().stream()
            .anyMatch(b -> b.getProfileId().equals(profileId) && b.getSessionId().equals(sessionId));
    }
    
    // ===== STATISTICS =====
    
    /**
     * Get total bookings count
     */
    public int getTotalBookings() {
        return databaseManager.getBookings().size();
    }
    
    /**
     * Get booking count by status
     */
    public long getBookingCountByStatus(String status) {
        return databaseManager.getBookings().stream()
            .filter(b -> b.getStatus().equalsIgnoreCase(status))
            .count();
    }
}