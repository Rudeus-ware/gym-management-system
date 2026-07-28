#!/bin/bash

# =============================================================================
# Script: fix-classcontroller-errors.sh
# Purpose: Fix all unresolved method errors in ClassController.java
# =============================================================================

echo "🔧 FIXING CLASS CONTROLLER ERRORS"
echo "=================================="
echo ""

# =============================================================================
# STEP 1: Backup the original file
# =============================================================================

echo "📁 Step 1: Creating backup..."
if [ -f "src/main/java/com/gym/controller/ClassController.java" ]; then
    cp src/main/java/com/gym/controller/ClassController.java \
       src/main/java/com/gym/controller/ClassController.java.bak
    echo "✅ Backup created: ClassController.java.bak"
else
    echo "❌ ClassController.java not found!"
    exit 1
fi

# =============================================================================
# STEP 2: Fix imports
# =============================================================================

echo ""
echo "📁 Step 2: Fixing imports..."

# Remove incorrect imports
sed -i '/import com.gym.model.classes;/d' src/main/java/com/gym/controller/ClassController.java
sed -i '/import com.gym.model.booking;/d' src/main/java/com/gym/controller/ClassController.java

# Add correct imports
if ! grep -q "import com.gym.model.classes.GymClass" src/main/java/com/gym/controller/ClassController.java; then
    sed -i '/^package com.gym.controller;/a import com.gym.model.classes.GymClass;' \
        src/main/java/com/gym/controller/ClassController.java
fi

if ! grep -q "import com.gym.model.classes.Yoga" src/main/java/com/gym/controller/ClassController.java; then
    sed -i '/^import com.gym.model.classes.GymClass;/a import com.gym.model.classes.Yoga;' \
        src/main/java/com/gym/controller/ClassController.java
fi

if ! grep -q "import com.gym.model.classes.Spin" src/main/java/com/gym/controller/ClassController.java; then
    sed -i '/^import com.gym.model.classes.Yoga;/a import com.gym.model.classes.Spin;' \
        src/main/java/com/gym/controller/ClassController.java
fi

if ! grep -q "import com.gym.model.classes.Strength" src/main/java/com/gym/controller/ClassController.java; then
    sed -i '/^import com.gym.model.classes.Spin;/a import com.gym.model.classes.Strength;' \
        src/main/java/com/gym/controller/ClassController.java
fi

if ! grep -q "import com.gym.model.booking.Session" src/main/java/com/gym/controller/ClassController.java; then
    sed -i '/^import com.gym.model.classes.Strength;/a import com.gym.model.booking.Session;' \
        src/main/java/com/gym/controller/ClassController.java
fi

if ! grep -q "import com.gym.util.IdGenerator" src/main/java/com/gym/controller/ClassController.java; then
    sed -i '/^import com.gym.model.booking.Session;/a import com.gym.util.IdGenerator;' \
        src/main/java/com/gym/controller/ClassController.java
fi

echo "✅ Imports fixed"

# =============================================================================
# STEP 3: Fix the ClassController code
# =============================================================================

echo ""
echo "📁 Step 3: Rewriting ClassController.java..."

cat > src/main/java/com/gym/controller/ClassController.java << 'EOF'
package com.gym.controller;

import com.gym.database.DatabaseManager;
import com.gym.model.classes.GymClass;
import com.gym.model.classes.Yoga;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.booking.Session;
import com.gym.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * ClassController - Handles gym class operations
 */
public class ClassController {
    
    private GymController gymController;
    private DatabaseManager databaseManager;
    private IdGenerator idGenerator;
    
    public ClassController(GymController gymController) {
        this.gymController = gymController;
        this.databaseManager = gymController.databaseManager;
        this.idGenerator = new IdGenerator();
    }
    
    // ============================================================
    // CREATE CLASS
    // ============================================================
    
