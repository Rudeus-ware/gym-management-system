package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.persistence.DataManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    
    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    @FXML private Label userNameLabel;
    
    // Fields
    private DataManager dataManager;
    private GymController gymController;
    private Stage primaryStage;
    
    // ===== SETTER METHODS =====
    
    /**
     * Set the DataManager for this controller
     */
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.gymController = new GymController();
        System.out.println("✅ DataManager set in MainController");
        updateStatus("Data Manager initialized");
    }
    
    /**
     * Set the GymController for this controller
     */
    public void setUser(com.gym.model.user.User user) {
        if (userNameLabel != null) {
            userNameLabel.setText(user.getName());
        }
        System.out.println("✅ User set in MainController: " + user.getName());
    }
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
        this.dataManager = gymController.getDataManager();
        System.out.println("✅ GymController set in MainController");
        updateStatus("Gym Controller initialized");
    }
    
    /**
     * Set the primary stage
     */
    public void setStage(Stage stage) {
        this.primaryStage = stage;
        if (userNameLabel != null) {
            userNameLabel.setText("Admin");
        }
    }
    
    @FXML
    public void initialize() {
        updateStatus("✅ Application ready");
        showDashboard();
    }
    
    // ===== NAVIGATION METHODS =====
    
    @FXML
    public void showDashboard() {
        loadView("dashboard-view.fxml");
        updateStatus("✅ Dashboard loaded");
    }
    
    @FXML
    public void showProfiles() {
        FXMLLoader loader = loadView("profile-view.fxml");
        if (loader != null) {
            ProfileController controller = loader.getController();
            if (controller != null && gymController != null) {
                controller.setGymController(gymController);
            }
        }
        updateStatus("✅ Members loaded");
    }
    
    @FXML
    public void showClasses() {
        loadView("class-view.fxml");
        updateStatus("✅ Classes loaded");
    }
    
    @FXML
    public void showSessions() {
        loadView("session-view.fxml");
        updateStatus("✅ Sessions loaded");
    }
    
    @FXML
    public void showBookings() {
        loadView("booking-view.fxml");
        updateStatus("✅ Bookings loaded");
    }
    
    @FXML
    public void showAttendance() {
        loadView("attendance-view.fxml");
        updateStatus("✅ Attendance loaded");
    }
    
    @FXML
    public void showReports() {
        loadView("reports-view.fxml");
        updateStatus("✅ Reports loaded");
    }
    
    @FXML
    public void handleLogout() {
        if (gymController != null) {
            gymController.saveAllData();
        }
        if (primaryStage != null) {
            primaryStage.close();
        }
    }
    
    // ===== UTILITY METHODS =====
    
    private FXMLLoader loadView(String fxmlFile) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gym/view/javafx/fxml/" + fxmlFile)
            );
            Pane view = loader.load();
            contentArea.getChildren().add(view);
            return loader;
        } catch (IOException e) {
            updateStatus("❌ Error loading view: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}