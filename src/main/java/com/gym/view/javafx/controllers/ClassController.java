package com.gym.view.controllers;

import com.gym.model.classes.GymClass;
import com.gym.model.classes.Yoga;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
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

import java.util.Optional;

public class ClassController {
    
    @FXML private TableView<GymClass> classTable;
    @FXML private TextField searchField;
    @FXML private Text totalClasses;
    @FXML private Text activeClasses;
    @FXML private Text fullClasses;
    @FXML private Label statusLabel;
    
    private DataManager dataManager;
    private ObservableList<GymClass> classList;
    
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        loadClasses();
        updateStats();
    }
    
    @FXML
    public void initialize() {
        setupTableColumns();
        classList = FXCollections.observableArrayList();
        classTable.setItems(classList);
    }
    
    private void setupTableColumns() {
        TableColumn<GymClass, Integer> idCol = (TableColumn<GymClass, Integer>) classTable.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("classId"));
        
        TableColumn<GymClass, String> nameCol = (TableColumn<GymClass, String>) classTable.getColumns().get(1);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("className"));
        
        TableColumn<GymClass, String> typeCol = (TableColumn<GymClass, String>) classTable.getColumns().get(2);
        typeCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        
        TableColumn<GymClass, String> scheduleCol = (TableColumn<GymClass, String>) classTable.getColumns().get(3);
        scheduleCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        
        TableColumn<GymClass, String> trainerCol = (TableColumn<GymClass, String>) classTable.getColumns().get(4);
        trainerCol.setCellValueFactory(new PropertyValueFactory<>("trainer"));
        
        TableColumn<GymClass, String> capacityCol = (TableColumn<GymClass, String>) classTable.getColumns().get(5);
        capacityCol.setCellValueFactory(cellData -> {
            GymClass gc = cellData.getValue();
            return new SimpleStringProperty(
                gc.getCurrentBookings() + "/" + gc.getCapacity()
            );
        });
        
        TableColumn<GymClass, Void> actionsCol = (TableColumn<GymClass, Void>) classTable.getColumns().get(6);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button viewBtn = new Button("👁️ View");
            private final Button deleteBtn = new Button("🗑️ Delete");
            private final HBox container = new HBox(5, viewBtn, deleteBtn);
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                viewBtn.setOnAction(e -> handleViewClass(getTableRow().getItem()));
                
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> handleDeleteClass(getTableRow().getItem()));
                
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
    
    public void refreshTable() {
        loadClasses();
        updateStats();
    }
    
    private void loadClasses() {
        if (dataManager != null) {
            classList.clear();
            classList.addAll(dataManager.getGymClasses());
        }
    }
    
    private void updateStats() {
        if (totalClasses == null) return;
        
        int total = classList.size();
        int active = 0;
        int full = 0;
        
        for (GymClass gc : classList) {
            if (gc.isFull()) {
                full++;
            }
            if (gc.getCurrentBookings() < gc.getCapacity()) {
                active++;
            }
        }
        
        totalClasses.setText(String.valueOf(total));
        activeClasses.setText(String.valueOf(active));
        fullClasses.setText(String.valueOf(full));
    }
    
    @FXML
    private void handleAddClass() {
        Dialog<GymClass> dialog = createClassDialog("Add New Class", "Enter class details:", null);
        
        dialog.showAndWait().ifPresent(gymClass -> {
            dataManager.addGymClass(gymClass);
            refreshTable();
            statusLabel.setText("✅ Class added: " + gymClass.getClassName());
        });
    }
    
    private void handleViewClass(GymClass gymClass) {
        if (gymClass == null) return;
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Class Details");
        alert.setHeaderText(gymClass.getClassName());
        alert.setContentText(gymClass.getClassDetails());
        alert.showAndWait();
    }
    
    private void handleDeleteClass(GymClass gymClass) {
        if (gymClass == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Class");
        alert.setHeaderText("Delete " + gymClass.getClassName() + "?");
        alert.setContentText("This will remove all associated sessions and bookings.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataManager.removeGymClass(gymClass.getClassId());
                refreshTable();
                statusLabel.setText("🗑️ Class deleted: " + gymClass.getClassName());
            }
        });
    }
    
    private Dialog<GymClass> createClassDialog(String title, String header, GymClass existingClass) {
        Dialog<GymClass> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        TextField scheduleField = new TextField();
        TextField capacityField = new TextField();
        TextField trainerField = new TextField();
        ComboBox<String> classType = new ComboBox<>();
        classType.getItems().addAll("Yoga", "Spin", "Strength");
        
        // Additional fields for specific types
        TextField styleField = new TextField();
        TextField difficultyField = new TextField();
        TextField intensityField = new TextField();
        TextField focusField = new TextField();
        
        // Hide additional fields initially
        styleField.setVisible(false);
        difficultyField.setVisible(false);
        intensityField.setVisible(false);
        focusField.setVisible(false);
        
        if (existingClass != null) {
            nameField.setText(existingClass.getClassName());
            scheduleField.setText(existingClass.getSchedule());
            capacityField.setText(String.valueOf(existingClass.getCapacity()));
            trainerField.setText(existingClass.getTrainer());
            classType.setValue(existingClass.getClass().getSimpleName());
            
            if (existingClass instanceof Yoga) {
                Yoga yoga = (Yoga) existingClass;
                styleField.setText(yoga.getYogaStyle());
                difficultyField.setText(yoga.getDifficulty());
                styleField.setVisible(true);
                difficultyField.setVisible(true);
            } else if (existingClass instanceof Spin) {
                Spin spin = (Spin) existingClass;
                intensityField.setText(spin.getIntensity());
                intensityField.setVisible(true);
            } else if (existingClass instanceof Strength) {
                Strength strength = (Strength) existingClass;
                focusField.setText(strength.getFocusArea());
                focusField.setVisible(true);
            }
        }
        
        // Show/hide additional fields based on class type
        classType.setOnAction(e -> {
            String type = classType.getValue();
            styleField.setVisible("Yoga".equals(type));
            difficultyField.setVisible("Yoga".equals(type));
            intensityField.setVisible("Spin".equals(type));
            focusField.setVisible("Strength".equals(type));
        });
        
        int row = 0;
        grid.add(new Label("Class Name:"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("Schedule:"), 0, row);
        grid.add(scheduleField, 1, row++);
        grid.add(new Label("Capacity:"), 0, row);
        grid.add(capacityField, 1, row++);
        grid.add(new Label("Trainer:"), 0, row);
        grid.add(trainerField, 1, row++);
        grid.add(new Label("Class Type:"), 0, row);
        grid.add(classType, 1, row++);
        
        // Additional fields
        grid.add(new Label("Yoga Style:"), 0, row);
        grid.add(styleField, 1, row++);
        grid.add(new Label("Difficulty:"), 0, row);
        grid.add(difficultyField, 1, row++);
        grid.add(new Label("Intensity:"), 0, row);
        grid.add(intensityField, 1, row++);
        grid.add(new Label("Focus Area:"), 0, row);
        grid.add(focusField, 1, row++);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                int nextId = existingClass != null ? 
                    existingClass.getClassId() : 
                    dataManager.getGymClasses().size() + 101;
                    
                String type = classType.getValue();
                if (type == null) return null;
                
                try {
                    int capacity = Integer.parseInt(capacityField.getText());
                    
                    switch (type) {
                        case "Yoga":
                            return new Yoga(
                                nextId,
                                nameField.getText(),
                                scheduleField.getText(),
                                capacity,
                                trainerField.getText(),
                                styleField.getText(),
                                difficultyField.getText()
                            );
                        case "Spin":
                            return new Spin(
                                nextId,
                                nameField.getText(),
                                scheduleField.getText(),
                                capacity,
                                trainerField.getText(),
                                intensityField.getText(),
                                45,
                                "EDM"
                            );
                        case "Strength":
                            return new Strength(
                                nextId,
                                nameField.getText(),
                                scheduleField.getText(),
                                capacity,
                                trainerField.getText(),
                                focusField.getText(),
                                "Intermediate"
                            );
                        default:
                            return null;
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Please enter a valid number for capacity");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
        
        return dialog;
    }
}