#!/bin/bash

echo "🔧 Fixing Attendance Class Conflict"
echo "==================================="
echo ""

# =============================================================================
# STEP 1: IDENTIFY THE PROBLEM
# =============================================================================

echo "📁 Step 1: Identifying Attendance class locations..."
echo ""

ATTENDANCE_FILES=$(find src/main/java -name "Attendance.java" 2>/dev/null)

if [ -z "$ATTENDANCE_FILES" ]; then
    echo "❌ No Attendance.java files found!"
    exit 1
fi

echo "Found Attendance.java files:"
echo "$ATTENDANCE_FILES"
echo ""

# =============================================================================
# STEP 2: CHOOSE THE KEEPER
# =============================================================================

echo "📁 Step 2: Choosing which Attendance to keep..."
echo ""

# Check if booking Attendance exists
if [ -f "src/main/java/com/gym/model/booking/Attendance.java" ]; then
    echo "✅ Keeping: src/main/java/com/gym/model/booking/Attendance.java"
    KEEP="booking"
else
    echo "✅ Keeping: src/main/java/com/gym/model/attendance/Attendance.java"
    KEEP="attendance"
fi

# =============================================================================
# STEP 3: DELETE THE DUPLICATE
# =============================================================================

echo ""
echo "📁 Step 3: Deleting duplicate Attendance class..."
echo ""

if [ "$KEEP" = "booking" ]; then
    if [ -f "src/main/java/com/gym/model/attendance/Attendance.java" ]; then
        echo "🗑️ Deleting: src/main/java/com/gym/model/attendance/Attendance.java"
        rm src/main/java/com/gym/model/attendance/Attendance.java
        echo "✅ Deleted!"
        
        # Also delete the attendance folder if empty
        if [ -z "$(ls -A src/main/java/com/gym/model/attendance 2>/dev/null)" ]; then
            rmdir src/main/java/com/gym/model/attendance 2>/dev/null
            echo "🗑️ Removed empty folder: src/main/java/com/gym/model/attendance"
        fi
    fi
else
    if [ -f "src/main/java/com/gym/model/booking/Attendance.java" ]; then
        echo "🗑️ Deleting: src/main/java/com/gym/model/booking/Attendance.java"
        rm src/main/java/com/gym/model/booking/Attendance.java
        echo "✅ Deleted!"
    fi
fi

# =============================================================================
# STEP 4: UPDATE IMPORTS IN ALL FILES
# =============================================================================

echo ""
echo "📁 Step 4: Updating imports to use the correct Attendance class..."
echo ""

# Determine the correct import statement
if [ "$KEEP" = "booking" ]; then
    CORRECT_IMPORT="com.gym.model.booking.Attendance"
    echo "📌 Using: $CORRECT_IMPORT"
else
    CORRECT_IMPORT="com.gym.model.attendance.Attendance"
    echo "📌 Using: $CORRECT_IMPORT"
fi

# Files to update
FILES_TO_UPDATE=$(grep -l "com.gym.model.attendance.Attendance\|com.gym.model.booking.Attendance" \
    src/main/java/com/gym/**/*.java 2>/dev/null)

echo ""
echo "Files to update:"
echo "$FILES_TO_UPDATE"
echo ""

# Update imports
for FILE in $FILES_TO_UPDATE; do
    echo "✏️ Updating: $FILE"
    
    # Replace both imports with the correct one
    sed -i "s/import com.gym.model.attendance.Attendance;/import $CORRECT_IMPORT;/g" "$FILE"
    sed -i "s/import com.gym.model.booking.Attendance;/import $CORRECT_IMPORT;/g" "$FILE"
    
    echo "   ✅ Updated"
done

# =============================================================================
# STEP 5: FIX ATTENDANCE CONTROLLER
# =============================================================================

echo ""
echo "📁 Step 5: Fixing AttendanceController.java..."
echo ""

# If we kept booking.Attendance, update AttendanceController to use it
if [ "$KEEP" = "booking" ]; then
    # Fix AttendanceController to use booking.Attendance
    sed -i 's/com.gym.model.attendance.Attendance/com.gym.model.booking.Attendance/g' \
        src/main/java/com/gym/controller/AttendanceController.java 2>/dev/null || echo "   ✅ Already correct"
else
    sed -i 's/com.gym.model.booking.Attendance/com.gym.model.attendance.Attendance/g' \
        src/main/java/com/gym/controller/AttendanceController.java 2>/dev/null || echo "   ✅ Already correct"
fi

# =============================================================================
# STEP 6: FIX DATA MANAGER
# =============================================================================

echo ""
echo "📁 Step 6: Updating DataManager.java..."
echo ""

if [ "$KEEP" = "booking" ]; then
    sed -i 's/import com.gym.model.attendance.Attendance;/import com.gym.model.booking.Attendance;/g' \
        src/main/java/com/gym/persistence/DataManager.java 2>/dev/null
    sed -i 's/List<com.gym.model.attendance.Attendance>/List<com.gym.model.booking.Attendance>/g' \
        src/main/java/com/gym/persistence/DataManager.java 2>/dev/null
else
    sed -i 's/import com.gym.model.booking.Attendance;/import com.gym.model.attendance.Attendance;/g' \
        src/main/java/com/gym/persistence/DataManager.java 2>/dev/null
    sed -i 's/List<com.gym.model.booking.Attendance>/List<com.gym.model.attendance.Attendance>/g' \
        src/main/java/com/gym/persistence/DataManager.java 2>/dev/null
fi
echo "✅ Updated"

# =============================================================================
# STEP 7: COMPILE TO VERIFY
# =============================================================================

echo ""
echo "📁 Step 7: Compiling to verify fixes..."
echo ""

echo "🔨 Running: mvn clean compile"
mvn clean compile 2>&1 | tail -20

if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 COMPILATION SUCCESSFUL!"
    echo "========================================="
    echo "✅ All Attendance conflicts resolved!"
else
    echo ""
    echo "⚠️ Still have errors. Please check the output above."
    echo ""
    echo "To restore backups (if any), run:"
    echo "  git checkout -- src/main/java/com/gym"
fi

echo ""
echo "📋 Summary:"
echo "   - Kept: $KEEP.Attendance"
echo "   - Deleted: $( [ "$KEEP" = "booking" ] && echo "attendance" || echo "booking" ).Attendance"
echo "   - Updated imports in all files"
echo "========================================="