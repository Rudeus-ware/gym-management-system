#!/bin/bash

# =============================================================================
# Script: find-type-mismatches.sh
# Purpose: Identify all int/String type mismatches in the project
# =============================================================================

set -e

echo "🔍 SCANNING FOR TYPE MISMATCHES"
echo "==============================="
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
print_found() { echo -e "${MAGENTA}🔍 $1${NC}"; }

# =============================================================================
# FUNCTION: FIND PROFILE ID PARAMETERS
# =============================================================================

find_profile_id_issues() {
    echo ""
    print_header "1. Checking for 'int profileId' parameters"
    
    echo "Searching for methods with 'int profileId' parameter..."
    echo ""
    
    # Find all method signatures with int profileId
    RESULTS=$(grep -rn "int profileId" src/main/java --include="*.java" | grep -v "//" | grep -v "import" | grep -v "package" 2>/dev/null)
    
    if [ -n "$RESULTS" ]; then
        echo -e "${YELLOW}⚠️ Methods using 'int profileId' (should be String):${NC}"
        echo ""
        echo "$RESULTS" | while read line; do
            echo -e "  ${MAGENTA}$line${NC}"
        done
        echo ""
        echo -e "${YELLOW}Total: $(echo "$RESULTS" | wc -l) methods found${NC}"
    else
        print_success "No 'int profileId' methods found"
    fi
}

# =============================================================================
# FUNCTION: FIND CONSTRUCTOR MISMATCHES
# =============================================================================

find_constructor_issues() {
    echo ""
    print_header "2. Checking for Constructor Type Mismatches"
    
    echo "Checking Profile constructor calls..."
    echo ""
    
    # Find new Profile(int, ...) calls
    RESULTS=$(grep -rn "new Profile([0-9]" src/main/java --include="*.java" | grep -v "//" 2>/dev/null)
    
    if [ -n "$RESULTS" ]; then
        echo -e "${YELLOW}⚠️ Profile constructors using int ID (should be String):${NC}"
        echo ""
        echo "$RESULTS" | while read line; do
            echo -e "  ${MAGENTA}$line${NC}"
        done
        echo ""
        echo -e "${YELLOW}Total: $(echo "$RESULTS" | wc -l) occurrences found${NC}"
    else
        print_success "No int ID Profile constructor calls found"
    fi
}

# =============================================================================
# FUNCTION: FIND MEMBERSHIP CONSTRUCTOR MISMATCHES
# =============================================================================

find_membership_issues() {
    echo ""
    print_header "3. Checking for Membership Constructor Mismatches"
    
    # Basic
    echo "Checking Basic constructor calls..."
    RESULTS=$(grep -rn "new Basic([0-9]" src/main/java --include="*.java" 2>/dev/null)
    if [ -n "$RESULTS" ]; then
        echo -e "${YELLOW}⚠️ Basic constructors using int ID:${NC}"
        echo "$RESULTS" | while read line; do
            echo -e "  ${MAGENTA}$line${NC}"
        done
    else
        print_success "No int ID Basic constructor calls found"
    fi
    
    # Premium
    echo ""
    echo "Checking Premium constructor calls..."
    RESULTS=$(grep -rn "new Premium([0-9]" src/main/java --include="*.java" 2>/dev/null)
    if [ -n "$RESULTS" ]; then
        echo -e "${YELLOW}⚠️ Premium constructors using int ID:${NC}"
        echo "$RESULTS" | while read line; do
            echo -e "  ${MAGENTA}$line${NC}"
        done
    else
        print_success "No int ID Premium constructor calls found"
    fi
    
    # Family
    echo ""
    echo "Checking Family constructor calls..."
    RESULTS=$(grep -rn "new Family([0-9]" src/main/java --include="*.java" 2>/dev/null)
    if [ -n "$RESULTS" ]; then
        echo -e "${YELLOW}⚠️ Family constructors using int ID:${NC}"
        echo "$RESULTS" | while read line; do
            echo -e "  ${MAGENTA}$line${NC}"
        done
    else
        print_success "No int ID Family constructor calls found"
    fi
}

# =============================================================================
# FUNCTION: FIND COMPARISON OPERATOR ISSUES
# =============================================================================

find_comparison_issues() {
    echo ""
    print_header "4. Checking for '==' vs '.equals()' Issues"
    
    echo "Searching for 'profileId ==' comparisons..."
    echo ""
    
    RESULTS=$(grep -rn "profileId ==" src/main/java --include="*.java" 2>/dev/null)
    
    if [ -n "$RESULTS" ]; then
        echo -e "${YELLOW}⚠️ Using '==' to compare profileId (should use .equals()):${NC}"
        echo ""
        echo "$RESULTS" | while read line; do
            echo -e "  ${MAGENTA}$line${NC}"
        done
        echo ""
        echo -e "${YELLOW}Total: $(echo "$RESULTS" | wc -l) occurrences found${NC}"
    else
        print_success "No 'profileId ==' comparisons found"
    fi
}

# =============================================================================
# FUNCTION: FIND METHOD PARAMETER MISMATCHES
# =============================================================================

