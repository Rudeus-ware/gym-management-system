package com.gym.view.javafx.controllers;

import java.io.IOException;

import com.gym.controller.SessionController;
import com.gym.persistence.DataManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {
    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    
    private DataManager dataManager;
    private Stage primaryStage;
    
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    @FXML
    public void initialize() {
        statusLabel.setText("Application ready");
        showDashboard();
    }
    
    // Navigation Methods
    @FXML
    public void showDashboard() {
        loadView("dashboard.fxml");
        updateStatus("Dashboard loaded");
    }
    
    @FXML
    public void showProfiles() {
        FXMLLoader loader = loadView("profile-view.fxml");
        if (loader != null) {
            ProfileController controller = loader.getController();
            controller.setDataManager(dataManager);
        }
        updateStatus("Profiles loaded");
    }
    
    @FXML
    public void showClasses() {
        FXMLLoader loader = loadView("class-view.fxml");
        if (loader != null) {
            JavaFxClassController controller = loader.getController();
            controller.setDataManager(dataManager);
        }
        updateStatus("Classes loaded");
    }
    
    @FXML
    public void showSessions() {
        FXMLLoader loader = loadView("session-view.fxml");
        if (loader != null) {
            SessionController controller = loader.getController();
            controller.setDataManager(dataManager);
        }
        updateStatus("Sessions loaded");
    }
    
    @FXML
    public void showBookings() {
        FXMLLoader loader = loadView("booking-view.fxml");
        if (loader != null) {
            com.gym.view.javafx.controllers.BookingController controller = loader.getController();
            controller.setDataManager(dataManager);
        }
        updateStatus("Bookings loaded");
    }
    
    @FXML
    public void showAttendance() {
        FXMLLoader loader = loadView("attendance-view.fxml");
        if (loader != null) {
            com.gym.view.javafx.controllers.AttendanceController controller = loader.getController();
            controller.setDataManager(dataManager);
        }
        updateStatus("Attendance loaded");
    }
    
    @FXML
    public void showReports() {
        loadView("reports-view.fxml");
        updateStatus("Reports loaded");
    }
    
    @FXML
    public void handleLogout() {
        // Save data and close
        dataManager.saveAllData();
        primaryStage.close();
    }
    
    // Utility Methods
    private FXMLLoader loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gym/view/fxml/" + fxmlFile));
            Pane view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            return loader;
        } catch (IOException e) {
            statusLabel.setText("Error loading view: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void updateStatus(String message) {
        statusLabel.setText("✓ " + message);
    }
}