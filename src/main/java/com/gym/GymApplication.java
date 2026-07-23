package com.gym;

import com.gym.controller.GymController;
import com.gym.persistence.DataInitializer;
import com.gym.view.javafx.controller.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymApplication extends Application {
    
    private GymController gymController;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize controllers
        gymController = new GymController();
        
        // Load test data if empty
        if (gymController.getDataManager().getProfiles().isEmpty()) {
            DataInitializer.initializeTestData(gymController.getDataManager());
        }
        
        // Load login screen
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/gym/view/javafx/fxml/login-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 550);
        scene.getStylesheets().add(
            getClass().getResource("/com/gym/view/css/styles.css").toExternalForm()
        );
        
        LoginController loginController = loader.getController();
        loginController.setGymController(gymController);
        loginController.setStage(primaryStage);
        
        primaryStage.setTitle("🏋️ Gym Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(event -> gymController.saveAllData());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}