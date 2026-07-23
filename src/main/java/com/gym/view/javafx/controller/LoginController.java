package com.gym.view.javafx.controller;

import java.io.IOException;

import com.gym.controller.GymController;
import com.gym.model.Profile;
import com.gym.model.user.Admin;
import com.gym.model.user.Trainer;
import com.gym.model.user.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * LoginController - Handles user authentication and login
 */
public class LoginController {
    
    // ===== FXML INJECTED FIELDS =====
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private CheckBox rememberMe;
    @FXML private Button loginButton;
    
    // ===== INSTANCE FIELDS =====
    private GymController gymController;
    private Stage primaryStage;
    
    // ===== SETTERS =====
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    // ============================================================
    // INITIALIZATION
    // ============================================================
    
    @FXML
    public void initialize() {
        errorLabel.setText("");
        emailField.setOnAction(event -> handleLogin());
        passwordField.setOnAction(event -> handleLogin());
    }
    
    // ============================================================
    // LOGIN HANDLING
    // ============================================================
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            setError("Please enter both email and password");
            return;
        }
        
        // Check members
        for (Profile profile : gymController.getDataManager().getProfiles()) {
            if (profile.getEmail().equalsIgnoreCase(email)) {
                if (!profile.isActive()) {
                    setError("Account is inactive. Please contact support.");
                    return;
                }
                
                if (profile.getMembership() != null && !profile.getMembership().isValid()) {
                    setError("Membership expired. Please renew.");
                    return;
                }
                
                if (password.equals("password") || password.equals(profile.getName().toLowerCase())) {
                    setSuccess("Login successful!");
                    User user = new User(
                        profile.getProfileId(),
                        profile.getName(),
                        profile.getEmail(),
                        profile.getPhone(),
                        profile.getAddress(),
                        String.valueOf(profile.getProfileId()),
                        password
                    );
                    loadMainApplication(user);
                    return;
                } else {
                    setError("Invalid password");
                    return;
                }
            }
        }
        
        // Check trainers
        for (Trainer trainer : gymController.getDataManager().getTrainers()) {
            if (trainer.getEmail().equalsIgnoreCase(email) && 
                trainer.getPassword().equals(password)) {
                setSuccess("Trainer login successful!");
                loadMainApplication(trainer);
                return;
            }
        }
        
        // Check admins
        for (Admin admin : gymController.getDataManager().getAdmins()) {
            if (admin.getEmail().equalsIgnoreCase(email) && 
                admin.getPassword().equals(password)) {
                setSuccess("Admin login successful!");
                loadMainApplication(admin);
                return;
            }
        }
        
        setError("Invalid email or password");
    }
    
    // ============================================================
    // NAVIGATION
    // ============================================================
    
    private void loadMainApplication(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gym/view/javafx/fxml/main-view.fxml")
            );
            BorderPane root = loader.load();
            
            MainController mainController = loader.getController();
            mainController.setGymController(gymController);
            mainController.setStage(primaryStage);
            mainController.setUser(user);
            
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(
                getClass().getResource("/com/gym/view/css/styles.css").toExternalForm()
            );
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("🏋️ Gym Management System - " + user.getName());
            primaryStage.show();
            
        } catch (IOException e) {
            setError("Error loading application");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gym/view/javafx/fxml/register-view.fxml")
            );
            Scene scene = new Scene(loader.load(), 450, 600);
            
            RegisterController registerController = loader.getController();
            registerController.setGymController(gymController);
            registerController.setStage(primaryStage);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("🏋️ Gym Management System - Register");
            primaryStage.show();
            
        } catch (IOException e) {
            setError("Error loading registration page");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Recovery");
        alert.setHeaderText("Password Recovery");
        alert.setContentText("Please contact your system administrator to reset your password.");
        alert.showAndWait();
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    private void setError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
    
    private void setSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: green;");
    }
}