    public GymClass createClass(String name, String type, String schedule, int capacity, String trainerId) {
        String classId = idGenerator.generateClassId(LocalDate.now());
        
        GymClass gymClass = null;
        switch (type) {
            case "Yoga":
                gymClass = new Yoga(classId, name, schedule, capacity, trainerId, "Hatha", "Beginner");
                break;
            case "Spin":
                gymClass = new Spin(classId, name, schedule, capacity, trainerId, "Medium", 45, "EDM");
                break;
            case "Strength":
                gymClass = new Strength(classId, name, schedule, capacity, trainerId, "Full Body", "Intermediate");
                break;
            default:
                System.out.println("❌ Invalid class type: " + type);
                return null;
        }
        
        databaseManager.addGymClass(gymClass);
        System.out.println("✅ Class created: " + name + " (ID: " + classId + ")");
        return gymClass;
    }
    
    // ============================================================
    // FIND CLASSES
    // ============================================================
    
    public GymClass findClassById(String id) {
        return databaseManager.findClassById(id);
    }
    
    public List<GymClass> findAllClasses() {
        return databaseManager.findAllClasses();
    }
    
    public List<GymClass> getAllClasses() {
        return databaseManager.getGymClasses();
    }
    
    public boolean isClassAvailable(String classId) {
        GymClass gymClass = findClassById(classId);
        if (gymClass == null) {
            return false;
        }
        return !gymClass.isFull();
    }
    
    // ============================================================
    // SESSION MANAGEMENT
    // ============================================================
    
    public Session createSession(String classId, String date, String startTime, 
                                String endTime, String duration, String trainerId) {
        String sessionId = idGenerator.generateSessionId(LocalDate.now());
        
        Session session = new Session(
            sessionId, classId, date, startTime, endTime, duration, trainerId
        );
        
        databaseManager.addSession(session);
        System.out.println("✅ Session created: " + sessionId + " for class " + classId);
        return session;
    }
    
    public List<Session> getSessionsForClass(String classId) {
        return databaseManager.getSessionsForClass(classId);
    }
    
    public List<Session> getAllSessions() {
        return databaseManager.getSessions();
    }
}
EOF

echo "✅ ClassController.java rewritten"

# =============================================================================
# STEP 4: Verify DatabaseManager has required methods
# =============================================================================

echo ""
echo "📁 Step 4: Verifying DatabaseManager methods..."

if [ -f "src/main/java/com/gym/database/DatabaseManager.java" ]; then
    # Check if methods exist
    if ! grep -q "public.*addGymClass" src/main/java/com/gym/database/DatabaseManager.java; then
        echo "⚠️ addGymClass method may be missing in DatabaseManager"
    fi
    
    if ! grep -q "public.*findClassById" src/main/java/com/gym/database/DatabaseManager.java; then
        echo "⚠️ findClassById method may be missing in DatabaseManager"
    fi
    
    if ! grep -q "public.*findAllClasses" src/main/java/com/gym/database/DatabaseManager.java; then
        echo "⚠️ findAllClasses method may be missing in DatabaseManager"
    fi
    
    if ! grep -q "public.*getGymClasses" src/main/java/com/gym/database/DatabaseManager.java; then
        echo "⚠️ getGymClasses method may be missing in DatabaseManager"
    fi
    
    if ! grep -q "public.*addSession" src/main/java/com/gym/database/DatabaseManager.java; then
        echo "⚠️ addSession method may be missing in DatabaseManager"
    fi
    
    if ! grep -q "public.*getSessions" src/main/java/com/gym/database/DatabaseManager.java; then
        echo "⚠️ getSessions method may be missing in DatabaseManager"
    fi
else
    echo "❌ DatabaseManager.java not found!"
fi

# =============================================================================
# STEP 5: Compile to verify
# =============================================================================

echo ""
echo "📁 Step 5: Compiling project..."
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
else
    echo "❌ Compilation failed. Please check the errors above."
    echo "   To restore backup: cp src/main/java/com/gym/controller/ClassController.java.bak src/main/java/com/gym/controller/ClassController.java"
fi

echo ""
echo "=================================="
echo "✅ Script completed!"
echo ""
echo "📋 Files modified:"
echo "   - src/main/java/com/gym/controller/ClassController.java"
echo ""
echo "📋 Next steps:"
echo "   1. If compilation failed, check if DatabaseManager has all required methods"
echo "   2. Run: mvn clean compile"
echo "   3. Run: mvn javafx:run"