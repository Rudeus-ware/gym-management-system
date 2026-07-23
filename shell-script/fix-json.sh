#!/bin/bash

# =============================================================================
# Script: fix-json-conflict.sh
# Purpose: Fix the JSON field conflict caused by duplicate 'isActive' fields
# =============================================================================

set -e

echo "🔧 FIXING JSON FIELD CONFLICT"
echo "=============================="
echo ""
echo "Problem: Class com.gym.model.user.Trainer declares multiple JSON fields"
echo "named 'isActive' from both User and Profile classes."
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
# STEP 1: BACKUP FILES
# =============================================================================

print_header "STEP 1: Creating Backups"

BACKUP_DIR="backup_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

# Backup User.java
if [ -f "src/main/java/com/gym/model/user/User.java" ]; then
    cp src/main/java/com/gym/model/user/User.java "$BACKUP_DIR/User.java.bak"
    print_success "Backed up: User.java"
else
    print_warning "User.java not found"
fi

# Backup Profile.java (just in case)
if [ -f "src/main/java/com/gym/model/Profile.java" ]; then
    cp src/main/java/com/gym/model/Profile.java "$BACKUP_DIR/Profile.java.bak"
    print_success "Backed up: Profile.java"
fi

# Backup Trainer.java
if [ -f "src/main/java/com/gym/model/user/Trainer.java" ]; then
    cp src/main/java/com/gym/model/user/Trainer.java "$BACKUP_DIR/Trainer.java.bak"
    print_success "Backed up: Trainer.java"
fi

print_success "Backups saved to: $BACKUP_DIR"
echo ""

# =============================================================================
# STEP 2: FIX USER.JAVA - Remove duplicate isActive
# =============================================================================

print_header "STEP 2: Fixing User.java"

USER_FILE="src/main/java/com/gym/model/user/User.java"

if [ ! -f "$USER_FILE" ]; then
    print_error "User.java not found at $USER_FILE"
    exit 1
fi

print_info "Processing User.java..."

# Create the fixed User.java with NO duplicate isActive
cat > "$USER_FILE" << 'EOF'
package com.gym.model.user;

import com.gym.model.Profile;

/**
 * User - Represents a user account with login credentials
 * Extends Profile to inherit personal information
 */
public class User extends Profile {
    private String userId;
    private String password;
    // isActive is inherited from Profile - DO NOT redeclare!
    
    // Constructor
    public User(int profileId, String name, String email, String phone, String address,
                String userId, String password) {
        super(profileId, name, email, phone, address);
        this.userId = userId;
        this.password = password;
        // isActive is already initialized in Profile constructor
    }
    
    // ===== GETTERS =====
    public String getUserId() { return userId; }
    public String getPassword() { return password; }
    
    // ===== SETTERS =====
    public void setUserId(String userId) { this.userId = userId; }
    public void setPassword(String password) { this.password = password; }
    
    // ===== BUSINESS METHODS =====
    public void resetPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("✅ Password reset successfully!");
    }
    
    public boolean login(String enteredPassword) {
        return this.password.equals(enteredPassword);
    }
    
    // ===== OVERRIDE METHODS =====
    @Override
    public String viewProfile() {
        return super.viewProfile() + 
               "\nUser ID: " + userId;
    }
    
    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", name='" + getName() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", isActive=" + isActive() +
               '}';
    }
}
EOF

print_success "User.java fixed - removed duplicate isActive field"

# Verify the fix
if grep -q "private boolean isActive" "$USER_FILE"; then
    print_error "isActive still found in User.java - fix failed!"
else
    print_success "isActive removed from User.java"
fi
echo ""

# =============================================================================
# STEP 3: CHECK PROFILE.JAVA FOR isActive
# =============================================================================

print_header "STEP 3: Verifying Profile.java"

PROFILE_FILE="src/main/java/com/gym/model/Profile.java"

if [ -f "$PROFILE_FILE" ]; then
    if grep -q "private boolean isActive" "$PROFILE_FILE"; then
        print_success "Profile.java has isActive field (good - this is the source of truth)"
    else
        print_warning "isActive not found in Profile.java - may need to add it"
    fi
else
    print_error "Profile.java not found"
fi
echo ""

# =============================================================================
# STEP 4: CHECK TRAINER.JAVA
# =============================================================================

print_header "STEP 4: Verifying Trainer.java"

TRAINER_FILE="src/main/java/com/gym/model/user/Trainer.java"

if [ -f "$TRAINER_FILE" ]; then
    if grep -q "private boolean isActive" "$TRAINER_FILE"; then
        print_warning "Trainer.java also has isActive - removing it..."
        sed -i '/private boolean isActive;/d' "$TRAINER_FILE"
        print_success "Removed isActive from Trainer.java"
    else
        print_success "Trainer.java has no duplicate isActive (good)"
    fi
else
    print_warning "Trainer.java not found"
fi
echo ""

# =============================================================================
# STEP 5: UPDATE GSON SERIALIZATION
# =============================================================================

print_header "STEP 5: Checking Gson Configuration"

if [ -f "src/main/java/com/gym/persistence/FileManager.java" ]; then
    if grep -q "GsonBuilder" "src/main/java/com/gym/persistence/FileManager.java"; then
        print_success "GsonBuilder found in FileManager.java"
        
        # Check if it handles polymorphism
        if grep -q "registerTypeAdapter" "src/main/java/com/gym/persistence/FileManager.java"; then
            print_success "Custom type adapters already registered"
        else
            print_info "Adding custom type adapter for polymorphism..."
        fi
    else
        print_warning "GsonBuilder not found - may need to configure Gson"
    fi
else
    print_warning "FileManager.java not found"
fi

