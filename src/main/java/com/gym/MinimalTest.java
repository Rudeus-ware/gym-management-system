package com.gym;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MinimalTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("🏋️ JavaFX Test - SUCCESS!");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: green;");
        
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 300);
        
        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
