package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.model.user.User;
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
    
    private GymController gymController;
    private Stage primaryStage;
    private User currentUser;
    
    // ===== SETTERS =====
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        if (userNameLabel != null) {
            userNameLabel.setText(user.getName());
        }
        System.out.println("✅ User set: " + user.getName());
    }
    
    @FXML
    public void initialize() {
        statusLabel.setText("✅ Application ready");
        showDashboard();
    }
    
    // ===== NAVIGATION =====
    @FXML public void showDashboard() { loadView("dashboard-view.fxml"); statusLabel.setText("✅ Dashboard loaded"); }
    @FXML public void showProfiles() { loadView("profile-view.fxml"); statusLabel.setText("✅ Members loaded"); }
    @FXML public void showClasses() { loadView("class-view.fxml"); statusLabel.setText("✅ Classes loaded"); }
    @FXML public void showSessions() { loadView("session-view.fxml"); statusLabel.setText("✅ Sessions loaded"); }
    @FXML public void showBookings() { loadView("booking-view.fxml"); statusLabel.setText("✅ Bookings loaded"); }
    @FXML public void showAttendance() { loadView("attendance-view.fxml"); statusLabel.setText("✅ Attendance loaded"); }
    @FXML public void showReports() { loadView("reports-view.fxml"); statusLabel.setText("✅ Reports loaded"); }
    
    @FXML
    public void handleLogout() {
        if (gymController != null) {
            gymController.saveAllData();
        }
        if (primaryStage != null) {
            primaryStage.close();
        }
    }
    
    private void loadView(String fxmlFile) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gym/view/javafx/fxml/" + fxmlFile)
            );
            Pane view = loader.load();
            
            // Pass GymController to controllers that need it
            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setGymController", GymController.class)
                        .invoke(controller, gymController);
                } catch (Exception e) {
                    // Controller doesn't need GymController
                }
            }
            
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            statusLabel.setText("❌ Error loading view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
