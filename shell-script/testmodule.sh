#!/bin/bash

# =============================================================================
# Script: diagnose-ui.sh
# Purpose: Diagnose UI build failures by testing each component individually
# =============================================================================

set -e

echo "🔍 GYM MANAGEMENT SYSTEM - UI DIAGNOSTIC TOOL"
echo "============================================="
echo ""
echo "This script will test each component of your UI to find the failure point."
echo ""

# =============================================================================
# COLOR CODES
# =============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# =============================================================================
# HELPER FUNCTIONS
# =============================================================================

print_header() {
    echo ""
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${CYAN}▶ $1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️ $1${NC}"
}

print_info() {
    echo -e "${CYAN}ℹ️ $1${NC}"
}

# =============================================================================
# TEST 1: CHECK PROJECT STRUCTURE
# =============================================================================

print_header "TEST 1: Checking Project Structure"

# Check critical directories
DIRS=(
    "src/main/java/com/gym"
    "src/main/java/com/gym/controller"
    "src/main/java/com/gym/model"
    "src/main/java/com/gym/view/javafx/controller"
    "src/main/resources/com/gym/view/javafx/fxml"
    "src/main/resources/com/gym/view/css"
)

MISSING_DIRS=()
for dir in "${DIRS[@]}"; do
    if [ -d "$dir" ]; then
        print_success "Found: $dir"
    else
        print_error "Missing: $dir"
        MISSING_DIRS+=("$dir")
    fi
done

