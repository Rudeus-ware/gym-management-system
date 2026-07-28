#!/bin/bash

echo "🔄 FINAL TRANSITION TO DATABASE MANAGER"
echo "========================================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# =============================================================================
# STEP 1: Backup DataManager
# =============================================================================

echo -e "${YELLOW}📁 Step 1: Backing up DataManager.java...${NC}"
if [ -f "src/main/java/com/gym/persistence/DataManager.java" ]; then
    cp src/main/java/com/gym/persistence/DataManager.java \
       src/main/java/com/gym/persistence/DataManager.java.bak
    echo -e "${GREEN}✅ DataManager.java backed up${NC}"
fi

# =============================================================================
# STEP 2: Update GymController
# =============================================================================

echo -e "\n${YELLOW}📁 Step 2: Updating GymController.java...${NC}"
if [ -f "src/main/java/com/gym/controller/GymController.java" ]; then
    cp src/main/java/com/gym/controller/GymController.java \
       src/main/java/com/gym/controller/GymController.java.bak
    
    sed -i 's/import com.gym.persistence.DataManager;/import com.gym.database.DatabaseManager;/g' \
        src/main/java/com/gym/controller/GymController.java
    
    sed -i 's/DataManager dataManager;/DatabaseManager dataManager;/g' \
        src/main/java/com/gym/controller/GymController.java
    
    sed -i 's/new DataManager()/new DatabaseManager()/g' \
        src/main/java/com/gym/controller/GymController.java
    
    echo -e "${GREEN}✅ GymController.java updated${NC}"
fi

# =============================================================================
# STEP 3: Update All Controllers
# =============================================================================

echo -e "\n${YELLOW}📁 Step 3: Updating all controllers...${NC}"

for file in src/main/java/com/gym/controller/*.java; do
    if [ -f "$file" ]; then
        sed -i 's/import com.gym.persistence.DataManager;/import com.gym.database.DatabaseManager;/g' "$file"
        sed -i 's/DataManager dataManager;/DatabaseManager dataManager;/g' "$file"
        sed -i 's/new DataManager()/new DatabaseManager()/g' "$file"
        echo -e "${GREEN}   Updated: $(basename "$file")${NC}"
    fi
done

# =============================================================================
# STEP 4: Update GymApplication
# =============================================================================

echo -e "\n${YELLOW}📁 Step 4: Updating GymApplication.java...${NC}"
if [ -f "src/main/java/com/gym/GymApplication.java" ]; then
    cp src/main/java/com/gym/GymApplication.java \
       src/main/java/com/gym/GymApplication.java.bak
    
    sed -i 's/import com.gym.persistence.DataManager;/import com.gym.database.DatabaseManager;/g' \
        src/main/java/com/gym/GymApplication.java
    
    sed -i 's/DataManager dataManager;/DatabaseManager dataManager;/g' \
        src/main/java/com/gym/GymApplication.java
    
    sed -i 's/new DataManager()/new DatabaseManager()/g' \
        src/main/java/com/gym/GymApplication.java
    
    echo -e "${GREEN}✅ GymApplication.java updated${NC}"
fi

# =============================================================================
# STEP 5: Update DataInitializer
# =============================================================================

echo -e "\n${YELLOW}📁 Step 5: Updating DataInitializer.java...${NC}"
if [ -f "src/main/java/com/gym/persistence/DataInitializer.java" ]; then
    sed -i 's/DataManager dataManager/DatabaseManager dataManager/g' \
        src/main/java/com/gym/persistence/DataInitializer.java
    
    echo -e "${GREEN}✅ DataInitializer.java updated${NC}"
fi

# =============================================================================
# STEP 6: Create schema if not exists
# =============================================================================

echo -e "\n${YELLOW}📁 Step 6: Checking database schema...${NC}"
if [ -f "src/main/resources/database/schema.sql" ]; then
    echo -e "${GREEN}✅ Database schema exists${NC}"
else
    echo -e "${RED}⚠️ Database schema not found. Please create src/main/resources/database/schema.sql${NC}"
fi

# =============================================================================
# STEP 7: Verify changes
# =============================================================================

echo -e "\n${YELLOW}📁 Step 7: Verifying changes...${NC}"

# Check if any files still use DataManager
REMAINING=$(grep -r "DataManager" src/main/java/com/gym/controller/*.java 2>/dev/null | wc -l)
if [ "$REMAINING" -eq 0 ]; then
    echo -e "${GREEN}✅ No remaining DataManager references in controllers${NC}"
else
    echo -e "${RED}⚠️ $REMAINING DataManager references still found${NC}"
fi

# =============================================================================
# FINAL INSTRUCTIONS
# =============================================================================

echo -e "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}✅ TRANSITION COMPLETE!${NC}"
echo -e "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo -e "${YELLOW}Next Steps:${NC}"
echo ""
echo "1. Start MySQL in XAMPP"
echo "2. Run: mysql -u root -p < src/main/resources/database/schema.sql"
echo "3. Run: mvn clean compile"
echo "4. Run: mvn javafx:run"
echo ""
echo -e "${YELLOW}If you need to keep DataManager for compatibility:${NC}"
echo "  - Rename DataManager to JsonDataManager"
echo "  - Keep both and use a flag to switch"
echo ""
echo -e "${GREEN}🎉 Done! Your application now uses DatabaseManager!${NC}"