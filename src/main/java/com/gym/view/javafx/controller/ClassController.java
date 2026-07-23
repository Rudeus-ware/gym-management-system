package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class ClassController {
    @FXML private TableView<?> classTable;
    @FXML private Text totalClasses;
    @FXML private Text availableClasses;
    @FXML private Text fullClasses;
    @FXML private Label statusLabel;
    
    private GymController gymController;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    @FXML public void handleAddClass() { statusLabel.setText("Add Class clicked"); }
    @FXML public void handleRefresh() { statusLabel.setText("Refreshed"); }
}
