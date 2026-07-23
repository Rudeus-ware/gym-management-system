#!/bin/bash

echo "🔧 Fixing Compilation Errors"
echo "============================"

# Step 1: Create Payment model
echo "📁 Creating Payment model..."
cat > src/main/java/com/gym/model/Payment.java << 'END'
package com.gym.model;

public class Payment {
    private int paymentId;
    private int profileId;
    private double amount;
    private String paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    
    public Payment(int paymentId, double amount, String paymentDate, String paymentMethod, String paymentStatus) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }
    
    public int getPaymentId() { return paymentId; }
    public int getProfileId() { return profileId; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
END
echo "✅ Payment.java created"

# Step 2: Update DataManager
echo "📁 Updating DataManager..."

# Step 3: Compile
echo "📁 Compiling..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
else
    echo "❌ Still have errors. Please check the output above."
fi
