#!/bin/bash

# =============================================================================
# Script: fix-errors-interactive.sh
# Purpose: Fix all compilation errors interactively with confirmation
# =============================================================================

set -e

echo "🔧 INTERACTIVE ERROR FIXER"
echo "=========================="
echo ""
echo "This script will analyze and fix compilation errors one by one."
echo "You will be prompted to confirm each fix before it is applied."
echo ""

# =============================================================================
# COLOR CODES
# =============================================================================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

print_header() { echo -e "\n${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; echo -e "${CYAN}▶ $1${NC}"; echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"; }
print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }
print_warning() { echo -e "${YELLOW}⚠️ $1${NC}"; }
print_info() { echo -e "${CYAN}ℹ️ $1${NC}"; }
print_fix() { echo -e "${MAGENTA}🔧 $1${NC}"; }

# =============================================================================
# CONFIRMATION FUNCTION
# =============================================================================

confirm_fix() {
    echo ""
    echo -e "${YELLOW}Do you want to apply this fix?${NC}"
    echo -e "${CYAN}Type 'yes' to apply, 'no' to skip, or 'exit' to stop:${NC}"
    read -r response
    case "$response" in
        yes|y|Y|YES|Yes) return 0 ;;
        exit|e|E|Exit) echo -e "${RED}Exiting...${NC}"; exit 0 ;;
        *) return 1 ;;
    esac
}

# =============================================================================
# BACKUP FUNCTION
# =============================================================================

create_backup() {
    local file=$1
    if [ -f "$file" ]; then
        cp "$file" "$file.bak.$(date +%Y%m%d_%H%M%S)"
        return 0
    fi
    return 1
}

# =============================================================================
# ERROR 1: GymController.java - int cannot be converted to String
# =============================================================================

fix_gymcontroller_error() {
    print_header "ERROR 1: GymController.java (Line 98)"
    echo -e "${RED}Error: incompatible types: int cannot be converted to java.lang.String${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/controller/GymController.java"
    echo -e "${BLUE}Line:${NC} 98"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} You're trying to use a number (int) where text (String) is expected."
    echo "This happens when passing an int value to a method that expects a String ID."
    echo ""
    echo -e "${GREEN}Fix:${NC} Convert the int to String using String.valueOf() or change the variable type."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fix...${NC}"
        create_backup "src/main/java/com/gym/controller/GymController.java"
        
        # Find the line with the error and fix it
        sed -i 's/\([^ ]*\)\.getProfileId() = \([0-9]*\)/String.valueOf(\1.getProfileId()).equals(\2)/g' \
            src/main/java/com/gym/controller/GymController.java 2>/dev/null || \
        sed -i 's/\([^ ]*\)\.getProfileId() == \([0-9]*\)/\1.getProfileId().equals(String.valueOf(\2))/g' \
            src/main/java/com/gym/controller/GymController.java 2>/dev/null
        
        print_success "Fix applied to GymController.java"
    else
        print_warning "Skipped GymController.java fix"
    fi
}

# =============================================================================
# ERROR 2: ClassController.java - Missing generateId method
# =============================================================================

fix_classcontroller_error() {
    print_header "ERROR 2: ClassController.java (Line 322)"
    echo -e "${RED}Error: cannot find symbol: method generateId(String,String,LocalDate)${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/controller/ClassController.java"
    echo -e "${BLUE}Line:${NC} 322"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} The IdGenerator class doesn't have the generateId() method being called."
    echo "The method signature doesn't match what's being called."
    echo ""
    echo -e "${GREEN}Fix:${NC} Add the generateId() method to IdGenerator class or change the method call."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fix...${NC}"
        
        # Check if IdGenerator exists
        if [ -f "src/main/java/com/gym/util/IdGenerator.java" ]; then
            create_backup "src/main/java/com/gym/util/IdGenerator.java"
            
            # Add the generateId method if it doesn't exist
            if ! grep -q "public String generateId" src/main/java/com/gym/util/IdGenerator.java; then
                echo -e "${CYAN}Adding generateId method to IdGenerator.java...${NC}"
                
                # Add the method before the last closing brace
                sed -i '/^}/i\
    public String generateId(String idType, String roleCode, LocalDate registrationDate) {\
        return roleCode + registrationDate.format(DateTimeFormatter.ofPattern("MMdd")) + idType;\
    }' src/main/java/com/gym/util/IdGenerator.java
                
                print_success "Added generateId method to IdGenerator.java"
            else
                print_warning "generateId method already exists in IdGenerator.java"
            fi
        else
            print_error "IdGenerator.java not found! Creating it..."
            mkdir -p src/main/java/com/gym/util
            cat > src/main/java/com/gym/util/IdGenerator.java << 'EOF'
