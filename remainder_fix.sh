#!/bin/bash

# =============================================================================
# Script: enhance-project.sh
# Purpose: Address all identified gaps in the Gym Management System
# =============================================================================

set -e

echo "🚀 Starting Project Enhancement"
echo "================================"
echo ""

# =============================================================================
# 1. FIX COMPILER WARNING
# =============================================================================

echo "📁 1. Fixing Compiler Warning..."
echo "-------------------------------"

if [ -f "pom.xml" ]; then
    cp pom.xml pom.xml.bak
    echo "✅ Backup created: pom.xml.bak"
    
    # Update to use release flag
    sed -i 's/<maven.compiler.source>17<\/maven.compiler.source>/<maven.compiler.release>17<\/maven.compiler.release>/g' pom.xml
    sed -i 's/<maven.compiler.target>17<\/maven.compiler.target>//g' pom.xml
    
    # Also update compiler plugin if present
    sed -i 's/<source>17<\/source>/<release>17<\/release>/g' pom.xml
    sed -i 's/<target>17<\/target>//g' pom.xml
    
    echo "✅ pom.xml updated with release flag"
else
    echo "⚠️ pom.xml not found, skipping"
fi
echo ""

# =============================================================================
# 2. CREATE MISSING LOGIN CONTROLLER
# =============================================================================

echo "📁 2. Creating Login Controller..."
echo "--------------------------------"

mkdir -p src/main/java/com/gym/view/javafx/controller

cat > src/main/java/com/gym/view/javafx/controller/LoginController.java << 'EOF'
package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.model.user.User;
import com.gym.model.user.Trainer;
import com.gym.model.user.Admin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Login UI Controller - Handles user authentication
 */
public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private CheckBox rememberMe;
    @FXML private Button loginButton;
    
    private GymController gymController;
    private Stage primaryStage;
    
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
    }
    
    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("❌ Please enter both email and password");
            return;
        }
        
        // Attempt login using GymController
        User user = gymController.getLoginController().login(email, password);
        
        if (user != null) {
            errorLabel.setText("✅ Login successful!");
            errorLabel.setStyle("-fx-text-fill: green;");
            
            // Load main application
            loadMainApplication(user);
        } else {
            errorLabel.setText("❌ Invalid email or password");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    private void loadMainApplication(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/gym/view/javafx/fxml/main-view.fxml")
            );
            BorderPane root = loader.load();
            
            MainController mainController = loader.getController();
            mainController.setGymController(gymController);
            mainController.setStage(primaryStage);
            mainController.setUser(user);
            
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(
                getClass().getResource("/com/gym/view/css/styles.css").toExternalForm()
            );
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("🏋️ Gym Management System - " + user.getName());
            primaryStage.show();
            
        } catch (IOException e) {
            errorLabel.setText("❌ Error loading application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Recovery");
        alert.setHeaderText("Password Recovery");
        alert.setContentText("Please contact your system administrator to reset your password.");
        alert.showAndWait();
    }
}
EOF

echo "✅ LoginController.java created"
echo ""

# =============================================================================
# 3. CREATE MISSING LOGIN FXML
# =============================================================================

echo "📁 3. Creating Login FXML..."
echo "---------------------------"

mkdir -p src/main/resources/com/gym/view/javafx/fxml

cat > src/main/resources/com/gym/view/javafx/fxml/login-view.fxml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Text?>

