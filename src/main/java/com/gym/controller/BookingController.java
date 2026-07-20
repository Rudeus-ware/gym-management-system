package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Session;
import com.gym.model.booking.Attendance;
import com.gym.persistence.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Booking operations
 * Handles all booking-related business logic
 */
public class BookingController {
    
    private DataManager dataManager;
    
    public BookingController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    // ===== CREATE BOOKING =====
    
    /**
     * Create a new booking
     */
    public Booking createBooking(int profileId, int classId, int sessionId) {
        // Check if profile exists
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found!");
            return null;
        }
        
        // Check if class exists
        GymClass gymClass = dataManager.findClassById(classId);
        if (gymClass == null) {
            System.out.println("❌ Class not found!");
            return null;
        }
        
        // Check if session exists
        Session session = null;
        for (Session s : dataManager.getSessions()) {
            if (s.getSessionId() == sessionId) {
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
        boolean alreadyBooked = dataManager.getBookings().stream()
            .anyMatch(b -> b.getProfileId() == profileId && b.getSessionId() == sessionId);
        if (alreadyBooked) {
            System.out.println("❌ Already booked for this session!");
            return null;
        }
        
        // Create booking
        int bookingId = dataManager.getBookings().size() + 1;
        String bookingDate = LocalDate.now().toString();
        Booking booking = new Booking(bookingId, profileId, classId, sessionId, bookingDate, "Confirmed");
        
        dataManager.addBooking(booking);
        gymClass.addBooking(profile.getName());
        session.addAttendee();
        dataManager.saveAllData();
        
        System.out.println("✅ Booking created successfully!");
        return booking;
    }
    
    // ===== CANCEL BOOKING =====
    
    /**
     * Cancel a booking
     */
    public boolean cancelBooking(int bookingId) {
        Booking booking = dataManager.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("❌ Booking not found!");
            return false;
        }
        
        // Remove from class
        GymClass gymClass = dataManager.findClassById(booking.getClassId());
        Profile profile = dataManager.findProfileById(booking.getProfileId());
        
        if (gymClass != null && profile != null) {
            gymClass.removeBooking(profile.getName());
        }
        
        // Remove from session
        for (Session session : dataManager.getSessions()) {
            if (session.getSessionId() == booking.getSessionId()) {
                session.removeAttendee();
                break;
            }
        }
        
        // Remove booking
        dataManager.getBookings().remove(booking);
        dataManager.saveAllData();
        
        System.out.println("✅ Booking cancelled successfully!");
        return true;
    }
    
    /**
     * Cancel all bookings for a profile
     */
    public int cancelAllBookingsForProfile(int profileId) {
        List<Booking> toRemove = dataManager.getBookings().stream()
            .filter(b -> b.getProfileId() == profileId)
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
    public boolean changeBookingStatus(int bookingId, String newStatus) {
        Booking booking = dataManager.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("❌ Booking not found!");
            return false;
        }
        
        booking.changeStatus(newStatus);
        dataManager.saveAllData();
        return true;
    }
    
    /**
     * Confirm a booking
     */
    public boolean confirmBooking(int bookingId) {
        Booking booking = dataManager.findBookingById(bookingId);
        if (booking == null) {
            System.out.println("❌ Booking not found!");
            return false;
        }
        
        booking.confirmBooking();
        dataManager.saveAllData();
        return true;
    }
    
    // ===== QUERIES =====
    
    /**
     * Get all bookings for a profile
     */
    public List<Booking> getBookingsForProfile(int profileId) {
        return dataManager.getBookings().stream()
            .filter(b -> b.getProfileId() == profileId)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all bookings for a class
     */
    public List<Booking> getBookingsForClass(int classId) {
        return dataManager.getBookings().stream()
            .filter(b -> b.getClassId() == classId)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all bookings for a session
     */
    public List<Booking> getBookingsForSession(int sessionId) {
        return dataManager.getBookings().stream()
            .filter(b -> b.getSessionId() == sessionId)
            .collect(Collectors.toList());
    }
    
    /**
     * Get active bookings for a profile
     */
    public List<Booking> getActiveBookingsForProfile(int profileId) {
        return dataManager.getBookings().stream()
            .filter(b -> b.getProfileId() == profileId && b.isActive())
            .collect(Collectors.toList());
    }
    
    /**
     * Get confirmed bookings
     */
    public List<Booking> getConfirmedBookings() {
        return dataManager.getBookings().stream()
            .filter(Booking::isConfirmed)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if a profile is booked for a session
     */
    public boolean isBookedForSession(int profileId, int sessionId) {
        return dataManager.getBookings().stream()
            .anyMatch(b -> b.getProfileId() == profileId && b.getSessionId() == sessionId);
    }
    
    // ===== STATISTICS =====
    
    /**
     * Get total bookings count
     */
    public int getTotalBookings() {
        return dataManager.getBookings().size();
    }
    
    /**
     * Get booking count by status
     */
    public long getBookingCountByStatus(String status) {
        return dataManager.getBookings().stream()
            .filter(b -> b.getStatus().equalsIgnoreCase(status))
            .count();
    }
}