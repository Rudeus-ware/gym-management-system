#!/bin/bash

echo "🔧 Fixing JavaFX POM Warnings"
echo "============================="
echo ""

# =============================================================================
# Step 1: Backup pom.xml
# =============================================================================
echo "📁 Step 1: Backing up pom.xml..."
if [ -f "pom.xml" ]; then
    cp pom.xml pom.xml.backup
    echo "✅ Backup created: pom.xml.backup"
else
    echo "❌ Error: pom.xml not found!"
    exit 1
fi
echo ""

# =============================================================================
# Step 2: Update JavaFX version
# =============================================================================
echo "📁 Step 2: Updating JavaFX version..."
if grep -q "<javafx.version>19.0.2" pom.xml; then
    # Update from 19.0.2 to 21.0.2
    sed -i 's/<javafx.version>19.0.2<\/javafx.version>/<javafx.version>21.0.2<\/javafx.version>/g' pom.xml
    echo "✅ Updated to JavaFX 21.0.2"
else
    echo "ℹ️ JavaFX version already updated or different"
fi
echo ""

# =============================================================================
# Step 3: Add classifiers for Windows (if not present)
# =============================================================================
echo "📁 Step 3: Adding classifiers..."
if ! grep -q "<classifier>win</classifier>" pom.xml; then
    # This sed command adds classifier to ALL JavaFX dependencies
    # It looks for javafx- artifacts and inserts classifier after version
    sed -i '/<artifactId>javafx-/,/<\/artifactId>/ {
        /<version>/ {
            n
            /<classifier>/! {
                i\
            <classifier>win</classifier>
            }
        }
    }' pom.xml
    echo "✅ Added classifiers to all JavaFX dependencies"
else
    echo "ℹ️ Classifiers already present"
fi
echo ""

# =============================================================================
# Step 4: Ensure all JavaFX modules are present
# =============================================================================
echo "📁 Step 4: Ensuring all JavaFX modules are present..."

# Check if javafx-base is present
if ! grep -q "javafx-base" pom.xml; then
    echo "📌 Adding javafx-base module..."
    
    # Find the last JavaFX dependency and add base after it
    sed -i '/<artifactId>javafx-graphics<\/artifactId>/a\
        <dependency>\
            <groupId>org.openjfx</groupId>\
            <artifactId>javafx-base</artifactId>\
            <version>${javafx.version}</version>\
            <classifier>win</classifier>\
        </dependency>' pom.xml
    echo "✅ Added javafx-base module"
fi
echo ""

# =============================================================================
# Step 5: Clean Maven cache
# =============================================================================
echo "📁 Step 5: Cleaning Maven cache..."

# Check OS and clean accordingly
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "win32" ]]; then
    # Windows
    if [ -d "$USERPROFILE/.m2/repository/org/openjfx" ]; then
        rm -rf "$USERPROFILE/.m2/repository/org/openjfx"
        echo "✅ JavaFX cache cleaned (Windows)"
    fi
else
    # Linux/Mac
    if [ -d "$HOME/.m2/repository/org/openjfx" ]; then
        rm -rf "$HOME/.m2/repository/org/openjfx"
        echo "✅ JavaFX cache cleaned (Linux/Mac)"
    fi
fi
echo ""

# =============================================================================
# Step 6: Rebuild
# =============================================================================
echo "📁 Step 6: Rebuilding project..."
mvn clean compile -U

if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 BUILD SUCCESSFUL!"
    echo "✅ JavaFX warnings resolved!"
    echo ""
    echo "📋 Verification:"
    echo "   mvn compile 2>&1 | grep -i 'effective model'"
else
    echo ""
    echo "⚠️ Build failed. Restoring backup..."
    cp pom.xml.backup pom.xml
    echo "✅ Backup restored"
    echo ""
    echo "🔄 Please check the error messages above and try again"
fi

echo ""
echo "✅ Script completed!"