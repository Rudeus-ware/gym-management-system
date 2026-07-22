#!/bin/bash

# =============================================================================
# Script: fix-profile-controller.sh
# Purpose: Fix all compilation errors in ProfileController.java
# =============================================================================

set -e  # Exit immediately if a command exits with a non-zero status.

echo "🚀 Starting ProfileController fix..."
echo "====================================="

# Path to the ProfileController file
PROFILE_PATH="src/main/java/com/gym/view/javafx/controller/ProfileController.java"

# 1. Check if the ProfileController file exists
if [ ! -f "$PROFILE_PATH" ]; then
    echo "❌ Error: ProfileController not found at $PROFILE_PATH"
    echo "   Please ensure you are running this script from the project root directory."
    exit 1
fi

echo "✅ Found ProfileController at $PROFILE_PATH"

# 2. Create a backup
echo "📁 Creating backup..."
cp "$PROFILE_PATH" "$PROFILE_PATH.bak"
echo "✅ Backup created: $PROFILE_PATH.bak"

# 3. Read the current file content
echo "✏️ Analyzing and fixing ProfileController..."

# Create the fixed version with proper structure
cat > "$PROFILE_PATH" << 'EOF'
package com.gym.view.javafx.controller;

import com.gym.controller.GymController;
import com.gym.model.Profile;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import com.gym.persistence.DataManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.time.LocalDate;

/**
 * Profile UI Controller - Handles profile management UI
 */
public class ProfileController {
    
    // ===== FXML INJECTED FIELDS =====
    @FXML private TableView<Profile> profileTable;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    @FXML private Text totalMembers;
    @FXML private Text activeMembers;
    @FXML private Text inactiveMembers;
    @FXML private Text basicCount;
    @FXML private Text premiumCount;
    @FXML private Text familyCount;
    
    // ===== INSTANCE FIELDS =====
    private GymController gymController;
    private DataManager dataManager;
    private ObservableList<Profile> profileList;
    
    // ===== SETTERS =====
    public void setGymController(GymController gymController) {
        this.gymController = gymController;
        if (gymController != null) {
            this.dataManager = gymController.getDataManager();
            loadProfiles();
            updateStats();
        }
        System.out.println("✅ GymController set in ProfileController");
    }
    
    // ===== INITIALIZATION =====
    @FXML
    public void initialize() {
        setupTableColumns();
        profileList = FXCollections.observableArrayList();
        profileTable.setItems(profileList);
        statusLabel.setText("✅ Profile Controller ready");
    }
    
    // ===== TABLE SETUP =====
    private void setupTableColumns() {
        // ID Column
        TableColumn<Profile, Integer> idCol = (TableColumn<Profile, Integer>) profileTable.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("profileId"));
        
        // Name Column
        TableColumn<Profile, String> nameCol = (TableColumn<Profile, String>) profileTable.getColumns().get(1);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        // Email Column
        TableColumn<Profile, String> emailCol = (TableColumn<Profile, String>) profileTable.getColumns().get(2);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Phone Column
        TableColumn<Profile, String> phoneCol = (TableColumn<Profile, String>) profileTable.getColumns().get(3);
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        // Address Column
        TableColumn<Profile, String> addressCol = (TableColumn<Profile, String>) profileTable.getColumns().get(4);
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        
        // Membership Column (custom)
        TableColumn<Profile, String> membershipCol = (TableColumn<Profile, String>) profileTable.getColumns().get(5);
        membershipCol.setCellValueFactory(cellData -> {
            Profile p = cellData.getValue();
            if (p.getMembership() != null) {
                return new SimpleStringProperty(p.getMembership().getClass().getSimpleName());
            }
            return new SimpleStringProperty("None");
        });
        
