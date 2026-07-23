#!/bin/bash

# =============================================================================
# Script: fix-heredoc.sh
# Purpose: Fix heredoc errors in a script
# =============================================================================

echo "🔧 Fixing Heredoc Errors"
echo "========================"

SCRIPT_FILE="remainder_fix.sh"

# Check if file exists
if [ ! -f "$SCRIPT_FILE" ]; then
    echo "❌ File not found: $SCRIPT_FILE"
    echo "   Please specify the correct filename"
    exit 1
fi

echo "📁 File: $SCRIPT_FILE"

# Create backup
cp "$SCRIPT_FILE" "$SCRIPT_FILE.bak"
echo "✅ Backup created: $SCRIPT_FILE.bak"

# Count heredoc starts and ends
START_COUNT=$(grep -c "<< 'EOF'" "$SCRIPT_FILE")
END_COUNT=$(grep -c "^EOF$" "$SCRIPT_FILE")

echo "📊 Heredoc Analysis:"
echo "   Start markers: $START_COUNT"
echo "   End markers: $END_COUNT"

if [ "$START_COUNT" -eq "$END_COUNT" ]; then
    echo "✅ All heredocs are properly closed!"
else
    echo "⚠️ Mismatch found: $START_COUNT starts vs $END_COUNT ends"
    echo ""
    echo "🔧 Fixing..."

    # Find lines where EOF is missing
    # This is a heuristic - look for heredoc starts without matching EOF
    
    # Get the last heredoc start line
    LAST_HEREDOC_START=$(grep -n "<< 'EOF'" "$SCRIPT_FILE" | tail -1 | cut -d: -f1)
    
    if [ -n "$LAST_HEREDOC_START" ]; then
        echo "   Last heredoc starts at line: $LAST_HEREDOC_START"
        echo "   Adding missing EOF at end of file..."
        
        # Add EOF at the end of the file
        echo "" >> "$SCRIPT_FILE"
        echo "EOF" >> "$SCRIPT_FILE"
        
        echo "✅ Added EOF at end of file"
    fi
fi

echo ""
echo "📝 Next Steps:"
echo "   1. Run: bash $SCRIPT_FILE"
echo "   2. If errors persist, check heredoc contents"
echo "   3. Restore backup: cp $SCRIPT_FILE.bak $SCRIPT_FILE"
echo ""
echo "✅ Script complete!"