<BorderPane xmlns="http://javafx.com/javafx/19"
            xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="com.gym.view.javafx.controller.LoginController"
            prefHeight="500" prefWidth="400"
            style="-fx-background-color: #ecf0f1;">

    <center>
        <VBox alignment="CENTER" spacing="20" style="-fx-padding: 40px;">
            <!-- Logo/Title -->
            <VBox alignment="CENTER" spacing="10">
                <Text text="🏋️" style="-fx-font-size: 48px;" />
                <Text text="Gym Management System" style="-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #2c3e50;" />
                <Text text="Please login to continue" style="-fx-font-size: 14px; -fx-fill: #7f8c8d;" />
            </VBox>

            <!-- Login Form -->
            <VBox spacing="15" style="-fx-background-color: white; -fx-padding: 30px; -fx-border-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);" prefWidth="320">
                <Label text="Email" style="-fx-font-weight: bold; -fx-text-fill: #2c3e50;" />
                <TextField fx:id="emailField" promptText="Enter your email" prefHeight="35">
                    <padding>
                        <Insets left="10" right="10" />
                    </padding>
                </TextField>

                <Label text="Password" style="-fx-font-weight: bold; -fx-text-fill: #2c3e50;" />
                <PasswordField fx:id="passwordField" promptText="Enter your password" prefHeight="35">
                    <padding>
                        <Insets left="10" right="10" />
                    </padding>
                </PasswordField>

                <HBox alignment="CENTER_RIGHT" spacing="10">
                    <CheckBox fx:id="rememberMe" text="Remember me" />
                    <Hyperlink text="Forgot password?" onAction="#handleForgotPassword" />
                </HBox>

                <Button fx:id="loginButton" text="🔑 Login" prefHeight="40" prefWidth="320" 
                        onAction="#handleLogin" style="-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;" />

                <Label fx:id="errorLabel" text="" style="-fx-text-fill: #e74c3c; -fx-font-size: 12px;" />
            </VBox>

            <!-- Footer -->
            <Text text="© 2026 Gym Management System" style="-fx-font-size: 12px; -fx-fill: #bdc3c7;" />
        </VBox>
    </center>
</BorderPane>
EOF

echo "✅ login-view.fxml created"
echo ""

# =============================================================================
# 4. CREATE REPORTS CONTROLLER
# =============================================================================

echo "📁 4. Creating Reports Controller..."
echo "-----------------------------------"

cat > src/main/java/com/gym/view/javafx/controller/ReportsController.java << 'EOF'
package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.model.Profile;
import com.gym.model.classes.GymClass;
import com.gym.model.booking.Booking;
import com.gym.model.booking.Attendance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    
    private String generateMemberReport() {
        List<Profile> members = gymController.getAllMembers();
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
        List<GymClass> classes = gymController.getAllClasses();
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
        List<Booking> bookings = gymController.getAllBookings();
        
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
        List<Attendance> attendance = gymController.getAllAttendance();
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
        for (Profile p : gymController.getAllMembers()) {
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
        List<com.gym.model.user.Trainer> trainers = gymController.getAllTrainers();
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 TRAINER REPORT\n");
        sb.append("=================\n\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n\n");
        sb.append("📈 Summary Statistics:\n");
        sb.append("  Total Trainers: ").append(trainers.size()).append("\n\n");
        sb.append("📋 Trainer List:\n");
        sb.append("----------------\n");
        for (com.gym.model.user.Trainer t : trainers) {
            sb.append("  ID: ").append(t.getUserId())
              .append(" | Name: ").append(t.getName())
              .append(" | Specialization: ").append(t.getSpecialization())
              .append("\n");
        }
        return sb.toString();
    }
    
    @FXML
    private void handleApplyFilter() {
        // Filter reports by date range
        statusLabel.setText("✅ Date range applied");
    }
    
    @FXML
    private void handleExportPDF() {
        // Placeholder for PDF export
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
        
        reportTotalMembers.setText(String.valueOf(gymController.getAllMembers().size()));
        reportTotalClasses.setText(String.valueOf(gymController.getAllClasses().size()));
        reportTotalTrainers.setText(String.valueOf(gymController.getAllTrainers().size()));
        
        // Calculate attendance rate
        List<Attendance> attendance = gymController.getAllAttendance();
        long present = attendance.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
        double rate = attendance.isEmpty() ? 0 : (double) present / attendance.size() * 100;
        reportAttendanceRate.setText(String.format("%.1f%%", rate));
        
        // Calculate revenue
        double totalRevenue = 0;
        for (Profile p : gymController.getAllMembers()) {
            if (p.getMembership() != null && p.getMembership().isValid()) {
                totalRevenue += p.getMembership().calculateFee();
            }
        }
        reportTotalRevenue.setText(String.format("$%.2f", totalRevenue));
        
        // Active members
        long active = gymController.getAllMembers().stream()
            .filter(p -> p.getMembership() != null && p.getMembership().isValid())
            .count();
        reportActiveMembers.setText(String.valueOf(active));
    }
}
EOF

