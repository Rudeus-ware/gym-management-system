package com.gym;

import com.gym.controller.GymController;
import com.gym.view.javafx.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymApplication extends Application {
    
    private GymController gymController;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize the main controller
        gymController = new GymController();
        
        // Load the main FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/gym/view/javafx/fxml/main-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 1200, 800);
        
        // Load CSS
        scene.getStylesheets().add(
            getClass().getResource("/com/gym/view/css/styles.css").toExternalForm()
        );
        
        // ✅ FIX: Use setGymController instead of setDataManager
        MainController mainController = loader.getController();
        mainController.setGymController(gymController);
        mainController.setStage(primaryStage);
        
        // Setup stage
        primaryStage.setTitle("🏋️ Gym Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Save on close
        primaryStage.setOnCloseRequest(event -> {
            gymController.saveAllData();
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}