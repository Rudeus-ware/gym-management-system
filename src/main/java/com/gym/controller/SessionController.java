package com.gym.view.controllers;

import com.gym.model.booking.Session;
import com.gym.persistence.DataManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class SessionController {
    
    @FXML private TableView<Session> sessionTable;
    @FXML private Label statusLabel;
    @FXML private Text totalSessions;
    @FXML private Text activeSessions;
    @FXML private Text endedSessions;
    
    private DataManager dataManager;
    private ObservableList<Session> sessionList;
    
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        loadSessions();
        updateStats();
    }
    
    @FXML
    public void initialize() {
        setupTableColumns();
        sessionList = FXCollections.observableArrayList();
        sessionTable.setItems(sessionList);
    }
    
    private void setupTableColumns() {
        TableColumn<Session, Integer> idCol = (TableColumn<Session, Integer>) sessionTable.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        
        TableColumn<Session, Integer> classCol = (TableColumn<Session, Integer>) sessionTable.getColumns().get(1);
        classCol.setCellValueFactory(new PropertyValueFactory<>("classId"));
        
        TableColumn<Session, String> dateCol = (TableColumn<Session, String>) sessionTable.getColumns().get(2);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sessionDate"));
        
        TableColumn<Session, String> startCol = (TableColumn<Session, String>) sessionTable.getColumns().get(3);
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        
        TableColumn<Session, String> endCol = (TableColumn