package com.gym.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IdGenerator {
    
    public IdGenerator(Object connection) {
        // Constructor
    }
    
    public String generateProfileId(String roleCode, LocalDate registrationDate) {
        return roleCode + registrationDate.format(DateTimeFormatter.ofPattern("MMdd")) + "000";
    }
    
    public String generateId(String idType, String roleCode, LocalDate registrationDate) {
        return roleCode + registrationDate.format(DateTimeFormatter.ofPattern("MMdd")) + idType;
    }
}
EOF
            print_success "Created IdGenerator.java with required methods"
        fi
        
        print_success "Fix applied for ClassController error"
    else
        print_warning "Skipped ClassController.java fix"
    fi
}

# =============================================================================
# ERROR 3: BookingController.java - int/String mismatches
# =============================================================================

fix_bookingcontroller_errors() {
    print_header "ERROR 3: BookingController.java Multiple Errors"
    echo -e "${RED}Errors: incompatible types (int ↔ String) and bad operand types${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/controller/BookingController.java"
    echo -e "${BLUE}Lines:${NC} 91, 108, 147, 159, 174, 201, 210"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} These errors are caused by mixing int and String IDs."
    echo "Profile IDs, Class IDs, and Booking IDs should all be Strings."
    echo ""
    echo -e "${GREEN}Fix:${NC} Convert all int IDs to String IDs throughout the class."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fixes to BookingController.java...${NC}"
        create_backup "src/main/java/com/gym/controller/BookingController.java"
        
        # Fix: Change method parameters from int to String
        sed -i 's/public boolean cancelBooking(int bookingId)/public boolean cancelBooking(String bookingId)/g' \
            src/main/java/com/gym/controller/BookingController.java
        
        sed -i 's/public int cancelAllBookingsForProfile(int profileId)/public int cancelAllBookingsForProfile(String profileId)/g' \
            src/main/java/com/gym/controller/BookingController.java
        
        # Fix: Change == to .equals() for String comparisons
        sed -i 's/\([^ ]*\)\.getProfileId() == \([^ ]*\)/\1.getProfileId().equals(\2)/g' \
            src/main/java/com/gym/controller/BookingController.java
        
        sed -i 's/\([^ ]*\)\.getClassId() == \([^ ]*\)/\1.getClassId().equals(\2)/g' \
            src/main/java/com/gym/controller/BookingController.java
        
        sed -i 's/\([^ ]*\)\.getSessionId() == \([^ ]*\)/\1.getSessionId().equals(\2)/g' \
            src/main/java/com/gym/controller/BookingController.java
        
        # Fix: Convert int to String in constructor calls
        sed -i 's/new Booking(\([0-9]*\), \([^,]*\), \([^,]*\), \([^,]*\),/new Booking(String.valueOf(\1), \2, \3, \4,/g' \
            src/main/java/com/gym/controller/BookingController.java
        
        print_success "Applied fixes to BookingController.java"
    else
        print_warning "Skipped BookingController.java fixes"
    fi
}

# =============================================================================
# ERROR 4: AttendanceController.java - Date/Time mismatch
# =============================================================================

