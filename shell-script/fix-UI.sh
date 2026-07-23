#!/bin/bash

# =============================================================================
# Script: fix-gymapplication.sh
# Purpose: Fix the setDataManager() error in GymApplication.java
# =============================================================================

set -e

echo "🔧 FIXING GYMAPPLICATION.JAVA"
echo "=============================="
echo ""

# =============================================================================
# COLOR CODES
# =============================================================================

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }
print_info() { echo -e "${BLUE}ℹ️ $1${NC}"; }

# =============================================================================
# BACKUP THE FILE
# =============================================================================

print_info "Step 1: Creating backup..."

if [ -f "src/main/java/com/gym/GymApplication.java" ]; then
    cp src/main/java/com/gym/GymApplication.java \
       src/main/java/com/gym/GymApplication.java.bak
    print_success "Backup created: GymApplication.java.bak"
else
    print_error "GymApplication.java not found!"
    exit 1
fi

# =============================================================================
# CREATE THE FIXED VERSION
# =============================================================================

print_info "Step 2: Creating fixed GymApplication.java..."

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
        Scene scene = new Scene(loader.load(), 400, 550);
        
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

print_success "GymApplication.java fixed"

# =============================================================================
# VERIFY THE FIX
# =============================================================================

print_info "Step 3: Verifying the fix..."

if grep -q "setGymController" src/main/java/com/gym/GymApplication.java; then
    print_success "✅ GymApplication.java now uses setGymController()"
else
    print_warning "⚠️ setGymController() not found in GymApplication.java"
fi

if ! grep -q "setDataManager" src/main/java/com/gym/GymApplication.java; then
    print_success "✅ setDataManager() removed from GymApplication.java"
else
    print_warning "⚠️ setDataManager() still present in GymApplication.java"
fi

# =============================================================================
# COMPILE TO VERIFY
# =============================================================================

print_info "Step 4: Compiling project..."

mvn clean compile -q

if [ $? -eq 0 ]; then
    print_success "Compilation successful!"
else
    print_error "Compilation failed. Please check the errors above."
    echo ""
    echo "Run this command to see the error:"
    echo "  mvn compile -e"
    exit 1
fi

# =============================================================================
# SUMMARY
# =============================================================================

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}✅ FIX COMPLETE!${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${BLUE}What was fixed:${NC}"
echo "  • Changed setDataManager() → setGymController()"
echo "  • Updated LoginController initialization"
echo "  • Removed incompatible method call"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Run: mvn clean compile"
echo "  2. Run: mvn javafx:run"
echo ""
echo -e "${GREEN}🎉 The application should now compile and run!${NC}"