if [ ${#MISSING_DIRS[@]} -ne 0 ]; then
    print_error "Missing directories detected. Create them and try again."
    exit 1
fi

echo ""

# =============================================================================
# TEST 2: CHECK CRITICAL JAVA FILES
# =============================================================================

print_header "TEST 2: Checking Critical Java Files"

FILES=(
    "src/main/java/com/gym/GymApplication.java"
    "src/main/java/com/gym/controller/GymController.java"
    "src/main/java/com/gym/controller/AdminController.java"
    "src/main/java/com/gym/controller/LoginController.java"
    "src/main/java/com/gym/view/javafx/controller/LoginController.java"
    "src/main/java/com/gym/view/javafx/controller/MainController.java"
    "src/main/java/com/gym/view/javafx/controller/RegisterController.java"
)

MISSING_FILES=()
for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        print_success "Found: $file"
    else
        print_error "Missing: $file"
        MISSING_FILES+=("$file")
    fi
done

if [ ${#MISSING_FILES[@]} -ne 0 ]; then
    print_error "Missing files detected. Create them and try again."
    exit 1
fi

echo ""

# =============================================================================
# TEST 3: CHECK CRITICAL FXML FILES
# =============================================================================

print_header "TEST 3: Checking Critical FXML Files"

FXML_FILES=(
    "src/main/resources/com/gym/view/javafx/fxml/login-view.fxml"
    "src/main/resources/com/gym/view/javafx/fxml/register-view.fxml"
    "src/main/resources/com/gym/view/javafx/fxml/main-view.fxml"
    "src/main/resources/com/gym/view/javafx/fxml/dashboard-view.fxml"
    "src/main/resources/com/gym/view/javafx/fxml/profile-view.fxml"
)

MISSING_FXML=()
for fxml in "${FXML_FILES[@]}"; do
    if [ -f "$fxml" ]; then
        print_success "Found: $fxml"
    else
        print_error "Missing: $fxml"
        MISSING_FXML+=("$fxml")
    fi
done

if [ ${#MISSING_FXML[@]} -ne 0 ]; then
    print_error "Missing FXML files detected."
fi

echo ""

# =============================================================================
# TEST 4: CHECK FXML CONTROLLER REFERENCES
# =============================================================================

print_header "TEST 4: Checking FXML Controller References"

for fxml in "${FXML_FILES[@]}"; do
    if [ -f "$fxml" ]; then
        # Extract controller class from FXML
        CONTROLLER=$(grep -o 'fx:controller="[^"]*"' "$fxml" | head -1 | sed 's/fx:controller="//' | sed 's/"//')
        if [ -n "$CONTROLLER" ]; then
            # Convert to Java file path
            JAVA_PATH=$(echo "$CONTROLLER" | tr '.' '/' | sed 's/$/.java/')
            JAVA_PATH="src/main/java/$JAVA_PATH"
            if [ -f "$JAVA_PATH" ]; then
                print_success "FXML: $fxml → Controller found: $CONTROLLER"
            else
                print_error "FXML: $fxml → Controller NOT found: $CONTROLLER"
            fi
        else
            print_warning "FXML: $fxml → No controller reference found"
        fi
    fi
done

echo ""

# =============================================================================
# TEST 5: CHECK POM.XML CONFIGURATION
# =============================================================================

print_header "TEST 5: Checking pom.xml Configuration"

if [ -f "pom.xml" ]; then
    # Check JavaFX version
    JAVAFX_VERSION=$(grep -o '<javafx.version>[^<]*</javafx.version>' pom.xml | sed 's/<javafx.version>//' | sed 's/<\/javafx.version>//')
    if [ -n "$JAVAFX_VERSION" ]; then
        print_success "JavaFX version: $JAVAFX_VERSION"
    else
        print_warning "JavaFX version not explicitly set"
    fi
    
    # Check for JavaFX dependencies
    if grep -q "javafx-controls" pom.xml; then
        print_success "javafx-controls dependency found"
    else
        print_error "javafx-controls dependency missing"
    fi
    
    if grep -q "javafx-fxml" pom.xml; then
        print_success "javafx-fxml dependency found"
    else
        print_error "javafx-fxml dependency missing"
    fi
    
    # Check main class in plugin configuration
    MAIN_CLASS=$(grep -o '<mainClass>[^<]*</mainClass>' pom.xml | head -1 | sed 's/<mainClass>//' | sed 's/<\/mainClass>//')
    if [ -n "$MAIN_CLASS" ]; then
        print_success "Main class configured: $MAIN_CLASS"
    else
        print_warning "Main class not configured in javafx-maven-plugin"
    fi
else
    print_error "pom.xml not found"
    exit 1
fi

echo ""

# =============================================================================
# TEST 6: TEST SIMPLE JAVA APPLICATION (NO FXML)
# =============================================================================

print_header "TEST 6: Testing Minimal JavaFX Application"

# Create a minimal test
cat > src/main/java/com/gym/MinimalTest.java << 'EOF'
package com.gym;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MinimalTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("🏋️ JavaFX Test - SUCCESS!");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: green;");
        
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 300);
        
        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
EOF

print_info "Created MinimalTest.java"
print_info "Attempting to run MinimalTest..."
echo ""

# Try to compile and run MinimalTest
if mvn clean compile -q; then
    print_success "Compilation successful"
else
    print_error "Compilation failed"
    exit 1
fi

# Run with timeout (5 seconds)
timeout 5 mvn exec:java -Dexec.mainClass="com.gym.MinimalTest" 2>/dev/null &
PID=$!
sleep 2
if kill -0 $PID 2>/dev/null; then
    print_success "MinimalTest runs successfully!"
    kill $PID 2>/dev/null
    MINIMAL_PASSED=true
else
    print_error "MinimalTest failed to run"
    MINIMAL_PASSED=false
fi

echo ""

# =============================================================================
# TEST 7: TEST GYM APPLICATION (WITH FXML)
# =============================================================================

print_header "TEST 7: Testing Full GymApplication"

if [ "$MINIMAL_PASSED" = true ]; then
    print_info "Attempting to run GymApplication..."
    echo ""
    
    # Try to run GymApplication
    timeout 5 mvn exec:java -Dexec.mainClass="com.gym.GymApplication" 2>/dev/null &
    PID=$!
    sleep 2
    if kill -0 $PID 2>/dev/null; then
        print_success "GymApplication runs successfully!"
        kill $PID 2>/dev/null
        GYM_PASSED=true
    else
        print_error "GymApplication failed to run"
        GYM_PASSED=false
    fi
else
    print_warning "Skipping GymApplication test (MinimalTest failed)"
    GYM_PASSED=false
fi

echo ""

# =============================================================================
# TEST 8: CHECK FOR COMMON ERRORS
# =============================================================================

print_header "TEST 8: Checking for Common Errors"

# Check for NullPointerException patterns
if [ "$GYM_PASSED" = false ]; then
    echo "Checking for common issues..."
    
    # Check if LoginController has all required methods
    if grep -q "setGymController" src/main/java/com/gym/view/javafx/controller/LoginController.java; then
        print_success "LoginController has setGymController()"
    else
        print_error "LoginController missing setGymController()"
    fi
    
    if grep -q "setStage" src/main/java/com/gym/view/javafx/controller/LoginController.java; then
        print_success "LoginController has setStage()"
    else
        print_error "LoginController missing setStage()"
    fi
    
    # Check if MainController has required methods
    if grep -q "setGymController" src/main/java/com/gym/view/javafx/controller/MainController.java; then
        print_success "MainController has setGymController()"
    else
        print_error "MainController missing setGymController()"
    fi
    
    if grep -q "setStage" src/main/java/com/gym/view/javafx/controller/MainController.java; then
        print_success "MainController has setStage()"
    else
        print_error "MainController missing setStage()"
    fi
    
    if grep -q "setUser" src/main/java/com/gym/view/javafx/controller/MainController.java; then
        print_success "MainController has setUser()"
    else
        print_error "MainController missing setUser()"
    fi
fi

echo ""

# =============================================================================
# TEST 9: GET COMPLETE ERROR OUTPUT
# =============================================================================

print_header "TEST 9: Getting Complete Error Output"

if [ "$GYM_PASSED" = false ]; then
    print_info "Running with -e to get full error output..."
    echo ""
    echo -e "${YELLOW}Full error output:${NC}"
    echo "─────────────────────────────────────────────────────────────"
    mvn exec:java -Dexec.mainClass="com.gym.GymApplication" -e 2>&1 | head -100
    echo "─────────────────────────────────────────────────────────────"
else
    print_success "No errors found! UI is working!"
fi

echo ""

# =============================================================================
# SUMMARY
# =============================================================================

print_header "DIAGNOSTIC SUMMARY"

echo -e "${BLUE}Test Results:${NC}"
echo ""
echo -e "  ${CYAN}1.${NC} Project Structure     : ${GREEN}✅ PASS${NC}"
echo -e "  ${CYAN}2.${NC} Java Files           : ${GREEN}✅ PASS${NC}"
if [ ${#MISSING_FXML[@]} -eq 0 ]; then
    echo -e "  ${CYAN}3.${NC} FXML Files          : ${GREEN}✅ PASS${NC}"
else
    echo -e "  ${CYAN}3.${NC} FXML Files          : ${RED}❌ FAIL (${#MISSING_FXML[@]} missing)${NC}"
fi
echo -e "  ${CYAN}4.${NC} FXML Controllers     : ${GREEN}✅ CHECKED${NC}"
echo -e "  ${CYAN}5.${NC} pom.xml              : ${GREEN}✅ CHECKED${NC}"
if [ "$MINIMAL_PASSED" = true ]; then
    echo -e "  ${CYAN}6.${NC} MinimalTest         : ${GREEN}✅ PASS${NC}"
else
    echo -e "  ${CYAN}6.${NC} MinimalTest         : ${RED}❌ FAIL${NC}"
fi
if [ "$GYM_PASSED" = true ]; then
    echo -e "  ${CYAN}7.${NC} GymApplication      : ${GREEN}✅ PASS${NC}"
else
    echo -e "  ${CYAN}7.${NC} GymApplication      : ${RED}❌ FAIL${NC}"
fi
echo ""

echo -e "${BLUE}Recommendations:${NC}"
echo ""

if [ "$MINIMAL_PASSED" = false ]; then
    echo -e "${RED}  → MinimalTest failed. Fix JavaFX dependencies or installation.${NC}"
elif [ "$GYM_PASSED" = false ]; then
    echo -e "${RED}  → GymApplication failed. Check:${NC}"
    echo -e "${YELLOW}     1. FXML file paths are correct${NC}"
    echo -e "${YELLOW}     2. Controller classes have all required methods${NC}"
    echo -e "${YELLOW}     3. FXMLLoader resource paths use leading slash (/com/gym/...)${NC}"
    echo -e "${YELLOW}     4. All @FXML fields match fx:id in FXML${NC}"
else
    echo -e "${GREEN}  → Everything is working! No issues found.${NC}"
fi

echo ""
print_info "Remove MinimalTest.java: rm src/main/java/com/gym/MinimalTest.java"
echo ""

echo -e "${GREEN}🎉 Diagnostic complete!${NC}"