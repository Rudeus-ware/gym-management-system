package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.model.user.User;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Login UI Controller - Handles user authentication
 */
public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private CheckBox rememberMe;
    @FXML private Button loginButton;
    
    private GymController gymController;
    private Stage primaryStage;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("❌ Please enter both email and password");
            return;
        }
        
        // Attempt login using GymController
        User user = gymController.getLoginController().login(email, password);
        
        if (user != null) {
            errorLabel.setText("✅ Login successful!");
            errorLabel.setStyle("-fx-text-fill: green;");
            
            // Load main application
            loadMainApplication(user);
        } else {
            errorLabel.setText("❌ Invalid email or password");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
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
            errorLabel.setText("❌ Error loading application: " + e.getMessage());
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
}
