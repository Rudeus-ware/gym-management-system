package com.gym.view.javafx.controller;

import com.gym.controller.AdminController;
import com.gym.model.Profile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Profile UI Controller - Handles profile management UI
 * THIS IS A UI CONTROLLER - Uses JavaFX
 */
public class ProfileController {
    
    @FXML private TableView<Profile> profileTable;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;
    
    private AdminController adminController;
    private ObservableList<Profile> profileList;
    
    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
        loadProfiles();
    }
    
    @FXML
    public void initialize() {
        setupTableColumns();
        profileList = FXCollections.observableArrayList();
        profileTable.setItems(profileList);
    }
    
    private void setupTableColumns() {
        TableColumn<Profile, Integer> idCol = (TableColumn<Profile, Integer>) profileTable.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("profileId"));
        
        TableColumn<Profile, String> nameCol = (TableColumn<Profile, String>) profileTable.getColumns().get(1);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Profile, String> emailCol = (TableColumn<Profile, String>) profileTable.getColumns().get(2);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<Profile, String> phoneCol = (TableColumn<Profile, String>) profileTable.getColumns().get(3);
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        TableColumn<Profile, String> addressCol = (TableColumn<Profile, String>) profileTable.getColumns().get(4);
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        
        TableColumn<Profile, String> membershipCol = (TableColumn<Profile, String>) profileTable.getColumns().get(5);
        membershipCol.setCellValueFactory(cellData -> {
            Profile p = cellData.getValue();
            if (p.getMembership() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    p.getMembership().getClass().getSimpleName()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("None");
        });
        
        // Actions column
        TableColumn<Profile, Void> actionsCol = (TableColumn<Profile, Void>) profileTable.getColumns().get(6);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox container = new HBox(5, editBtn, deleteBtn);
            
            {
                editBtn.setOnAction(e -> handleEdit(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> handleDelete(getTableRow().getItem()));
                container.setPadding(new Insets(5, 0, 5, 0));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }
    
    public void loadProfiles() {
        if (adminController != null) {
            profileList.clear();
            profileList.addAll(adminController.getAllMembers());
        }
    }
    
    @FXML
    private void handleAddMember() {
        Dialog<Profile> dialog = createProfileDialog("Add Member", null);
        dialog.showAndWait().ifPresent(profile -> {
            adminController.createMember(
                profile.getName(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getAddress(),
                "Basic"  // Default
            );
            loadProfiles();
            statusLabel.setText("✅ Member added: " + profile.getName());
        });
    }
    
    private void handleEdit(Profile profile) {
        if (profile == null) return;
        Dialog<Profile> dialog = createProfileDialog("Edit Member", profile);
        dialog.showAndWait().ifPresent(updatedProfile -> {
            loadProfiles();
            statusLabel.setText("✅ Member updated: " + updatedProfile.getName());
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
                adminController.removeMember(profile.getProfileId());
                loadProfiles();
                statusLabel.setText("🗑️ Member deleted: " + profile.getName());
            }
        });
    }
    
    private Dialog<Profile> createProfileDialog(String title, Profile existingProfile) {
        Dialog<Profile> dialog = new Dialog<>();
        dialog.setTitle(title);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        
        if (existingProfile != null) {
            nameField.setText(existingProfile.getName());
            emailField.setText(existingProfile.getEmail());
            phoneField.setText(existingProfile.getPhone());
            addressField.setText(existingProfile.getAddress());
        }
        
        int row = 0;
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row++);
        grid.add(new Label("Phone:"), 0, row);
        grid.add(phoneField, 1, row++);
        grid.add(new Label("Address:"), 0, row);
        grid.add(addressField, 1, row++);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (existingProfile != null) {
                    // Update existing
                    existingProfile.updateProfile(
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText()
                    );
                    return existingProfile;
                } else {
                    // Create new
                    return new Profile(
                        0, // ID will be assigned
                        nameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        addressField.getText()
                    );
                }
            }
            return null;
        });
        
        return dialog;
    }
}