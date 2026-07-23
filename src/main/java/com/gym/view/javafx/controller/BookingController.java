package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class BookingController {
    @FXML private TableView<?> bookingTable;
    @FXML private Text totalBookings;
    @FXML private Text confirmedBookings;
    @FXML private Text pendingBookings;
    @FXML private Text cancelledBookings;
    @FXML private Label statusLabel;
    
    private GymController gymController;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    @FXML public void handleNewBooking() { statusLabel.setText("New Booking clicked"); }
    @FXML public void handleRefresh() { statusLabel.setText("Refreshed"); }
}
