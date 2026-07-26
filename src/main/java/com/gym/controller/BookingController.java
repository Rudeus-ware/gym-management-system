package com.gym.controller;

import com.gym.model.Profile;
import com.gym.model.booking.Booking;
import com.gym.model.classes.GymClass;
import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;
import com.gym.util.IdGenerator;
import com.gym.model.booking.Session;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookingController {
    
    private final DatabaseManager databaseManager;
    private final IdGenerator idGenerator;
    
    public BookingController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
        if (dataManager != null && dataManager.getConnection() != null) {
            this.idGenerator = new IdGenerator(dataManager.getConnection());
        } else {
            this.idGenerator = new IdGenerator("BOOK");
        }
    }
    
    public BookingController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
        this.idGenerator = new IdGenerator("BOOK");
    }
    
    // ============================================================
    // BOOKING OPERATIONS
    // ============================================================
    
    public Booking createBooking(String profileId, String sessionId, String status) {
        if (profileId == null || sessionId == null) {
            System.out.println("❌ Profile ID and Session ID are required");
            return null;
        }
        
        Profile profile = databaseManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        // Check if class exists (through session)
        Session session = databaseManager.getSessions().stream()
            .filter(s -> s.getSessionId().equals(sessionId))
            .findFirst()
            .orElse(null);
        if (session == null) {
            System.out.println("❌ Session not found: " + sessionId);
            return null;
        }
        
        // Check if already booked
        boolean alreadyBooked = databaseManager.getBookings().stream()
            .anyMatch(b -> b.getProfileId().equals(profileId) && b.getSessionId().equals(sessionId));
        if (alreadyBooked) {
            System.out.println("⚠️ Already booked for this session!");
            return null;
        }
        
        String bookingId = idGenerator.generateBookingId(LocalDate.now());
        String bookingDate = LocalDate.now().toString();
        Booking booking = new Booking(bookingId, profileId, sessionId, bookingDate, status);
        databaseManager.addBooking(booking);
        databaseManager.saveAllData();
        
        System.out.println("✅ Booking created for: " + profile.getName());
        return booking;
    }
    
    public Booking findBookingById(String id) {
        return databaseManager.findBookingById(id);
    }
    
    public List<Booking> getAllBookings() {
        return databaseManager.findAllBookings();
    }
    
    public List<Booking> getBookingsForProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) return List.of();
        return databaseManager.getBookings().stream()
            .filter(b -> b.getProfileId().equals(profileId))
            .collect(Collectors.toList());
    }
    
    public boolean cancelBooking(String bookingId) {
        boolean removed = databaseManager.getBookings().removeIf(b -> b.getBookingId().equals(bookingId));
        if (removed) {
            databaseManager.saveAllData();
            System.out.println("✅ Booking cancelled: " + bookingId);
        }
        return removed;
    }
}