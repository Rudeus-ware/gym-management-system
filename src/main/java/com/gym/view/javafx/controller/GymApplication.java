package com.gym.view.javafx.controller;

import com.gym.persistence.DataInitializer;
import com.gym.persistence.DataManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymApplication extends Application {
    
    private DataManager dataManager;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data manager
        dataManager = new DataManager();
        
        // Check if data exists, if not, initialize test data
        if (dataManager.getProfiles().isEmpty()) {
            DataInitializer.initializeTestData(dataManager);
        }
        
        // Load the main FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        
        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        // Get controller and pass data
        MainController controller = loader.getController();
        controller.setDataManager(dataManager);
        controller.setStage(primaryStage);
        
        // Set up the stage
        primaryStage.setTitle("🏋️ Gym Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Set close handler to save data
        primaryStage.setOnCloseRequest(event -> {
            dataManager.saveAllData();
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}