find_method_parameter_issues() {
    echo ""
    print_header "5. Checking for Method Parameter Mismatches"
    
    echo "Searching for methods expecting int but called with String..."
    echo ""
    
    # Find getProfileId() returns String
    STRING_ID_USAGE=$(grep -rn "\.getProfileId()" src/main/java --include="*.java" 2>/dev/null)
    
    if [ -n "$STRING_ID_USAGE" ]; then
        echo -e "${CYAN}ℹ️ Locations where getProfileId() is called (returns String):${NC}"
        echo ""
        echo "$STRING_ID_USAGE" | head -20 | while read line; do
            echo -e "  ${MAGENTA}$line${NC}"
        done
        TOTAL=$(echo "$STRING_ID_USAGE" | wc -l)
        echo ""
        echo -e "${CYAN}Total: $TOTAL occurrences (showing first 20)${NC}"
    fi
}

# =============================================================================
# FUNCTION: CHECK DATA MANAGER METHODS
# =============================================================================

find_datamanager_issues() {
    echo ""
    print_header "6. Checking DataManager Method Signatures"
    
    # Check findProfileById
    echo "Checking findProfileById signature..."
    if grep -q "findProfileById(int" src/main/java/com/gym/persistence/DataManager.java 2>/dev/null; then
        echo -e "${YELLOW}⚠️ findProfileById(int) should be findProfileById(String)${NC}"
    else
        print_success "findProfileById uses String"
    fi
    
    # Check removeProfile
    echo ""
    echo "Checking removeProfile signature..."
    if grep -q "removeProfile(int" src/main/java/com/gym/persistence/DataManager.java 2>/dev/null; then
        echo -e "${YELLOW}⚠️ removeProfile(int) should be removeProfile(String)${NC}"
    else
        print_success "removeProfile uses String"
    fi
}

# =============================================================================
# FUNCTION: GENERATE SUMMARY REPORT
# =============================================================================

generate_summary() {
    echo ""
    print_header "SUMMARY REPORT"
    
    echo -e "${BLUE}Type Mismatch Issues Found:${NC}"
    echo ""
    
    # Count issues
    INT_PROFILE_COUNT=$(grep -r "int profileId" src/main/java --include="*.java" 2>/dev/null | wc -l)
    INT_CONSTRUCTOR_COUNT=$(grep -r "new Profile([0-9]" src/main/java --include="*.java" 2>/dev/null | wc -l)
    INT_MEMBERSHIP_COUNT=$(grep -r "new Basic([0-9]" src/main/java --include="*.java" 2>/dev/null | wc -l)
    INT_MEMBERSHIP_COUNT=$((INT_MEMBERSHIP_COUNT + $(grep -r "new Premium([0-9]" src/main/java --include="*.java" 2>/dev/null | wc -l)))
    INT_MEMBERSHIP_COUNT=$((INT_MEMBERSHIP_COUNT + $(grep -r "new Family([0-9]" src/main/java --include="*.java" 2>/dev/null | wc -l)))
    COMPARISON_COUNT=$(grep -r "profileId ==" src/main/java --include="*.java" 2>/dev/null | wc -l)
    
    echo -e "  ${CYAN}1.${NC} Methods with 'int profileId' parameter: ${YELLOW}$INT_PROFILE_COUNT${NC}"
    echo -e "  ${CYAN}2.${NC} Profile constructor with int ID: ${YELLOW}$INT_CONSTRUCTOR_COUNT${NC}"
    echo -e "  ${CYAN}3.${NC} Membership constructors with int ID: ${YELLOW}$INT_MEMBERSHIP_COUNT${NC}"
    echo -e "  ${CYAN}4.${NC} 'profileId ==' comparisons: ${YELLOW}$COMPARISON_COUNT${NC}"
    
    echo ""
    
    if [ $INT_PROFILE_COUNT -gt 0 ] || [ $INT_CONSTRUCTOR_COUNT -gt 0 ] || [ $INT_MEMBERSHIP_COUNT -gt 0 ] || [ $COMPARISON_COUNT -gt 0 ]; then
        echo -e "${RED}⚠️ There are type mismatches that need to be fixed!${NC}"
        echo ""
        echo -e "${YELLOW}Common fixes needed:${NC}"
        echo -e "  1. Change 'int profileId' to 'String profileId' in method signatures"
        echo -e "  2. Change 'new Profile(int, ...)' to 'new Profile(String, ...)'"
        echo -e "  3. Change 'new Basic(int, ...)' to 'new Basic(String, ...)'"
        echo -e "  4. Change 'profileId ==' to 'profileId.equals()'"
        echo -e "  5. Update DataManager methods to accept String IDs"
    else
        print_success "No type mismatches found!"
    fi
}

# =============================================================================
# MAIN EXECUTION
# =============================================================================

echo -e "${BLUE}Starting type mismatch scan...${NC}"
echo ""

# Check if src/main/java exists
if [ ! -d "src/main/java" ]; then
    print_error "src/main/java directory not found!"
    echo "Please run this script from the project root directory."
    exit 1
fi

# Run all checks
find_profile_id_issues
find_constructor_issues
find_membership_issues
find_comparison_issues
find_method_parameter_issues
find_datamanager_issues
generate_summary

# =============================================================================
# GENERATE FIX SUGGESTIONS
# =============================================================================

echo ""
print_header "FIX SUGGESTIONS"

echo -e "${CYAN}To fix these issues automatically, run:${NC}"
echo ""
echo -e "  ${GREEN}sed -i 's/int profileId/String profileId/g' src/main/java/com/gym/controller/*.java${NC}"
echo -e "  ${GREEN}sed -i 's/findProfileById(int/findProfileById(String/g' src/main/java/com/gym/persistence/DataManager.java${NC}"
echo ""
echo -e "${YELLOW}⚠️ Manual review is recommended for complex changes${NC}"
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Scan complete!${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"