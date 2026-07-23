cat > src/main/java/com/gym/controller/PaymentController.java << 'EOF'
package com.gym.controller;

import com.gym.model.Payment;
import com.gym.model.Profile;
import com.gym.persistence.DataManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Payment Controller - Handles payment processing
 */
public class PaymentController {
    
    private DataManager dataManager;
    
    public PaymentController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    /**
     * Process a payment for a membership
     */
    public Payment processPayment(int profileId, double amount, String method) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        // Validate amount
        if (amount <= 0) {
            System.out.println("❌ Invalid amount: " + amount);
            return null;
        }
        
        int paymentId = getNextPaymentId();
        String paymentDate = LocalDate.now().toString();
        
        Payment payment = new Payment(paymentId, amount, paymentDate, method, "Completed");
        dataManager.addPayment(payment);
        dataManager.saveAllData();
        
        // Generate receipt
        String receipt = generateReceipt(payment, profile);
        System.out.println(receipt);
        
        return payment;
    }
    
    /**
     * Process a refund
     */
    public boolean refundPayment(int paymentId) {
        List<Payment> payments = dataManager.getPayments();
        for (Payment payment : payments) {
            if (payment.getPaymentId() == paymentId) {
                payment.setPaymentStatus("Refunded");
                dataManager.saveAllData();
                System.out.println("✅ Payment refunded: " + paymentId);
                return true;
            }
        }
        System.out.println("❌ Payment not found: " + paymentId);
        return false;
    }
    
    /**
     * Generate a receipt for a payment
     */
    public String generateReceipt(Payment payment, Profile profile) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=".repeat(50));
        receipt.append("\n📋 GYM MANAGEMENT SYSTEM - PAYMENT RECEIPT\n");
        receipt.append("=".repeat(50)).append("\n");
        receipt.append("Receipt #: ").append(payment.getPaymentId()).append("\n");
        receipt.append("Date: ").append(payment.getPaymentDate()).append("\n");
        receipt.append("Member: ").append(profile.getName()).append("\n");
        receipt.append("Member ID: ").append(profile.getProfileId()).append("\n");
        receipt.append("-".repeat(50)).append("\n");
        receipt.append("Amount: $").append(String.format("%.2f", payment.getAmount())).append("\n");
        receipt.append("Method: ").append(payment.getPaymentMethod()).append("\n");
        receipt.append("Status: ").append(payment.getPaymentStatus()).append("\n");
        receipt.append("=".repeat(50)).append("\n");
        receipt.append("Thank you for your business!\n");
        receipt.append("=".repeat(50));
        return receipt.toString();
    }
    
    /**
     * Get all payments for a profile
     */
    public List<Payment> getPaymentsForProfile(int profileId) {
        List<Payment> allPayments = dataManager.getPayments();
        if (allPayments == null) {
            return new ArrayList<>();
        }
        return allPayments.stream()
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
     * Get total revenue from all payments
     */
    public double getTotalRevenue() {
        List<Payment> payments = dataManager.getPayments();
        if (payments == null) {
            return 0.0;
        }
        return payments.stream()
            .filter(p -> "Completed".equalsIgnoreCase(p.getPaymentStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    /**
     * Get next payment ID
     */
    private int getNextPaymentId() {
        List<Payment> payments = dataManager.getPayments();
        if (payments == null || payments.isEmpty()) {
            return 1001;
        }
        return payments.stream()
            .mapToInt(Payment::getPaymentId)
            .max()
            .orElse(1000) + 1;
    }
}
EOF