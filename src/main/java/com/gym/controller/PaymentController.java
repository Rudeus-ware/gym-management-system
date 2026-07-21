package com.gym.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.gym.model.Profile;
import com.gym.model.payment.Payment;
import com.gym.persistence.DataManager;

/**
 * Controller for Payment operations
 */
public class PaymentController {
    
    private DataManager dataManager;
    
    public PaymentController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    /**
     * Make a payment
     */
    public Payment makePayment(int profileId, double amount, String method) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found!");
            return null;
        }
        
        int paymentId = 1000 + dataManager.getBookings().size() + 1;
        String paymentDate = LocalDate.now().toString();
        
        Payment payment = new Payment(String.valueOf(paymentId), amount, method);
        dataManager.addBooking(new com.gym.model.booking.Booking(paymentId, profileId, 0, 0, paymentDate, "Completed"));
        dataManager.saveAllData();
        
        System.out.println("✅ Payment of $" + amount + " processed successfully!");
        return payment;
    }
    
    /**
     * Get payments for a profile
     */
    public List<Payment> getPaymentsForProfile(int profileId) {
        return dataManager.getBookings().stream()
            .filter(b -> b.getProfileId() == profileId)
            .map(b -> new Payment(String.valueOf(b.getBookingId()), 0.0, "N/A"))
            .collect(Collectors.toList());
    }
    
    /**
     * Get total payments for a profile
     */
    public double getTotalPaymentsForProfile(int profileId) {
        return getPaymentsForProfile(profileId).stream()
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    /**
     * Get total revenue
     */
    public double getTotalRevenue() {
        return dataManager.getBookings().stream()
            .mapToDouble(b -> 0.0)
            .sum();
    }
}