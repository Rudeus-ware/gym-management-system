package com.gym.view.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GymApplication extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Gym Management System");
        StackPane root = new StackPane(label);
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Gym Management System");
        stage.show();
    }
}