fix_attendancecontroller_error() {
    print_header "ERROR 4: AttendanceController.java (Line 81)"
    echo -e "${RED}Error: incompatible types: String cannot be converted to LocalDateTime${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/controller/AttendanceController.java"
    echo -e "${BLUE}Line:${NC} 81"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} You're passing a String where a LocalDateTime is expected."
    echo "The Attendance constructor expects a LocalDateTime for the date."
    echo ""
    echo -e "${GREEN}Fix:${NC} Use LocalDateTime.now() instead of a String date."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fix...${NC}"
        create_backup "src/main/java/com/gym/controller/AttendanceController.java"
        
        # Fix: Use LocalDateTime.now() instead of String
        sed -i 's/"2026-[0-9]*-[0-9]*"/LocalDateTime.now()/g' \
            src/main/java/com/gym/controller/AttendanceController.java
        
        # Fix: Import LocalDateTime if not present
        if ! grep -q "import java.time.LocalDateTime" src/main/java/com/gym/controller/AttendanceController.java; then
            sed -i '/^package/a import java.time.LocalDateTime;' \
                src/main/java/com/gym/controller/AttendanceController.java
        fi
        
        print_success "Applied fix to AttendanceController.java"
    else
        print_warning "Skipped AttendanceController.java fix"
    fi
}

# =============================================================================
# ERROR 5: JavaFxClassController - Type mismatches
# =============================================================================

fix_javafxclasscontroller_errors() {
    print_header "ERROR 5: JavaFxClassController.java Multiple Errors"
    echo -e "${RED}Errors: incompatible types (int ↔ String) and bad conditional expression${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/view/javafx/controller/JavaFxClassController.java"
    echo -e "${BLUE}Lines:${NC} 268, 280, 290, 301"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} These errors are caused by mixing int and String IDs."
    echo "Class IDs should be Strings, not ints."
    echo ""
    echo -e "${GREEN}Fix:${NC} Change all int classId parameters to String classId."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fixes...${NC}"
        create_backup "src/main/java/com/gym/view/javafx/controller/JavaFxClassController.java"
        
        # Change method parameters from int to String
        sed -i 's/public void handleDeleteClass(int classId)/public void handleDeleteClass(String classId)/g' \
            src/main/java/com/gym/view/javafx/controller/JavaFxClassController.java
        
        sed -i 's/public void handleViewClass(int classId)/public void handleViewClass(String classId)/g' \
            src/main/java/com/gym/view/javafx/controller/JavaFxClassController.java
        
        sed -i 's/findClassById(int classId)/findClassById(String classId)/g' \
            src/main/java/com/gym/view/javafx/controller/JavaFxClassController.java
        
        # Change == to .equals()
        sed -i 's/\([^ ]*\)\.getClassId() == \([^ ]*\)/\1.getClassId().equals(\2)/g' \
            src/main/java/com/gym/view/javafx/controller/JavaFxClassController.java
        
        print_success "Applied fixes to JavaFxClassController.java"
    else
        print_warning "Skipped JavaFxClassController.java fixes"
    fi
}

# =============================================================================
# ERROR 6: RegisterController - int cannot be converted to String
# =============================================================================

fix_registercontroller_errors() {
    print_header "ERROR 6: RegisterController.java (Lines 101, 110)"
    echo -e "${RED}Error: incompatible types: int cannot be converted to String${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/view/javafx/controller/RegisterController.java"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} You're using int where String is expected for profile IDs."
    echo ""
    echo -e "${GREEN}Fix:${NC} Change profileId from int to String."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fixes...${NC}"
        create_backup "src/main/java/com/gym/view/javafx/controller/RegisterController.java"
        
        # Change int profileId to String profileId
        sed -i 's/int profileId = dataManager.getProfiles().size() + 1;/String profileId = String.valueOf(dataManager.getProfiles().size() + 1);/g' \
            src/main/java/com/gym/view/javafx/controller/RegisterController.java
        
        sed -i 's/new Profile(profileId,/new Profile(profileId,/g' \
            src/main/java/com/gym/view/javafx/controller/RegisterController.java
        
        print_success "Applied fixes to RegisterController.java"
    else
        print_warning "Skipped RegisterController.java fixes"
    fi
}

