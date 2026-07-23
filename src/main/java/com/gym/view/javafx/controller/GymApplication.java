package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.persistence.DataInitializer;
import com.gym.persistence.DataManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymApplication extends Application {
    
    private DataManager dataManager;
    private GymController gymController;  // ✅ ADDED: Declare as class field
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data manager
        dataManager = new DataManager();
        
        // ✅ ADDED: Initialize gymController
        gymController = new GymController();
        
        // Check if data exists, if not, initialize test data
        if (dataManager.getProfiles().isEmpty()) {
            DataInitializer.initializeTestData(dataManager);
        }
        
        // ✅ FIXED: Correct FXML path with leading slash and full package
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/gym/view/javafx/fxml/login-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 550);
        
        // ✅ FIXED: Correct CSS path
        scene.getStylesheets().add(
            getClass().getResource("/com/gym/view/css/styles.css").toExternalForm()
        );
        
        // ✅ FIXED: Get LoginController and pass dependencies
        LoginController loginController = loader.getController();
        loginController.setGymController(gymController);
        loginController.setStage(primaryStage);
        
        // Set up the stage
        primaryStage.setTitle("🏋️ Gym Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Set close handler to save data
        primaryStage.setOnCloseRequest(event -> {
            if (dataManager != null) {
                dataManager.saveAllData();
            }
            if (gymController != null) {
                gymController.saveAllData();
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}