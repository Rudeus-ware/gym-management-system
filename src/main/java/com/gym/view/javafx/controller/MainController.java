package com.gym.view.javafx.controller;

import java.io.IOException;

import com.gym.controller.GymController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main UI Controller - Handles navigation and layout
 * THIS IS A UI CONTROLLER - Uses JavaFX
 */
public class MainController {
    
    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    @FXML private Label userNameLabel;
    
    private GymController gymController;
    private Stage primaryStage;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
        if (userNameLabel != null) {
            userNameLabel.setText("Admin");
        }
    }
    
    @FXML
    public void initialize() {
        statusLabel.setText("✅ Application ready");
        showDashboard();
    }
    
    @FXML
    public void showDashboard() {
        loadView("dashboard-view.fxml");
        statusLabel.setText("✅ Dashboard loaded");
    }
    
    @FXML
    public void showProfiles() {
        FXMLLoader loader = loadView("profile-view.fxml");
        if (loader != null) {
            ProfileController controller = loader.getController();
            if (controller != null && gymController != null) {
                controller.setAdminController(gymController.getAdminController());
            }
        }
        statusLabel.setText("✅ Members loaded");
    }
    
    @FXML
    public void showClasses() {
        loadView("class-view.fxml");
        statusLabel.setText("✅ Classes loaded");
    }
    
    @FXML
    public void showSessions() {
        loadView("session-view.fxml");
        statusLabel.setText("✅ Sessions loaded");
    }
    
    @FXML
    public void showBookings() {
        loadView("booking-view.fxml");
        statusLabel.setText("✅ Bookings loaded");
    }
    
    @FXML
    public void showAttendance() {
        loadView("attendance-view.fxml");
        statusLabel.setText("✅ Attendance loaded");
    }
    
    @FXML
    public void showReports() {
        loadView("reports-view.fxml");
        statusLabel.setText("✅ Reports loaded");
    }
    
    @FXML
    public void handleLogout() {
        if (gymController != null) {
            gymController.saveAllData();
        }
        primaryStage.close();
    }
    
    private FXMLLoader loadView(String fxmlFile) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gym/view/fxml/" + fxmlFile)
            );
            Pane view = loader.load();
            contentArea.getChildren().add(view);
            return loader;
        } catch (IOException e) {
            statusLabel.setText("❌ Error loading view: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}