# =============================================================================
# ERROR 7: ProfileController - Type mismatches
# =============================================================================

fix_profilecontroller_errors() {
    print_header "ERROR 7: ProfileController.java Multiple Errors"
    echo -e "${RED}Errors: incompatible types (int ↔ String)${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/controller/ProfileController.java"
    echo -e "${BLUE}Lines:${NC} 132, 356, 360, 378, 381, 384"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} These errors are caused by mixing int and String IDs."
    echo ""
    echo -e "${GREEN}Fix:${NC} Change all int ID parameters to String IDs."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fixes...${NC}"
        create_backup "src/main/java/com/gym/controller/ProfileController.java"
        
        # Change method parameters
        sed -i 's/public Profile getProfileById(int profileId)/public Profile getProfileById(String profileId)/g' \
            src/main/java/com/gym/controller/ProfileController.java
        
        sed -i 's/public boolean updateProfile(int profileId/public boolean updateProfile(String profileId/g' \
            src/main/java/com/gym/controller/ProfileController.java
        
        sed -i 's/public boolean deleteProfile(int profileId/public boolean deleteProfile(String profileId/g' \
            src/main/java/com/gym/controller/ProfileController.java
        
        # Fix comparisons
        sed -i 's/\([^ ]*\)\.getProfileId() == \([^ ]*\)/\1.getProfileId().equals(\2)/g' \
            src/main/java/com/gym/controller/ProfileController.java
        
        # Fix constructor calls
        sed -i 's/new Profile(\([0-9]*\), \([^,]*\),/new Profile(String.valueOf(\1), \2,/g' \
            src/main/java/com/gym/controller/ProfileController.java
        
        print_success "Applied fixes to ProfileController.java"
    else
        print_warning "Skipped ProfileController.java fixes"
    fi
}

# =============================================================================
# ERROR 8: SessionController - String == int
# =============================================================================

fix_sessioncontroller_error() {
    print_header "ERROR 8: SessionController.java (Line 27)"
    echo -e "${RED}Error: bad operand types for binary operator '=='${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/controller/SessionController.java"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} You're comparing a String with an int using ==."
    echo ""
    echo -e "${GREEN}Fix:${NC} Use .equals() for String comparison."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fix...${NC}"
        create_backup "src/main/java/com/gym/controller/SessionController.java"
        
        sed -i 's/\([^ ]*\)\.getSessionId() == \([^ ]*\)/\1.getSessionId().equals(String.valueOf(\2))/g' \
            src/main/java/com/gym/controller/SessionController.java
        
        print_success "Applied fix to SessionController.java"
    else
        print_warning "Skipped SessionController.java fix"
    fi
}

# =============================================================================
# ERROR 9: DashboardController - LocalDateTime to String
# =============================================================================

fix_dashboardcontroller_error() {
    print_header "ERROR 9: DashboardController.java (Line 172)"
    echo -e "${RED}Error: incompatible types: LocalDateTime cannot be converted to String${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/view/javafx/controller/DashboardController.java"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} You're trying to assign a LocalDateTime to a String field."
    echo ""
    echo -e "${GREEN}Fix:${NC} Use LocalDateTime.now().format() to convert to String."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fix...${NC}"
        create_backup "src/main/java/com/gym/view/javafx/controller/DashboardController.java"
        
        # Fix the date formatting
        sed -i 's/LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))/LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))/g' \
            src/main/java/com/gym/view/javafx/controller/DashboardController.java
        
        # Add import if needed
        if ! grep -q "import java.time.LocalDateTime" src/main/java/com/gym/view/javafx/controller/DashboardController.java; then
            sed -i '/^package/a import java.time.LocalDateTime;' \
                src/main/java/com/gym/view/javafx/controller/DashboardController.java
        fi
        
        print_success "Applied fix to DashboardController.java"
    else
        print_warning "Skipped DashboardController.java fix"
    fi
}

# =============================================================================
# ERROR 10: Main.java - int cannot be converted to String
# =============================================================================

