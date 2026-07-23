package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Attendance;
import com.gym.model.user.Trainer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reports UI Controller - Handles report generation and display
 */
public class ReportsController {
    
    @FXML private ComboBox<String> reportType;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private TextArea reportTextArea;
    @FXML private Label reportTitle;
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
        loadInitialStats();
    }
    
    @FXML
    public void initialize() {
        reportType.getItems().addAll(
            "Member Report",
            "Class Report",
            "Booking Report",
            "Attendance Report",
            "Revenue Report",
            "Trainer Report"
        );
        reportType.setValue("Member Report");
        
        // Set default date range (last 30 days)
        startDate.setValue(LocalDate.now().minusDays(30));
        endDate.setValue(LocalDate.now());
    }
    
    @FXML
    private void handleGenerateReport() {
        if (gymController == null) {
            statusLabel.setText("❌ System not ready");
            return;
        }
        
        String selectedReport = reportType.getValue();
        if (selectedReport == null) {
            statusLabel.setText("❌ Please select a report type");
            return;
        }
        
        statusLabel.setText("🔄 Generating " + selectedReport + "...");
        
        String report = generateReport(selectedReport);
        reportTextArea.setText(report);
        reportTitle.setText("📋 " + selectedReport);
        
        statusLabel.setText("✅ " + selectedReport + " generated successfully");
    }
    
    private String generateReport(String reportType) {
        switch (reportType) {
            case "Member Report":
                return generateMemberReport();
            case "Class Report":
                return generateClassReport();
            case "Booking Report":
                return generateBookingReport();
            case "Attendance Report":
                return generateAttendanceReport();
            case "Revenue Report":
                return generateRevenueReport();
            case "Trainer Report":
                return generateTrainerReport();
            default:
                return "Unknown report type";
        }
    }
    
    private List<Profile> getAllMembers() {
        if (gymController == null || gymController.getDataManager() == null) {
            return List.of();
        }
        return gymController.getDataManager().getProfiles();
    }
    
    private List<GymClass> getAllClasses() {
        if (gymController == null || gymController.getDataManager() == null) {
            return List.of();
        }
        return gymController.getDataManager().getGymClasses();
    }
    
    private List<Booking> getAllBookings() {
        if (gymController == null || gymController.getDataManager() == null) {
            return List.of();
        }
        return gymController.getDataManager().getBookings();
    }
    
    private List<Attendance> getAllAttendance() {
        if (gymController == null || gymController.getDataManager() == null) {
            return List.of();
        }
        return gymController.getDataManager().getAttendanceRecords();
    }
    
    private List<Trainer> getAllTrainers() {
        if (gymController == null || gymController.getDataManager() == null) {
            return List.of();
        }
        return gymController.getDataManager().getTrainers();
    }
    
    private String generateMemberReport() {
        List<Profile> members = getAllMembers();
        long active = members.stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .count();
        long inactive = members.size() - active;
        
        long basic = members.stream()
            .filter(p -> p.getMembership() != null && 
                        p.getMembership().getClass().getSimpleName().equals("Basic"))
            .count();
        long premium = members.stream()
            .filter(p -> p.getMembership() != null && 
                        p.getMembership().getClass().getSimpleName().equals("Premium"))
            .count();
        long family = members.stream()
            .filter(p -> p.getMembership() != null && 
                        p.getMembership().getClass().getSimpleName().equals("Family"))
            .count();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 MEMBER REPORT\n");
        sb.append("================\n\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("📈 Summary Statistics:\n");
        sb.append("  Total Members: ").append(members.size()).append("\n");
        sb.append("  Active Members: ").append(active).append("\n");
        sb.append("  Inactive Members: ").append(inactive).append("\n\n");
        sb.append("📋 Membership Breakdown:\n");
        sb.append("  Basic: ").append(basic).append("\n");
        sb.append("  Premium: ").append(premium).append("\n");
        sb.append("  Family: ").append(family).append("\n\n");
        sb.append("👤 Member List:\n");
        sb.append("---------------\n");
        for (Profile p : members) {
            sb.append("  ID: ").append(p.getProfileId())
              .append(" | Name: ").append(p.getName())
              .append(" | Email: ").append(p.getEmail())
              .append(" | Status: ");
            if (p.getMembership() != null && p.getMembership().isValid()) {
                sb.append("Active");
            } else {
                sb.append("Inactive");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private String generateClassReport() {
        List<GymClass> classes = getAllClasses();
        int totalCapacity = classes.stream().mapToInt(GymClass::getCapacity).sum();
        int totalBookings = classes.stream().mapToInt(GymClass::getCurrentBookings).sum();
        double utilization = totalCapacity > 0 ? (double) totalBookings / totalCapacity * 100 : 0;
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 CLASS REPORT\n");
        sb.append("===============\n\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("📈 Summary Statistics:\n");
        sb.append("  Total Classes: ").append(classes.size()).append("\n");
        sb.append("  Total Capacity: ").append(totalCapacity).append("\n");
        sb.append("  Total Bookings: ").append(totalBookings).append("\n");
        sb.append("  Utilization Rate: ").append(String.format("%.1f", utilization)).append("%\n\n");
        sb.append("📋 Class Details:\n");
        sb.append("-----------------\n");
        for (GymClass c : classes) {
            sb.append("  ID: ").append(c.getClassId())
              .append(" | Name: ").append(c.getClassName())
              .append(" | Type: ").append(c.getClass().getSimpleName())
              .append(" | Bookings: ").append(c.getCurrentBookings()).append("/").append(c.getCapacity())
              .append(" | Trainer: ").append(c.getTrainer())
              .append("\n");
        }
        return sb.toString();
    }
    
    private String generateBookingReport() {
        List<Booking> bookings = getAllBookings();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 BOOKING REPORT\n");
        sb.append("=================\n\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("📈 Summary Statistics:\n");
        sb.append("  Total Bookings: ").append(bookings.size()).append("\n");
        sb.append("  Confirmed: ").append(bookings.stream().filter(b -> "Confirmed".equalsIgnoreCase(b.getStatus())).count()).append("\n");
        sb.append("  Pending: ").append(bookings.stream().filter(b -> "Pending".equalsIgnoreCase(b.getStatus())).count()).append("\n");
        sb.append("  Cancelled: ").append(bookings.stream().filter(b -> "Cancelled".equalsIgnoreCase(b.getStatus())).count()).append("\n\n");
        sb.append("📋 Recent Bookings:\n");
        sb.append("-------------------\n");
        bookings.stream().limit(20).forEach(b -> {
            sb.append("  ID: ").append(b.getBookingId())
              .append(" | Member: ").append(b.getProfileId())
              .append(" | Class: ").append(b.getClassId())
              .append(" | Status: ").append(b.getStatus())
              .append("\n");
        });
        return sb.toString();
    }
    
    private String generateAttendanceReport() {
        List<Attendance> attendance = getAllAttendance();
        long present = attendance.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
        long absent = attendance.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
        long late = attendance.stream().filter(a -> "Late".equalsIgnoreCase(a.getStatus())).count();
        double rate = attendance.isEmpty() ? 0 : (double) present / attendance.size() * 100;
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 ATTENDANCE REPORT\n");
        sb.append("====================\n\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("📈 Summary Statistics:\n");
        sb.append("  Total Records: ").append(attendance.size()).append("\n");
        sb.append("  Present: ").append(present).append("\n");
        sb.append("  Absent: ").append(absent).append("\n");
        sb.append("  Late: ").append(late).append("\n");
        sb.append("  Attendance Rate: ").append(String.format("%.1f", rate)).append("%\n");
        return sb.toString();
    }
    
    private String generateRevenueReport() {
        // Calculate revenue from memberships
        double totalRevenue = 0;
        for (Profile p : getAllMembers()) {
            if (p.getMembership() != null && p.getMembership().isValid()) {
                totalRevenue += p.getMembership().calculateFee();
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 REVENUE REPORT\n");
        sb.append("=================\n\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("💰 Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
        return sb.toString();
    }
    
    private String generateTrainerReport() {
        List<Trainer> trainers = getAllTrainers();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 TRAINER REPORT\n");
        sb.append("=================\n\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("📈 Summary Statistics:\n");
        sb.append("  Total Trainers: ").append(trainers.size()).append("\n\n");
        sb.append("📋 Trainer List:\n");
        sb.append("----------------\n");
        for (Trainer t : trainers) {
            sb.append("  ID: ").append(t.getUserId())
              .append(" | Name: ").append(t.getName())
              .append(" | Specialization: ").append(t.getSpecialization())
              .append("\n");
        }
        return sb.toString();
    }
    
    @FXML
    private void handleApplyFilter() {
        statusLabel.setText("✅ Date range applied");
    }
    
    @FXML
    private void handleExportPDF() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export PDF");
        alert.setHeaderText("PDF Export");
        alert.setContentText("PDF export functionality will be implemented here.");
        alert.showAndWait();
        statusLabel.setText("📄 PDF export requested");
    }
    
    @FXML
    private void handleRefresh() {
        loadInitialStats();
        statusLabel.setText("✅ Data refreshed");
    }
    
    private void loadInitialStats() {
        if (gymController == null) return;
        
        List<Profile> members = getAllMembers();
        List<GymClass> classes = getAllClasses();
        List<Trainer> trainers = getAllTrainers();
        List<Attendance> attendance = getAllAttendance();
        
        reportTotalMembers.setText(String.valueOf(members.size()));
        reportTotalClasses.setText(String.valueOf(classes.size()));
        reportTotalTrainers.setText(String.valueOf(trainers.size()));
        
        // Calculate attendance rate
        long present = attendance.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
        double rate = attendance.isEmpty() ? 0 : (double) present / attendance.size() * 100;
        reportAttendanceRate.setText(String.format("%.1f%%", rate));
        
        // Calculate revenue
        double totalRevenue = 0;
        for (Profile p : members) {
            if (p.getMembership() != null && p.getMembership().isValid()) {
                totalRevenue += p.getMembership().calculateFee();
            }
        }
        reportTotalRevenue.setText(String.format("$%.2f", totalRevenue));
        
        // Active members
        long active = members.stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .count();
        reportActiveMembers.setText(String.valueOf(active));
    }
}
