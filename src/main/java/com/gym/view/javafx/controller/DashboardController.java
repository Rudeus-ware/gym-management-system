package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.model.Profile;
import com.gym.model.booking.Attendance;
import com.gym.model.booking.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {
    
    @FXML private Label welcomeLabel;
    @FXML private Text totalMembers;
    @FXML private Text activeMembers;
    @FXML private Text totalClasses;
    @FXML private Text availableClasses;
    @FXML private Text totalBookings;
    @FXML private Text activeBookings;
    @FXML private Text totalRevenue;
    @FXML private Text totalSessions;
    @FXML private Text activeSessions;
    @FXML private Text attendanceRate;
    @FXML private Text totalAttendance;
    @FXML private Text totalTrainers;
    @FXML private Text systemStatus;
    @FXML private Label lastUpdated;
    @FXML private Label statusLabel;
    @FXML private TableView<ActivityItem> recentActivityTable;
    
    private GymController gymController;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
        loadDashboard();
    }
    
    @FXML
    public void initialize() {
        setupActivityTable();
        welcomeLabel.setText("Welcome back, Admin! 👋");
    }
    
    private void setupActivityTable() {
        TableColumn<ActivityItem, String> dateCol = 
            (TableColumn<ActivityItem, String>) recentActivityTable.getColumns().get(0);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<ActivityItem, String> activityCol = 
            (TableColumn<ActivityItem, String>) recentActivityTable.getColumns().get(1);
        activityCol.setCellValueFactory(new PropertyValueFactory<>("activity"));
        
        TableColumn<ActivityItem, String> userCol = 
            (TableColumn<ActivityItem, String>) recentActivityTable.getColumns().get(2);
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        
        TableColumn<ActivityItem, String> statusCol = 
            (TableColumn<ActivityItem, String>) recentActivityTable.getColumns().get(3);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    @FXML
    private void handleRefresh() {
        loadDashboard();
        statusLabel.setText("✅ Dashboard refreshed");
    }
    
    private void loadDashboard() {
        if (gymController == null) return;
        
        try {
            // Members
            List<Profile> members = gymController.getDataManager().getProfiles();
            long active = members.stream()
                .filter(p -> p.getMembership() != null && p.getMembership().isValid())
                .count();
            totalMembers.setText(String.valueOf(members.size()));
            activeMembers.setText("Active: " + active);
            
            // Classes
            int classes = gymController.getDataManager().getGymClasses().size();
            long available = gymController.getDataManager().getGymClasses().stream()
                .filter(c -> !c.isFull())
                .count();
            totalClasses.setText(String.valueOf(classes));
            availableClasses.setText("Available: " + available);
            
            // Bookings
            int bookings = gymController.getDataManager().getBookings().size();
            long activeBook = gymController.getDataManager().getBookings().stream()
                .filter(b -> b.isActive())
                .count();
            totalBookings.setText(String.valueOf(bookings));
            activeBookings.setText("Active: " + activeBook);
            
            // Sessions
            int sessions = gymController.getDataManager().getSessions().size();
            long activeSess = gymController.getDataManager().getSessions().stream()
                .filter(s -> s.isActive())
                .count();
            totalSessions.setText(String.valueOf(sessions));
            activeSessions.setText("Active: " + activeSess);
            
            // Trainers
            totalTrainers.setText(String.valueOf(gymController.getDataManager().getTrainers().size()));
            
            // Attendance
            List<Attendance> attendance = gymController.getDataManager().getAttendanceRecords();
            totalAttendance.setText("Records: " + attendance.size());
            if (!attendance.isEmpty()) {
                long present = attendance.stream()
                    .filter(a -> "Present".equalsIgnoreCase(a.getStatus()))
                    .count();
                double rate = (double) present / attendance.size() * 100;
                attendanceRate.setText(String.format("%.1f%%", rate));
            } else {
                attendanceRate.setText("0%");
            }
            
            // Revenue
            double revenue = 0;
            for (Profile p : members) {
                if (p.getMembership() != null && p.getMembership().isValid()) {
                    revenue += p.getMembership().calculateFee();
                }
            }
            totalRevenue.setText(String.format("$%.2f", revenue));
            
            // System Status
            systemStatus.setText("✅ Operational");
            lastUpdated.setText("Last updated: " + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            
            // Recent Activity
            loadRecentActivity();
            
            statusLabel.setText("✅ Dashboard loaded successfully");
            
        } catch (Exception e) {
            statusLabel.setText("❌ Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadRecentActivity() {
        ObservableList<ActivityItem> activities = FXCollections.observableArrayList();
        
        // Add recent bookings
        List<Booking> bookings = gymController.getDataManager().getBookings();
        bookings.stream().limit(5).forEach(b -> {
            activities.add(new ActivityItem(
                b.getBookingDate(),
                "Booking created for class " + b.getClassId(),
                "Member " + b.getProfileId(),
                b.getStatus()
            ));
        });
        
        // Add recent attendance
        List<Attendance> attendance = gymController.getDataManager().getAttendanceRecords();
        attendance.stream().limit(3).forEach(a -> {
            activities.add(new ActivityItem(
                a.getAttendanceDate(),
                "Attendance marked: " + a.getStatus(),
                "Member " + a.getProfileId(),
                a.getStatus()
            ));
        });
        
        recentActivityTable.setItems(activities);
    }
    
    // ============================================================
    // INNER CLASS FOR ACTIVITY ITEMS
    // ============================================================
    
    public static class ActivityItem {
        private final String date;
        private final String activity;
        private final String user;
        private final String status;
        
        public ActivityItem(String date, String activity, String user, String status) {
            this.date = date;
            this.activity = activity;
            this.user = user;
            this.status = status;
        }
        
        public String getDate() { return date; }
        public String getActivity() { return activity; }
        public String getUser() { return user; }
        public String getStatus() { return status; }
    }
}
