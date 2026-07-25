package com.gym.view.javafx.controller;

import java.io.IOException;
import java.time.LocalDate;

import com.gym.controller.GymController;
import com.gym.model.Profile;
import com.gym.model.membership.Basic;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> membershipType;
    @FXML private Label errorLabel;
    
    private GymController gymController;
    private Stage primaryStage;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    @FXML
    public void initialize() {
        membershipType.getItems().addAll("Basic", "Premium", "Family");
        membershipType.getSelectionModel().selectFirst();
    }
    
    @FXML
    private void handleRegister() {
        // Validate inputs
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String membership = membershipType.getValue();
        
        // Check all fields are filled
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || 
            address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("❌ Please fill in all fields");
            return;
        }
        
        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("❌ Please enter a valid email address");
            return;
        }
        
        // Check password length
        if (password.length() < 6) {
            errorLabel.setText("❌ Password must be at least 6 characters");
            return;
        }
        
        // Check passwords match
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("❌ Passwords do not match");
            return;
        }
        
        // ============================================================
        // ✅ AUTOMATED REGISTRATION - No admin needed!
        // ============================================================
        
        // Check if email already exists
        for (Profile existing : gymController.getDataManager().getProfiles()) {
            if (existing.getEmail().equalsIgnoreCase(email)) {
                errorLabel.setText("❌ Email already registered. Please login.");
                errorLabel.setStyle("-fx-text-fill: orange;");
                return;
            }
        }
        
        // Generate unique profile ID
        int profileId = gymController.getDataManager().getProfiles().size() + 1;
        
        // Create new profile
        Profile newProfile = new Profile(profileId, name, email, phone, address);
        
        // ✅ AUTOMATIC: Assign default membership based on selection
        String membershipTypeValue = membership != null ? membership : "Basic";
        int membershipId = gymController.getDataManager().getMemberships().size() + 1000;
        String startDate = LocalDate.now().toString();
        String expiryDate = LocalDate.now().plusMonths(1).toString();  // 1 month free trial
        
        Basic defaultMembership = new Basic(
            membershipId, 
            membershipTypeValue.equals("Premium") ? 99.99 : 
            membershipTypeValue.equals("Family") ? 69.99 : 49.99,
            startDate, 
            expiryDate, 
            "Active"
        );
        newProfile.setMembership(defaultMembership);
        
        // ✅ Save to DataManager
        gymController.getDataManager().addProfile(newProfile);
        gymController.getDataManager().addMembership(defaultMembership);
        gymController.saveAllData();
        
        // ✅ Show success message
        showSuccessMessage(name, email, membershipTypeValue);
        
        // ✅ Send automated welcome notification
        sendWelcomeNotification(newProfile);
    }
    
    // ✅ NEW: Show success message
    private void showSuccessMessage(String name, String email, String membershipType) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("🎉 Registration Complete!");
        alert.setHeaderText("Welcome to the Gym, " + name + "!");
        alert.setContentText(
            "✅ Your account has been created successfully!\n\n" +
            "📧 Email: " + email + "\n" +
            "💪 Membership: " + membershipType + "\n" +
            "📅 Trial Period: 1 Month Free!\n\n" +
            "🔑 You can now log in with your email and password.\n" +
            "Enjoy your fitness journey! 💪"
        );
        alert.showAndWait();
        
        // Go back to login
        handleBackToLogin();
    }
    
    // ✅ NEW: Send automated welcome notification
    private void sendWelcomeNotification(Profile profile) {
        // In a real app, this would send an email
        // For now, just log it
        System.out.println("📧 [AUTOMATED WELCOME EMAIL]");
        System.out.println("   To: " + profile.getEmail());
        System.out.println("   Subject: Welcome to the Gym!");
        System.out.println("   Body: Hello " + profile.getName() + "!");
        System.out.println("   Your account has been activated.");
        System.out.println("   Login: " + profile.getEmail());
        System.out.println("   Membership: " + 
            (profile.getMembership() != null ? 
             profile.getMembership().getClass().getSimpleName() : "Basic"));
        System.out.println("   Trial valid until: " + 
            (profile.getMembership() != null ? 
             profile.getMembership().getExpiryDate() : "N/A"));
        System.out.println("   ----------------------------------------");
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gym/view/javafx/fxml/login-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 400, 550);
            
            LoginController loginController = loader.getController();
            loginController.setGymController(gymController);
            loginController.setStage(primaryStage);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("🏋️ Gym Management System - Login");
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("❌ Error loading login page");
        }
    }
}