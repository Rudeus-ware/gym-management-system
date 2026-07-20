package com.gym.controller;

import com.gym.model.Payment;
import com.gym.model.Profile;
import com.gym.persistence.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        
        int paymentId = 1000 + dataManager.getPayments().size() + 1;
        String paymentDate = LocalDate.now().toString();
        
        Payment payment = new Payment(paymentId, amount, paymentDate, method, "Completed");
        dataManager.addPayment(payment);
        dataManager.saveAllData();
        
        System.out.println("✅ Payment of $" + amount + " processed successfully!");
        return payment;
    }
    
    /**
     * Get payments for a profile
     */
    public List<Payment> getPaymentsForProfile(int profileId) {
        return dataManager.getPayments().stream()
            .filter(p -> p.getProfileId() == profileId)
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
        return dataManager.getPayments().stream()
            .mapToDouble(Payment::getAmount)
            .sum();
    }
}