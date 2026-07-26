package com.gym.model.payment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Payment model class
 */
public class Payment {
    
    private String paymentId;
    private String profileId;
    private double amount;
    private String paymentDate;
    private String method;
    private String status;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Main constructor - all fields
     */
    public Payment(String paymentId, String profileId, double amount, 
                   String paymentDate, String method, String status) {
        this.paymentId = paymentId;
        this.profileId = profileId;
        this.amount = amount;
        this.paymentDate = paymentDate != null ? paymentDate : LocalDate.now().toString();
        this.method = method != null ? method : "CASH";
        this.status = status != null ? status : "PENDING";
    }
    
    /**
     * Constructor without profileId
     */
    public Payment(String paymentId, double amount, String paymentDate, String method, String status) {
        this(paymentId, null, amount, paymentDate, method, status);
    }
    
    /**
     * Constructor with default values
     */
    public Payment(String paymentId, String profileId, double amount) {
        this(paymentId, profileId, amount, LocalDate.now().toString(), "CASH", "PENDING");
    }
    
    /**
     * Default constructor
     */
    public Payment() {
        this(null, null, 0.0, LocalDate.now().toString(), "CASH", "PENDING");
    }
    
    // ============================================================
    // GETTERS
    // ============================================================
    
    public String getPaymentId() { 
        return paymentId; 
    }
    
    public String getProfileId() { 
        return profileId; 
    }
    
    public double getAmount() { 
        return amount; 
    }
    
    public String getPaymentDate() { 
        return paymentDate; 
    }
    
    public String getMethod() { 
        return method; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    public void setPaymentId(String paymentId) { 
        this.paymentId = paymentId; 
    }
    
    public void setProfileId(String profileId) { 
        this.profileId = profileId; 
    }
    
    public void setAmount(double amount) { 
        this.amount = amount; 
    }
    
    public void setPaymentDate(String paymentDate) { 
        this.paymentDate = paymentDate; 
    }
    
    public void setMethod(String method) { 
        this.method = method; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(status);
    }
    
    public boolean isRefunded() {
        return "REFUNDED".equalsIgnoreCase(status);
    }
    
    public void complete() {
        this.status = "COMPLETED";
    }
    
    public void fail() {
        this.status = "FAILED";
    }
    
    public void refund() {
        this.status = "REFUNDED";
    }
    
    public String getFormattedDate() {
        try {
            LocalDate date = LocalDate.parse(paymentDate);
            return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        } catch (Exception e) {
            return paymentDate;
        }
    }
    
    public String getStatusDisplay() {
        return switch (status) {
            case "PENDING" -> "⏳ Pending";
            case "COMPLETED" -> "✅ Completed";
            case "FAILED" -> "❌ Failed";
            case "REFUNDED" -> "🔄 Refunded";
            default -> "❓ " + status;
        };
    }
    
    public String getMethodDisplay() {
        return switch (method) {
            case "CASH" -> "💰 Cash";
            case "CARD" -> "💳 Card";
            case "BANK_TRANSFER" -> "🏦 Bank Transfer";
            case "MOBILE_MONEY" -> "📱 Mobile Money";
            default -> "❓ " + method;
        };
    }
    
    @Override
    public String toString() {
        return String.format("Payment{id='%s', profile='%s', amount=%.2f, date='%s', status='%s'}",
            paymentId, profileId, amount, paymentDate, status);
    }
}