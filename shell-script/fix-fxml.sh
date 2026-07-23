#!/bin/bash

echo "🔧 Fixing FXML Controller References"
echo "===================================="
echo ""

# =============================================================================
# Fix main-view.fxml
# =============================================================================

echo "📁 Fixing main-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/main-view.fxml" ]; then
    # Replace wrong controller with correct one
    sed -i 's/fx:controller="com.gym.view.controllers.MainController"/fx:controller="com.gym.view.javafx.controller.MainController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/main-view.fxml
    echo "✅ main-view.fxml fixed"
else
    echo "⚠️ main-view.fxml not found"
fi
echo ""

# =============================================================================
# Fix login-view.fxml
# =============================================================================

echo "📁 Fixing login-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/login-view.fxml" ]; then
    sed -i 's/fx:controller="com.gym.view.controllers.LoginController"/fx:controller="com.gym.view.javafx.controller.LoginController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/login-view.fxml
    echo "✅ login-view.fxml fixed"
else
    echo "⚠️ login-view.fxml not found"
fi
echo ""

# =============================================================================
# Fix register-view.fxml
# =============================================================================

echo "📁 Fixing register-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/register-view.fxml" ]; then
    sed -i 's/fx:controller="com.gym.view.controllers.RegisterController"/fx:controller="com.gym.view.javafx.controller.RegisterController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/register-view.fxml
    echo "✅ register-view.fxml fixed"
else
    echo "⚠️ register-view.fxml not found"
fi
echo ""

# =============================================================================
# Fix profile-view.fxml
# =============================================================================

echo "📁 Fixing profile-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/profile-view.fxml" ]; then
    sed -i 's/fx:controller="com.gym.view.controllers.ProfileController"/fx:controller="com.gym.view.javafx.controller.ProfileController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/profile-view.fxml
    echo "✅ profile-view.fxml fixed"
else
    echo "⚠️ profile-view.fxml not found"
fi
echo ""

# =============================================================================
# Fix class-view.fxml
# =============================================================================

echo "📁 Fixing class-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/class-view.fxml" ]; then
    sed -i 's/fx:controller="com.gym.view.controllers.ClassController"/fx:controller="com.gym.view.javafx.controller.ClassController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/class-view.fxml
    echo "✅ class-view.fxml fixed"
else
    echo "⚠️ class-view.fxml not found"
fi
echo ""

# =============================================================================
# Fix booking-view.fxml
# =============================================================================

echo "📁 Fixing booking-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/booking-view.fxml" ]; then
    sed -i 's/fx:controller="com.gym.view.controllers.BookingController"/fx:controller="com.gym.view.javafx.controller.BookingController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/booking-view.fxml
    echo "✅ booking-view.fxml fixed"
else
    echo "⚠️ booking-view.fxml not found"
fi
echo ""

# =============================================================================
# Fix attendance-view.fxml
# =============================================================================

echo "📁 Fixing attendance-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/attendance-view.fxml" ]; then
    sed -i 's/fx:controller="com.gym.view.controllers.AttendanceController"/fx:controller="com.gym.view.javafx.controller.AttendanceController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/attendance-view.fxml
    echo "✅ attendance-view.fxml fixed"
else
    echo "⚠️ attendance-view.fxml not found"
fi
echo ""

# =============================================================================
# Fix reports-view.fxml
# =============================================================================

echo "📁 Fixing reports-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/reports-view.fxml" ]; then
    sed -i 's/fx:controller="com.gym.view.controllers.ReportsController"/fx:controller="com.gym.view.javafx.controller.ReportsController"/g' \
        src/main/resources/com/gym/view/javafx/fxml/reports-view.fxml
    echo "✅ reports-view.fxml fixed"
else
    echo "⚠️ reports-view.fxml not found"
fi
echo ""

# =============================================================================
# Check dashboard-view.fxml
# =============================================================================

echo "📁 Checking dashboard-view.fxml..."
if [ -f "src/main/resources/com/gym/view/javafx/fxml/dashboard-view.fxml" ]; then
    if grep -q 'fx:controller=' src/main/resources/com/gym/view/javafx/fxml/dashboard-view.fxml; then
        # Fix if needed
        sed -i 's/fx:controller="com.gym.view.controllers.DashboardController"/fx:controller="com.gym.view.javafx.controller.DashboardController"/g' \
            src/main/resources/com/gym/view/javafx/fxml/dashboard-view.fxml
        echo "✅ dashboard-view.fxml fixed"
    else
        echo "ℹ️ dashboard-view.fxml has no controller reference (this is fine)"
    fi
else
    echo "⚠️ dashboard-view.fxml not found"
fi
echo ""

# =============================================================================
# Verify fixes
# =============================================================================

echo "📁 Verifying fixes..."
echo ""

# Check if any wrong controllers remain
echo "Checking for remaining wrong controller references..."
if grep -r 'fx:controller="com.gym.view.controllers.' src/main/resources/com/gym/view/javafx/fxml/ 2>/dev/null; then
    echo "⚠️ Some wrong controller references still exist"
else
    echo "✅ All controller references are correct!"
fi
echo ""

echo "🔧 Fix complete!"
echo ""
echo "📋 Next steps:"
echo "  1. mvn clean compile"
echo "  2. mvn javafx:run"
echo ""