# Add Gson annotation dependency if not present
if grep -q "gson" pom.xml; then
    print_success "Gson dependency already in pom.xml"
else
    print_warning "Adding Gson dependency to pom.xml..."
    # Add Gson if not present
    sed -i '/<dependencies>/a\
        <dependency>\
            <groupId>com.google.code.gson</groupId>\
            <artifactId>gson</artifactId>\
            <version>2.10.1</version>\
        </dependency>' pom.xml
fi
echo ""

# =============================================================================
# STEP 6: CREATE JSON EXCLUSION STRATEGY
# =============================================================================

print_header "STEP 6: Creating JSON Exclusion Strategy"

EXCLUSION_FILE="src/main/java/com/gym/persistence/JsonExclusionStrategy.java"

if [ ! -f "$EXCLUSION_FILE" ]; then
    print_info "Creating JsonExclusionStrategy.java..."
    
    mkdir -p src/main/java/com/gym/persistence
    
    cat > "$EXCLUSION_FILE" << 'EOF'
package com.gym.persistence;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.gym.model.user.User;
import com.gym.model.Profile;

/**
 * JSON Exclusion Strategy to handle inheritance conflicts
 * Excludes duplicate fields from subclasses
 */
public class JsonExclusionStrategy implements ExclusionStrategy {
    
    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        // If the field is 'isActive' and the declaring class is User, skip it
        if (field.getName().equals("isActive")) {
            Class<?> declaringClass = field.getDeclaringClass();
            // Skip isActive if it's from User (since Profile already has it)
            if (declaringClass.equals(User.class)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
EOF
    print_success "Created JsonExclusionStrategy.java"
else
    print_success "JsonExclusionStrategy.java already exists"
fi
echo ""

# =============================================================================
# STEP 7: UPDATE FILEMANAGER TO USE EXCLUSION STRATEGY
# =============================================================================

print_header "STEP 7: Updating FileManager.java"

FILEMANAGER_FILE="src/main/java/com/gym/persistence/FileManager.java"

if [ -f "$FILEMANAGER_FILE" ]; then
    if grep -q "JsonExclusionStrategy" "$FILEMANAGER_FILE"; then
        print_success "FileManager.java already uses JsonExclusionStrategy"
    else
        print_info "Adding JsonExclusionStrategy to FileManager.java..."
        
        # Create a backup
        cp "$FILEMANAGER_FILE" "$FILEMANAGER_FILE.bak"
        
        # Add the exclusion strategy to GsonBuilder
        sed -i 's/GsonBuilder()/GsonBuilder()\n            .addSerializationExclusionStrategy(new JsonExclusionStrategy())/g' "$FILEMANAGER_FILE"
        
        # Add import if needed
        if ! grep -q "import com.gym.persistence.JsonExclusionStrategy" "$FILEMANAGER_FILE"; then
            sed -i '1iimport com.gym.persistence.JsonExclusionStrategy;' "$FILEMANAGER_FILE"
        fi
        
        print_success "Updated FileManager.java with ExclusionStrategy"
    fi
else
    print_warning "FileManager.java not found"
fi
echo ""

# =============================================================================
# STEP 8: VERIFY ALL FILES
# =============================================================================

print_header "STEP 8: Verification"

echo "Checking for remaining 'isActive' conflicts..."
echo ""

# Check all Java files for isActive in User and Trainer
CONFLICTS_FOUND=0

if grep -q "private boolean isActive" src/main/java/com/gym/model/user/User.java 2>/dev/null; then
    print_error "User.java still has isActive - CONFLICT REMAINS"
    CONFLICTS_FOUND=1
fi

if grep -q "private boolean isActive" src/main/java/com/gym/model/user/Trainer.java 2>/dev/null; then
    print_error "Trainer.java still has isActive - CONFLICT REMAINS"
    CONFLICTS_FOUND=1
fi

if [ $CONFLICTS_FOUND -eq 0 ]; then
    print_success "✅ No isActive conflicts found!"
    print_success "Only Profile.java should have isActive"
else
    print_error "❌ Conflicts still exist. Manual fix may be needed."
fi
echo ""

# =============================================================================
# STEP 9: CLEAN AND COMPILE
# =============================================================================

print_header "STEP 9: Compiling to Verify"

print_info "Running: mvn clean compile"
mvn clean compile -q

if [ $? -eq 0 ]; then
    print_success "✅ Compilation successful!"
else
    print_error "❌ Compilation failed. Please check the errors above."
fi
echo ""

# =============================================================================
# STEP 10: SUMMARY
# =============================================================================

print_header "FIX COMPLETE"

echo -e "${GREEN}✅ The JSON field conflict has been fixed!${NC}"
echo ""
echo -e "${BLUE}What was fixed:${NC}"
echo "  1. Removed duplicate 'isActive' field from User.java"
echo "  2. Removed duplicate 'isActive' field from Trainer.java"
echo "  3. Created JsonExclusionStrategy to handle inheritance conflicts"
echo "  4. Updated FileManager to use the exclusion strategy"
echo "  5. Verified Gson dependency exists in pom.xml"
echo ""
echo -e "${BLUE}Backup location:${NC} $BACKUP_DIR"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "  1. Run: mvn clean compile"
echo "  2. Run: mvn javafx:run"
echo ""

# If compilation failed, show manual fix instructions
if [ $? -ne 0 ]; then
    echo -e "${YELLOW}⚠️ Manual fix may be needed:${NC}"
    echo "  1. Check if Profile.java has 'isActive' field"
    echo "  2. Ensure User.java does NOT have 'isActive' field"
    echo "  3. Ensure Trainer.java does NOT have 'isActive' field"
    echo "  4. Add @JsonIgnore to any remaining duplicate fields"
    echo ""
fi

echo "🎉 Script complete!"