fix_main_errors() {
    print_header "ERROR 10: Main.java (Lines 352, 374, 386, 438)"
    echo -e "${RED}Errors: incompatible types: int cannot be converted to String${NC}"
    echo ""
    echo -e "${BLUE}File:${NC} src/main/java/com/gym/Main.java"
    echo ""
    echo -e "${YELLOW}Explanation:${NC} You're using int where String is expected for profile IDs."
    echo ""
    echo -e "${GREEN}Fix:${NC} Change int profileId to String profileId."
    echo ""
    
    if confirm_fix; then
        echo -e "${CYAN}Applying fixes...${NC}"
        create_backup "src/main/java/com/gym/Main.java"
        
        # Change int parsing to String
        sed -i 's/int profileId = Integer.parseInt(scanner.nextLine().trim());/String profileId = scanner.nextLine().trim();/g' \
            src/main/java/com/gym/Main.java
        
        # Fix method calls
        sed -i 's/getMember(profileId)/getMember(profileId)/g' \
            src/main/java/com/gym/Main.java
        
        print_success "Applied fixes to Main.java"
    else
        print_warning "Skipped Main.java fixes"
    fi
}

# =============================================================================
# MAIN EXECUTION
# =============================================================================

print_header "STARTING INTERACTIVE ERROR FIXER"

echo -e "${BLUE}This script will fix the following errors:${NC}"
echo ""
echo "  1. GymController.java - int → String conversion"
echo "  2. ClassController.java - Missing generateId method"
echo "  3. BookingController.java - Multiple int/String mismatches"
echo "  4. AttendanceController.java - Date/Time mismatch"
echo "  5. JavaFxClassController.java - Type mismatches"
echo "  6. RegisterController.java - int → String conversion"
echo "  7. ProfileController.java - Type mismatches"
echo "  8. SessionController.java - String == int comparison"
echo "  9. DashboardController.java - LocalDateTime → String"
echo "  10. Main.java - int → String conversions"
echo ""
echo -e "${YELLOW}You will be prompted to confirm each fix.${NC}"
echo -e "${YELLOW}Type 'yes' to apply, 'no' to skip, or 'exit' to stop.${NC}"
echo ""
read -p "Press Enter to continue..."

# Run each fix
fix_gymcontroller_error
fix_classcontroller_error
fix_bookingcontroller_errors
fix_attendancecontroller_error
fix_javafxclasscontroller_errors
fix_registercontroller_errors
fix_profilecontroller_errors
fix_sessioncontroller_error
fix_dashboardcontroller_error
fix_main_errors

# =============================================================================
# FINAL COMPILATION
# =============================================================================

print_header "FINAL COMPILATION"

echo -e "${CYAN}Do you want to compile the project to verify fixes?${NC}"
echo -e "${YELLOW}Type 'yes' to compile, 'no' to skip:${NC}"
read -r response

if [[ "$response" =~ ^(yes|y|Y|YES|Yes)$ ]]; then
    echo -e "${CYAN}Compiling project...${NC}"
    mvn clean compile
    
    if [ $? -eq 0 ]; then
        print_success "✅ Compilation successful!"
    else
        print_error "❌ Compilation still has errors."
        echo -e "${YELLOW}Some fixes may have been skipped or need manual attention.${NC}"
    fi
else
    print_warning "Skipping compilation"
fi

# =============================================================================
# SUMMARY
# =============================================================================

print_header "FIX COMPLETE"

echo -e "${GREEN}✅ Interactive error fixing complete!${NC}"
echo ""
echo -e "${BLUE}Summary:${NC}"
echo "  • Files were backed up before modifications"
echo "  • Each fix was confirmed before applying"
echo "  • Skipped fixes can be applied manually"
echo ""
echo -e "${YELLOW}Backup files are located in the same directory with .bak extension${NC}"
echo -e "${YELLOW}To restore: cp file.java.bak file.java${NC}"
echo ""
echo -e "${GREEN}🎉 Script complete!${NC}"