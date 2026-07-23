#!/bin/bash

# =============================================================================
# Script: fix-gymcontroller-scope.sh
# Purpose: Fix the "gymController cannot be resolved" error
# =============================================================================

set -e

echo "🔧 FIXING GYMCONTROLLER SCOPE ERROR"
echo "==================================="
echo ""

# =============================================================================
# COLOR CODES
# =============================================================================

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }
print_info() { echo -e "${CYAN}ℹ️ $1${NC}"; }
print_header() { echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; echo -e "${CYAN}▶ $1${NC}"; echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }

# =============================================================================
# STEP 1: LOCATE THE FILE
# =============================================================================

print_header "STEP 1: Locating GymApplication.java"

GYM_APP_FILE=""

if [ -f "src/main/java/com/gym/GymApplication.java" ]; then
    GYM_APP_FILE="src/main/java/com/gym/GymApplication.java"
elif [ -f "src/main/java/com/gym/view/javafx/controller/GymApplication.java" ]; then
    GYM_APP_FILE="src/main/java/com/gym/view/javafx/controller/GymApplication.java"
fi

if [ -n "$GYM_APP_FILE" ]; then
    print_success "Found GymApplication.java at: $GYM_APP_FILE"
else
    print_error "GymApplication.java not found!"
    exit 1
fi

# =============================================================================
# STEP 2: BACKUP
# =============================================================================

print_header "STEP 2: Creating Backup"

BACKUP_FILE="${GYM_APP_FILE}.bak.$(date +%Y%m%d_%H%M%S)"
cp "$GYM_APP_FILE" "$BACKUP_FILE"
print_success "Backup created: $BACKUP_FILE"

# =============================================================================
# STEP 3: CREATE FIXED VERSION
# =============================================================================

print_header "STEP 3: Creating Fixed GymApplication.java"

cat > "$GYM_APP_FILE" << 'EOF'
package com.gym;

import com.gym.controller.GymController;
import com.gym.persistence.DataInitializer;
import com.gym.view.javafx.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymApplication extends Application {
    
    // ✅ Declare as class-level field
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
        
        // Get LoginController and set dependencies
        LoginController loginController = loader.getController();
        loginController.setGymController(gymController);
        loginController.setStage(primaryStage);
        
        // Setup stage
        primaryStage.setTitle("🏋️ Gym Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Save on close
        primaryStage.setOnCloseRequest(event -> {
            if (gymController != null) {
                gymController.saveAllData();
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
EOF

print_success "GymApplication.java fixed"

# =============================================================================
# STEP 4: VERIFY
# =============================================================================

print_header "STEP 4: Verifying Fix"

if grep -q "private GymController gymController;" "$GYM_APP_FILE"; then
    print_success "✅ gymController declared as class field"
else
    print_error "❌ gymController not declared as class field"
fi

if grep -q "gymController = new GymController();" "$GYM_APP_FILE"; then
    print_success "✅ gymController initialized in start()"
else
    print_error "❌ gymController not initialized"
fi

if grep -q "loginController.setGymController(gymController)" "$GYM_APP_FILE"; then
    print_success "✅ setGymController() called correctly"
else
    print_error "❌ setGymController() not called correctly"
fi

# =============================================================================
# STEP 5: COMPILE
# =============================================================================

print_header "STEP 5: Compiling Project"

mvn clean compile -q

if [ $? -eq 0 ]; then
    print_success "✅ Compilation successful!"
else
    print_error "❌ Compilation failed"
    echo ""
    echo "Run this command to see the error:"
    echo "  mvn compile -e"
    exit 1
fi

# =============================================================================
# SUMMARY
# =============================================================================

print_header "FIX COMPLETE"

echo -e "${GREEN}✅ All errors resolved!${NC}"
echo ""
echo -e "${BLUE}What was fixed:${NC}"
echo "  1. Declared gymController as a class-level field"
echo "  2. Removed local variable declaration"
echo "  3. Fixed variable scope issue"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Run: mvn clean compile"
echo "  2. Run: mvn javafx:run"
echo ""
echo -e "${GREEN}🚀 Your application is ready!${NC}"