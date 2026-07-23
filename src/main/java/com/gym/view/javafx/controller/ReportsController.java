package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class ReportsController {
    @FXML private ComboBox<String> reportType;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextArea reportTextArea;
    @FXML private Text reportTitle;
    @FXML private Text reportTotalMembers;
    @FXML private Text reportActiveMembers;
    @FXML private Text reportTotalRevenue;
    @FXML private Text reportAttendanceRate;
    @FXML private Text reportTotalClasses;
    @FXML private Text reportTotalTrainers;
    @FXML private Label statusLabel;
    
    private GymController gymController;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
        reportType.getItems().addAll("Member Report", "Class Report", "Booking Report", "Attendance Report", "Revenue Report", "Trainer Report");
        reportType.setValue("Member Report");
    }
    
    @FXML public void handleGenerateReport() { statusLabel.setText("Report generated"); }
    @FXML public void handleExportPDF() { statusLabel.setText("PDF exported"); }
    @FXML public void handleApplyFilter() { statusLabel.setText("Filter applied"); }
    @FXML public void handleRefresh() { statusLabel.setText("Refreshed"); }
}