        // Actions Column (custom with buttons)
        TableColumn<Profile, Void> actionsCol = (TableColumn<Profile, Void>) profileTable.getColumns().get(6);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final HBox actionContainer = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                editBtn.setOnAction(e -> handleEdit(getTableRow().getItem()));
                
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> handleDelete(getTableRow().getItem()));
                
                actionContainer.setPadding(new Insets(5, 0, 5, 0));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(actionContainer);
                }
            }
        });
    }
    
    // ===== DATA LOADING =====
    public void refreshTable() {
        loadProfiles();
        updateStats();
        statusLabel.setText("✅ Table refreshed");
    }
    
    public void loadProfiles() {
        if (dataManager != null) {
            profileList.clear();
            profileList.addAll(dataManager.getProfiles());
        } else if (gymController != null) {
            this.dataManager = gymController.getDataManager();
            profileList.clear();
            profileList.addAll(dataManager.getProfiles());
        }
    }
    
    private void updateStats() {
        if (totalMembers == null) return;
        
        int total = profileList.size();
        int active = 0;
        int inactive = 0;
        int basic = 0;
        int premium = 0;
        int family = 0;
        
        for (Profile p : profileList) {
            Membership membership = p.getMembership();
            if (membership != null) {
                if (membership.isValid()) {
                    active++;
                } else {
                    inactive++;
                }
                
                String type = membership.getClass().getSimpleName();
                switch (type) {
                    case "Basic":
                        basic++;
                        break;
                    case "Premium":
                        premium++;
                        break;
                    case "Family":
                        family++;
                        break;
                }
            } else {
                inactive++;
            }
        }
        
        totalMembers.setText(String.valueOf(total));
        activeMembers.setText(String.valueOf(active));
        inactiveMembers.setText(String.valueOf(inactive));
        basicCount.setText(String.valueOf(basic));
        premiumCount.setText(String.valueOf(premium));
        familyCount.setText(String.valueOf(family));
    }
    
    // ===== SEARCH METHODS =====
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            loadProfiles();
            statusLabel.setText("✅ Search cleared");
            return;
        }
        
        ObservableList<Profile> filtered = FXCollections.observableArrayList();
        for (Profile p : dataManager.getProfiles()) {
            if (p.getName().toLowerCase().contains(searchText) ||
                p.getEmail().toLowerCase().contains(searchText) ||
                p.getPhone().contains(searchText)) {
                filtered.add(p);
            }
        }
        profileList.clear();
        profileList.addAll(filtered);
        
        if (filtered.isEmpty()) {
            statusLabel.setText("❌ No members found matching: " + searchText);
        } else {
            statusLabel.setText("✅ Found " + filtered.size() + " members");
        }
    }
    
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadProfiles();
        statusLabel.setText("✅ Search cleared");
    }
    
    // ===== CRUD OPERATIONS =====
    @FXML
    private void handleAddMember() {
        Dialog<Profile> dialog = createProfileDialog("Add New Member", "Enter member details:", null);
        
        dialog.showAndWait().ifPresent(profile -> {
            if (gymController != null && gymController.getAdminController() != null) {
                gymController.getAdminController().createMember(
                    profile.getName(),
                    profile.getEmail(),
                    profile.getPhone(),
                    profile.getAddress(),
                    "Basic"
                );
                loadProfiles();
                updateStats();
                statusLabel.setText("✅ Member added: " + profile.getName());
            }
        });
    }
    
    private void handleEdit(Profile profile) {
        if (profile == null) return;
        
        Dialog<Profile> dialog = createProfileDialog("Edit Member", "Edit member details:", profile);
        
        dialog.showAndWait().ifPresent(updatedProfile -> {
            if (gymController != null && gymController.getAdminController() != null) {
                gymController.getAdminController().updateMember(
                    updatedProfile.getProfileId(),
                    updatedProfile.getName(),
                    updatedProfile.getEmail(),
                    updatedProfile.getPhone(),
                    updatedProfile.getAddress()
                );
                loadProfiles();
                updateStats();
                statusLabel.setText("✅ Member updated: " + updatedProfile.getName());
            }
        });
    }
    
    private void handleDelete(Profile profile) {
        if (profile == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Member");
        alert.setHeaderText("Delete " + profile.getName() + "?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (gymController != null && gymController.getAdminController() != null) {
                    gymController.getAdminController().removeMember(profile.getProfileId());
                    loadProfiles();
                    updateStats();
                    statusLabel.setText("🗑️ Member deleted: " + profile.getName());
                }
            }
        });
    }
    
    @FXML
    private void handleRefresh() {
        if (gymController != null) {
            gymController.saveAllData();
            loadProfiles();
            updateStats();
            statusLabel.setText("✅ Data refreshed");
        }
    }
    
    // ===== DIALOG BUILDER =====
    private Dialog<Profile> createProfileDialog(String title, String header, Profile existingProfile) {
        Dialog<Profile> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        
        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        ComboBox<String> membershipType = new ComboBox<>();
        membershipType.getItems().addAll("Basic", "Premium", "Family");
        
        // If editing, pre-fill fields
        if (existingProfile != null) {
            nameField.setText(existingProfile.getName());
            emailField.setText(existingProfile.getEmail());
            phoneField.setText(existingProfile.getPhone());
            addressField.setText(existingProfile.getAddress());
            if (existingProfile.getMembership() != null) {
                membershipType.setValue(existingProfile.getMembership().getClass().getSimpleName());
            }
        }
        
        // Add fields to grid
        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        grid.add(new Label("Phone:"), 0, row);
        grid.add(phoneField, 1, row++);
        grid.add(new Label("Address:"), 0, row);
        grid.add(addressField, 1, row++);
        grid.add(new Label("Membership:"), 0, row);
        grid.add(membershipType, 1, row++);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Enable/disable OK button based on validation
        final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Please fill in all fields");
                alert.showAndWait();
                event.consume();
            }
        });
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                int nextId = existingProfile != null ? 
                    existingProfile.getProfileId() : 
                    (dataManager != null ? dataManager.getProfiles().size() + 1 : 1);
                    
                Profile profile = new Profile(
                    nextId,
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText()
                );
                
                // Add membership if new
                if (existingProfile == null) {
                    String type = membershipType.getValue();
                    if (type != null) {
                        String startDate = LocalDate.now().toString();
                        String expiryDate = LocalDate.now().plusYears(1).toString();
                        int membershipId = dataManager != null ? dataManager.getMemberships().size() + 1000 : 1000;
                        
                        Membership membership = null;
                        switch (type) {
                            case "Basic":
                                membership = new Basic(membershipId, 49.99, startDate, expiryDate, "Active");
                                break;
                            case "Premium":
                                membership = new Premium(membershipId, 99.99, startDate, expiryDate, "Active", "VIP Access");
                                break;
                            case "Family":
                                membership = new Family(membershipId, 69.99, startDate, expiryDate, "Active", 2);
                                break;
                        }
                        profile.setMembership(membership);
                    }
                } else {
                    // Keep existing membership
                    profile.setMembership(existingProfile.getMembership());
                }
                
                return profile;
            }
            return null;
        });
        
        return dialog;
    }
}
EOF

echo "✅ ProfileController.java has been completely rewritten with fixes."

# 4. Compile to verify
echo "⚙️ Compiling the project to verify fixes..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "🎉 Compilation successful! All errors have been fixed."
    echo "✅ Backup saved at: $PROFILE_PATH.bak (you can delete it later)"
else
    echo "❌ Compilation still has errors. Please check the output above."
    echo "   You can restore the backup using: cp $PROFILE_PATH.bak $PROFILE_PATH"
    exit 1
fi

echo "====================================="
echo "✅ Script execution completed successfully!"