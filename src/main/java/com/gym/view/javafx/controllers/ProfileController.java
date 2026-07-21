package com.gym.view.javafx.controllers;

import com.gym.model.Profile;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Premium;
import com.gym.persistence.DataManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ProfileController {
    @FXML private TableView<Profile> profileTable;
    @FXML private TextField searchField;
    @FXML private Text totalMembers;
    @FXML private Text activeMembers;
    @FXML private Text inactiveMembers;
    
    private DataManager dataManager;
    private ObservableList<Profile> profileList;
    
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        loadProfiles();
        updateStats();
    }
    
    @FXML
    public void initialize() {
        // Set up the table columns
        profileTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("profileId"));
        profileTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        profileTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("email"));
        profileTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("phone"));
        profileTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("address"));
        
        // Custom cell for membership
        TableColumn<Profile, String> membershipColumn = (TableColumn<Profile, String>) profileTable.getColumns().get(5);
        membershipColumn.setCellValueFactory(cellData -> {
            Profile profile = cellData.getValue();
            if (profile.getMembership() != null) {
                return new SimpleStringProperty(
                    profile.getMembership().getClass().getSimpleName()
                );
            }
            return new SimpleStringProperty("None");
        });
        
        // Custom cell for actions
        TableColumn<Profile, Void> actionsColumn = (TableColumn<Profile, Void>) profileTable.getColumns().get(6);
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            
            {
                editBtn.setOnAction(e -> handleEdit(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> handleDelete(getTableRow().getItem()));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editBtn, deleteBtn));
                }
            }
        });
    }
    
    private void loadProfiles() {
        profileList = FXCollections.observableArrayList(dataManager.getProfiles());
        profileTable.setItems(profileList);
    }
    
    private void updateStats() {
        int total = profileList.size();
        int active = 0;
        int inactive = 0;
        
        for (Profile p : profileList) {
            if (p.getMembership() != null && p.getMembership().isValid()) {
                active++;
            } else {
                inactive++;
            }
        }
        
        totalMembers.setText(String.valueOf(total));
        activeMembers.setText(String.valueOf(active));
        inactiveMembers.setText(String.valueOf(inactive));
    }
    
    @FXML
    private void handleAddMember() {
        // Show add member dialog
        Dialog<Profile> dialog = new Dialog<>();
        dialog.setTitle("Add New Member");
        dialog.setHeaderText("Enter member details:");
        
        // Set up form
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
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);
        grid.add(new Label("Membership:"), 0, 4);
        grid.add(membershipType, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                int nextId = dataManager.getProfiles().size() + 1;
                Profile profile = new Profile(nextId, nameField.getText(), emailField.getText(), 
                                              phoneField.getText(), addressField.getText());
                
                // Add membership
                String type = membershipType.getValue();
                if (type != null) {
                    switch (type) {
                        case "Basic":
                            profile.setMembership(new Basic(nextId + 1000, 49.99, 
                                "2026-01-01", "2026-12-31", "Active"));
                            break;
                        case "Premium":
                            profile.setMembership(new Premium(nextId + 2000, 99.99, 
                                "2026-01-01", "2026-12-31", "Active", "VIP Access"));
                            break;
                        case "Family":
                            profile.setMembership(new Family(nextId + 3000, 69.99, 
                                "2026-01-01", "2026-12-31", "Active", 2));
                            break;
                    }
                }
                
                return profile;
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(profile -> {
            dataManager.addProfile(profile);
            loadProfiles();
            updateStats();
        });
    }
    
    @FXML
    private void handleEdit(Profile profile) {
        if (profile != null) {
            // Show edit dialog
            Dialog<Profile> dialog = new Dialog<>();
            dialog.setTitle("Edit Member");
            dialog.setHeaderText("Edit member details:");
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            TextField nameField = new TextField(profile.getName());
            TextField emailField = new TextField(profile.getEmail());
            TextField phoneField = new TextField(profile.getPhone());
            TextField addressField = new TextField(profile.getAddress());
            
            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(emailField, 1, 1);
            grid.add(new Label("Phone:"), 0, 2);
            grid.add(phoneField, 1, 2);
            grid.add(new Label("Address:"), 0, 3);
            grid.add(addressField, 1, 3);
            
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    profile.updateProfile(nameField.getText(), emailField.getText(), 
                                         phoneField.getText(), addressField.getText());
                    return profile;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(p -> {
                dataManager.saveAllData();
                loadProfiles();
                updateStats();
            });
        }
    }
    
    @FXML
    private void handleDelete(Profile profile) {
        if (profile != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Member");
            alert.setHeaderText("Delete " + profile.getName() + "?");
            alert.setContentText("This action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    dataManager.removeProfile(profile.getProfileId());
                    loadProfiles();
                    updateStats();
                }
            });
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadProfiles();
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
        profileTable.setItems(filtered);
    }
    
    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadProfiles();
    }
    
    @FXML
    private void handleRefresh() {
        dataManager.saveAllData();
        loadProfiles();
        updateStats();
    }
}