echo "✅ ReportsController.java created"
echo ""

# =============================================================================
# 5. CREATE REPORTS FXML
# =============================================================================

echo "📁 5. Creating Reports FXML..."
echo "-----------------------------"

cat > src/main/resources/com/gym/view/javafx/fxml/reports-view.fxml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Text?>

<VBox xmlns="http://javafx.com/javafx/19"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="com.gym.view.javafx.controller.ReportsController"
      spacing="20" style="-fx-padding: 20px;">

    <!-- Header -->
    <HBox alignment="CENTER_LEFT" spacing="20">
        <Text text="💰 Reports & Analytics" style="-fx-font-size: 24px; -fx-font-weight: bold;" />
        <Region HBox.hgrow="ALWAYS" />
        <Button text="📊 Generate Report" styleClass="button-primary" onAction="#handleGenerateReport" />
        <Button text="📥 Export PDF" styleClass="button-success" onAction="#handleExportPDF" />
        <Button text="🔄 Refresh" styleClass="button-primary" onAction="#handleRefresh" />
    </HBox>

    <!-- Report Type Selection -->
    <HBox spacing="10" style="-fx-background-color: white; -fx-padding: 10px; -fx-border-radius: 5px;">
        <Label text="Report Type:" style="-fx-font-weight: bold; -fx-padding: 5px 0;" />
        <ComboBox fx:id="reportType" promptText="Select report type" prefWidth="200" />
        <Label text="Date Range:" style="-fx-font-weight: bold; -fx-padding: 5px 0; -fx-margin-left: 20px;" />
        <DatePicker fx:id="startDate" promptText="Start Date" />
        <DatePicker fx:id="endDate" promptText="End Date" />
        <Button text="Apply" styleClass="button-primary" onAction="#handleApplyFilter" />
    </HBox>

    <!-- Report Content -->
    <VBox fx:id="reportContent" spacing="15" style="-fx-background-color: white; -fx-padding: 20px; -fx-border-radius: 5px;" VBox.vgrow="ALWAYS">
        <Text fx:id="reportTitle" text="📋 Report Preview" style="-fx-font-size: 18px; -fx-font-weight: bold;" />
        <Separator />
        <TextArea fx:id="reportTextArea" promptText="Report will appear here..." 
                  prefHeight="400" wrapText="true" editable="false" />
    </VBox>

    <!-- Stats Summary -->
    <HBox spacing="20" style="-fx-background-color: white; -fx-padding: 15px; -fx-border-radius: 5px;">
        <VBox>
            <Label text="Total Members" />
            <Text fx:id="reportTotalMembers" text="0" styleClass="stat-number, stat-primary" />
        </VBox>
        <VBox>
            <Label text="Active Members" />
            <Text fx:id="reportActiveMembers" text="0" styleClass="stat-number, stat-success" />
        </VBox>
        <VBox>
            <Label text="Total Revenue" />
            <Text fx:id="reportTotalRevenue" text="$0.00" styleClass="stat-number, stat-warning" />
        </VBox>
        <VBox>
            <Label text="Attendance Rate" />
            <Text fx:id="reportAttendanceRate" text="0%" styleClass="stat-number, stat-primary" />
        </VBox>
        <VBox>
            <Label text="Classes" />
            <Text fx:id="reportTotalClasses" text="0" styleClass="stat-number, stat-success" />
        </VBox>
        <VBox>
            <Label text="Trainers" />
            <Text fx:id="reportTotalTrainers" text="0" styleClass="stat-number, stat-danger" />
        </VBox>
    </HBox>

    <!-- Status Label -->
    <Label fx:id="statusLabel" text="Ready" />
