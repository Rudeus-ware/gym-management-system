package com.gym.controller;

import com.gym.model.payment.Payment;
import com.gym.model.Profile;
import com.gym.database.DatabaseManager;
import com.gym.persistence.JsonDataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Payment operations
 */
public class PaymentController {
    
    private final DatabaseManager databaseManager;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    public PaymentController(DatabaseManager dataManager) {
        this.databaseManager = dataManager;
    }
    
    public PaymentController(JsonDataManager dataManager) {
        this.databaseManager = dataManager;
    }
    
    // ============================================================
    // CREATE OPERATIONS
    // ============================================================
    
    public Payment createPayment(String profileId, double amount, String method, String status) {
        if (profileId == null || profileId.isEmpty()) {
            System.out.println("❌ Invalid profile ID");
            return null;
        }
        
        Profile profile = databaseManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        String paymentId = "PAY" + System.currentTimeMillis();
        String paymentDate = LocalDate.now().toString();
        
        Payment payment = new Payment(paymentId, profileId, amount, paymentDate, method, status);
        
        databaseManager.addPayment(payment);
        databaseManager.saveAllData();
        
        System.out.println("✅ Payment recorded for: " + profile.getName());
        System.out.println("   Amount: $" + String.format("%.2f", amount));
        System.out.println("   Method: " + method);
        System.out.println("   Status: " + status);
        return payment;
    }
    
    public Payment createPayment(String profileId, double amount, String method) {
        return createPayment(profileId, amount, method, "PENDING");
    }
    
    public Payment createPayment(String profileId, double amount) {
        return createPayment(profileId, amount, "CASH", "PENDING");
    }
    
    // ============================================================
    // READ OPERATIONS
    // ============================================================
    
    public Payment getPaymentById(String id) {
        if (id == null || id.isEmpty()) return null;
        return databaseManager.getPayments().stream()
            .filter(p -> p.getPaymentId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public List<Payment> getPaymentsForProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) return List.of();
        return databaseManager.getPayments().stream()
            .filter(p -> p.getProfileId().equals(profileId))
            .collect(Collectors.toList());
    }
    
    public List<Payment> getAllPayments() {
        return databaseManager.getPayments();
    }
    
    public List<Payment> getPaymentsByStatus(String status) {
        if (status == null || status.isEmpty()) return List.of();
        return databaseManager.getPayments().stream()
            .filter(p -> p.getStatus().equalsIgnoreCase(status))
            .collect(Collectors.toList());
    }
    
    public List<Payment> getPaymentsByMethod(String method) {
        if (method == null || method.isEmpty()) return List.of();
        return databaseManager.getPayments().stream()
            .filter(p -> p.getMethod().equalsIgnoreCase(method))
            .collect(Collectors.toList());
    }
    
    public List<Payment> getPaymentsByDateRange(String startDate, String endDate) {
        if (startDate == null || endDate == null) return List.of();
        return databaseManager.getPayments().stream()
            .filter(p -> p.getPaymentDate().compareTo(startDate) >= 0 && 
                        p.getPaymentDate().compareTo(endDate) <= 0)
            .collect(Collectors.toList());
    }
    
    public List<Payment> getCompletedPayments() {
        return databaseManager.getPayments().stream()
            .filter(Payment::isCompleted)
            .collect(Collectors.toList());
    }
    
    public List<Payment> getPendingPayments() {
        return databaseManager.getPayments().stream()
            .filter(Payment::isPending)
            .collect(Collectors.toList());
    }
    
    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================
    
    public boolean updatePaymentStatus(String paymentId, String newStatus) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            System.out.println("❌ Payment not found: " + paymentId);
            return false;
        }
        
        payment.setStatus(newStatus);
        databaseManager.saveAllData();
        System.out.println("✅ Payment status updated to: " + newStatus);
        return true;
    }
    
    public boolean completePayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            System.out.println("❌ Payment not found: " + paymentId);
            return false;
        }
        
        if (payment.isPending()) {
            payment.complete();
            databaseManager.saveAllData();
            System.out.println("✅ Payment completed: " + paymentId);
            return true;
        }
        
        System.out.println("❌ Cannot complete payment (not pending): " + paymentId);
        return false;
    }
    
    public boolean failPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            System.out.println("❌ Payment not found: " + paymentId);
            return false;
        }
        
        if (payment.isPending()) {
            payment.fail();
            databaseManager.saveAllData();
            System.out.println("❌ Payment failed: " + paymentId);
            return true;
        }
        
        System.out.println("❌ Cannot fail payment (not pending): " + paymentId);
        return false;
    }
    
    public boolean refundPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            System.out.println("❌ Payment not found: " + paymentId);
            return false;
        }
        
        if (payment.isCompleted()) {
            payment.refund();
            databaseManager.saveAllData();
            System.out.println("✅ Payment refunded: " + paymentId);
            return true;
        }
        
        System.out.println("❌ Cannot refund payment (not completed): " + paymentId);
        return false;
    }
    
    // ============================================================
    // DELETE OPERATIONS
    // ============================================================
    
    public boolean deletePayment(String paymentId) {
        if (paymentId == null || paymentId.isEmpty()) {
            System.out.println("❌ Invalid payment ID");
            return false;
        }
        
        boolean removed = databaseManager.getPayments().removeIf(p -> p.getPaymentId().equals(paymentId));
        if (removed) {
            databaseManager.saveAllData();
            System.out.println("✅ Payment deleted: " + paymentId);
            return true;
        }
        
        System.out.println("❌ Payment not found: " + paymentId);
        return false;
    }
    
    // ============================================================
    // STATISTICS
    // ============================================================
    
    public double getTotalRevenue() {
        return databaseManager.getPayments().stream()
            .filter(Payment::isCompleted)
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    public double getTotalRevenueForProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) return 0.0;
        return databaseManager.getPayments().stream()
            .filter(p -> p.getProfileId().equals(profileId) && p.isCompleted())
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    public int getPaymentCountByStatus(String status) {
        if (status == null || status.isEmpty()) return 0;
        return (int) databaseManager.getPayments().stream()
            .filter(p -> p.getStatus().equalsIgnoreCase(status))
            .count();
    }
    
    public int getPaymentCountByMethod(String method) {
        if (method == null || method.isEmpty()) return 0;
        return (int) databaseManager.getPayments().stream()
            .filter(p -> p.getMethod().equalsIgnoreCase(method))
            .count();
    }
    
    public void printPaymentSummary() {
        System.out.println("📊 PAYMENT SUMMARY");
        System.out.println("==================");
        System.out.println("Total Payments: " + databaseManager.getPayments().size());
        System.out.println("Pending: " + getPaymentCountByStatus("PENDING"));
        System.out.println("Completed: " + getPaymentCountByStatus("COMPLETED"));
        System.out.println("Failed: " + getPaymentCountByStatus("FAILED"));
        System.out.println("Refunded: " + getPaymentCountByStatus("REFUNDED"));
        System.out.println("Total Revenue: $" + String.format("%.2f", getTotalRevenue()));
    }
}