</VBox>
EOF

echo "✅ reports-view.fxml created"
echo ""

# =============================================================================
# 6. CREATE/UPDATE CSS
# =============================================================================

echo "📁 6. Creating/Updating CSS..."
echo "-----------------------------"

mkdir -p src/main/resources/com/gym/view/css

cat > src/main/resources/com/gym/view/css/styles.css << 'EOF'
/* ===== ROOT STYLES ===== */
.root {
    -fx-font-family: "Segoe UI", Arial, sans-serif;
}

/* ===== HEADER ===== */
.header {
    -fx-background-color: linear-gradient(to right, #2c3e50, #3498db);
}

.header-label {
    -fx-font-size: 24px;
    -fx-font-weight: bold;
    -fx-text-fill: white;
}

.sub-header {
    -fx-font-size: 14px;
    -fx-text-fill: #bdc3c7;
}

/* ===== NAVIGATION ===== */
.nav-button {
    -fx-background-color: transparent;
    -fx-text-fill: #ecf0f1;
    -fx-font-size: 14px;
    -fx-padding: 10px 20px;
    -fx-cursor: hand;
    -fx-alignment: CENTER_LEFT;
}

.nav-button:hover {
    -fx-background-color: rgba(255,255,255,0.1);
}

.nav-button:pressed {
    -fx-background-color: rgba(255,255,255,0.2);
}

/* ===== BUTTONS ===== */
.button-primary {
    -fx-background-color: #3498db;
    -fx-text-fill: white;
    -fx-padding: 8px 20px;
    -fx-cursor: hand;
}

.button-primary:hover {
    -fx-background-color: #2980b9;
}

.button-success {
    -fx-background-color: #2ecc71;
    -fx-text-fill: white;
    -fx-padding: 8px 20px;
    -fx-cursor: hand;
}

.button-success:hover {
    -fx-background-color: #27ae60;
}

.button-danger {
    -fx-background-color: #e74c3c;
    -fx-text-fill: white;
    -fx-padding: 8px 20px;
    -fx-cursor: hand;
}

.button-danger:hover {
    -fx-background-color: #c0392b;
}

.button-warning {
    -fx-background-color: #f39c12;
    -fx-text-fill: white;
    -fx-padding: 8px 20px;
    -fx-cursor: hand;
}

.button-warning:hover {
    -fx-background-color: #e67e22;
}

/* ===== TABLES ===== */
.table-view {
    -fx-background-color: white;
}

.table-view .column-header {
    -fx-background-color: #2c3e50;
    -fx-text-fill: white;
}

.table-row-cell:even {
    -fx-background-color: #f8f9fa;
}

.table-row-cell:selected {
    -fx-background-color: #3498db;
    -fx-text-fill: white;
}

/* ===== STATS ===== */
.stat-number {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
}

.stat-primary { -fx-text-fill: #3498db; }
.stat-success { -fx-text-fill: #2ecc71; }
.stat-warning { -fx-text-fill: #f39c12; }
.stat-danger { -fx-text-fill: #e74c3c; }
EOF

echo "✅ styles.css created/updated"
echo ""

# =============================================================================
# 7. UPDATE MAIN CONTROLLER
# =============================================================================

echo "📁 7. Updating Main Controller..."
echo "--------------------------------"

if [ -f "src/main/java/com/gym/view/javafx/controller/MainController.java" ]; then
    cp src/main/java/com/gym/view/javafx/controller/MainController.java \
       src/main/java/com/gym/view/javafx/controller/MainController.java.bak
    echo "✅ Backup created: MainController.java.bak"
    
    # Add setUser method if not present
    if ! grep -q "setUser" src/main/java/com/gym/view/javafx/controller/MainController.java; then
        sed -i '/public void setGymController/i\
    public void setUser(com.gym.model.user.User user) {\
        if (userNameLabel != null) {\
            userNameLabel.setText(user.getName());\
        }\
        System.out.println("✅ User set in MainController: " + user.getName());\
    }' src/main/java/com/gym/view/javafx/controller/MainController.java
        echo "✅ Added setUser() method"
    fi
else
    echo "⚠️ MainController.java not found, skipping"
fi
echo ""

# =============================================================================
# 8. CREATE/UPDATE GYM APPLICATION
# =============================================================================

echo "📁 8. Creating/Updating Gym Application..."
echo "-----------------------------------------"

cat > src/main/java/com/gym/GymApplication.java << 'EOF'
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
        // Initialize the main controller
        gymController = new GymController();
        
        // Load test data if empty
        if (gymController.getDataManager().getProfiles().isEmpty()) {
            DataInitializer.initializeTestData(gymController.getDataManager());
        }
        
        // Load the login FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/gym/view/javafx/fxml/login-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 500);
        
        // Load CSS
        scene.getStylesheets().add(
            getClass().getResource("/com/gym/view/css/styles.css").toExternalForm()
        );
        
        // Get controller and pass dependencies
        LoginController loginController = loader.getController();
        loginController.setGymController(gymController);
        loginController.setStage(primaryStage);
        
        // Setup stage
        primaryStage.setTitle("🏋️ Gym Management System - Login");
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
EOF

echo "✅ GymApplication.java updated"
echo ""

# =============================================================================
# 9. CREATE PAYMENT CONTROLLER
# =============================================================================

echo "📁 9. Creating Payment Controller..."
echo "-----------------------------------"

mkdir -p src/main/java/com/gym/controller

cat > src/main/java/com/gym/controller/PaymentController.java << 'EOF'
package com.gym.controller;

import com.gym.model.Payment;
import com.gym.model.Profile;
import com.gym.persistence.DataManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Payment Controller - Handles payment processing
 */
public class PaymentController {
    
    private DataManager dataManager;
    
    public PaymentController(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    
    /**
     * Process a payment for a membership
     */
    public Payment processPayment(int profileId, double amount, String method) {
        Profile profile = dataManager.findProfileById(profileId);
        if (profile == null) {
            System.out.println("❌ Profile not found: " + profileId);
            return null;
        }
        
        // Validate amount
        if (amount <= 0) {
            System.out.println("❌ Invalid amount: " + amount);
            return null;
        }
        
        int paymentId = dataManager.getPayments().size() + 1000;
        String paymentDate = LocalDate.now().toString();
        
        Payment payment = new Payment(paymentId, amount, paymentDate, method, "Completed");
        dataManager.addPayment(payment);
        dataManager.saveAllData();
        
        // Generate receipt
        String receipt = generateReceipt(payment, profile);
        System.out.println(receipt);
        
        return payment;
    }
    
    /**
     * Process a refund
     */
    public boolean refundPayment(int paymentId) {
        for (Payment payment : dataManager.getPayments()) {
            if (payment.getPaymentId() == paymentId) {
                payment.setPaymentStatus("Refunded");
                dataManager.saveAllData();
                System.out.println("✅ Payment refunded: " + paymentId);
                return true;
            }
        }
        System.out.println("❌ Payment not found: " + paymentId);
        return false;
    }
    
    /**
     * Generate a receipt for a payment
     */
    public String generateReceipt(Payment payment, Profile profile) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=".repeat(50));
        receipt.append("\n📋 GYM MANAGEMENT SYSTEM - PAYMENT RECEIPT\n");
        receipt.append("=".repeat(50)).append("\n");
        receipt.append("Receipt #: ").append(payment.getPaymentId()).append("\n");
        receipt.append("Date: ").append(payment.getPaymentDate()).append("\n");
        receipt.append("Member: ").append(profile.getName()).append("\n");
        receipt.append("Member ID: ").append(profile.getProfileId()).append("\n");
        receipt.append("-".repeat(50)).append("\n");
        receipt.append("Amount: $").append(String.format("%.2f", payment.getAmount())).append("\n");
        receipt.append("Method: ").append(payment.getPaymentMethod()).append("\n");
        receipt.append("Status: ").append(payment.getPaymentStatus()).append("\n");
        receipt.append("=".repeat(50)).append("\n");
        receipt.append("Thank you for your business!\n");
        receipt.append("=".repeat(50));
        return receipt.toString();
    }
    
    /**
     * Get all payments for a profile
     */
    public List<Payment> getPaymentsForProfile(int profileId) {
        return dataManager.getPayments().stream()
            .filter(p -> p.getProfileId() == profileId)
            .collect(Collectors.toList());
    }
    
    /**
     * Get total payments for a profile
     */
    public double getTotalPaymentsForProfile(int profileId) {
        return getPaymentsForProfile(profileId).stream()
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    /**
     * Get total revenue from all payments
     */
    public double getTotalRevenue() {
        return dataManager.getPayments().stream()
            .filter(p -> "Completed".equalsIgnoreCase(p.getPaymentStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
    }
}
EOF

echo "✅ PaymentController.java created"
echo ""

# =============================================================================
# 10. CREATE TEST FILE
# =============================================================================

echo "📁 10. Creating Test File..."
echo "---------------------------"

mkdir -p src/test/java/com/gym/model

cat > src/test/java/com/gym/model/ProfileTest.java << 'EOF'
package com.gym.model;

import com.gym.model.membership.Basic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class ProfileTest {
    
    @Test
    public void testProfileCreation() {
        Profile profile = new Profile(1, "John Doe", "john@email.com", "555-1234", "123 Main St");
        assertEquals(1, profile.getProfileId());
        assertEquals("John Doe", profile.getName());
        assertEquals("john@email.com", profile.getEmail());
    }
    
    @Test
    public void testUpdateProfile() {
        Profile profile = new Profile(1, "John Doe", "john@email.com", "555-1234", "123 Main St");
        profile.updateProfile("Jane Doe", "jane@email.com", "555-5678", "456 Oak St");
        assertEquals("Jane Doe", profile.getName());
        assertEquals("jane@email.com", profile.getEmail());
    }
    
    @Test
    public void testMembershipValidation() {
        Basic membership = new Basic(1001, 49.99, "2026-01-01", "2026-12-31", "Active");
        assertTrue(membership.isValid());
        
        membership.setStatus("Inactive");
        assertFalse(membership.isValid());
        
        membership.setStatus("Active");
        membership.setExpiryDate("2025-12-31");
        assertFalse(membership.isValid());
    }
    
    @Test
    public void testMembershipRenewal() {
        Basic membership = new Basic(1001, 49.99, "2026-01-01", "2026-12-31", "Active");
        String oldExpiry = membership.getExpiryDate();
        membership.renew();
        assertNotEquals(oldExpiry, membership.getExpiryDate());
        assertTrue(membership.isValid());
    }
}
EOF

echo "✅ ProfileTest.java created"
echo ""

# =============================================================================
# 11. UPDATE README
# =============================================================================

echo "📁 11. Updating README..."
echo "------------------------"

if [ -f "README.md" ]; then
    cp README.md README.md.bak
    echo "✅ Backup created: README.md.bak"
    
    # Add JavaFX run instructions
    if ! grep -q "javafx:run" README.md; then
        cat >> README.md << 'EOF'

## 🖥️ Running the JavaFX GUI Application

### Run the GUI application
```bash
mvn clean compile
mvn javafx:run
>>EOF
