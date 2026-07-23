#!/bin/bash

echo "🔧 Fixing FileManager.java..."
echo "============================"

# Create backup
if [ -f "src/main/java/com/gym/persistence/FileManager.java" ]; then
    cp src/main/java/com/gym/persistence/FileManager.java \
       src/main/java/com/gym/persistence/FileManager.java.bak
    echo "✅ Backup created"
fi

# Create the fixed file
cat > src/main/java/com/gym/persistence/FileManager.java << 'EOF'
package com.gym.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private Gson gson;
    
    public FileManager() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .addSerializationExclusionStrategy(new JsonExclusionStrategy())
            .create();
    }
    
    public <T> void saveData(List<T> data, String filename) {
        try (Writer writer = new FileWriter(DATA_DIR + filename)) {
            gson.toJson(data, writer);
            System.out.println("✅ Data saved to: " + filename);
        } catch (IOException e) {
            System.out.println("❌ Error saving data to " + filename);
            e.printStackTrace();
        }
    }
    
    public <T> List<T> loadData(String filename, Type type) {
        File file = new File(DATA_DIR + filename);
        
        if (!file.exists()) {
            System.out.println("ℹ️ " + filename + " not found. Starting with empty data.");
            return new ArrayList<>();
        }
        
        try (Reader reader = new FileReader(file)) {
            List<T> data = gson.fromJson(reader, type);
            System.out.println("✅ Data loaded from: " + filename + " (" + data.size() + " items)");
            return data;
        } catch (IOException e) {
            System.out.println("❌ Error loading data from " + filename);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public boolean fileExists(String filename) {
        return new File(DATA_DIR + filename).exists();
    }
    
    public void deleteFile(String filename) {
        File file = new File(DATA_DIR + filename);
        if (file.exists() && file.delete()) {
            System.out.println("🗑️ File deleted: " + filename);
        }
    }
    
    public void clearAllData() {
        String[] files = {
            "profiles.json",
            "memberships.json", 
            "classes.json",
            "sessions.json",
            "bookings.json",
            "attendance.json",
            "trainers.json"
        };
        
        for (String file : files) {
            deleteFile(file);
        }
        System.out.println("✅ All data cleared!");
    }
}
EOF

echo "✅ FileManager.java fixed!"

# Verify
if grep -q "package com.gym.persistence" src/main/java/com/gym/persistence/FileManager.java; then
    echo "✅ Package declaration found - good!"
else
    echo "❌ Package declaration still missing!"
fi

echo ""
echo "📋 Next steps:"
echo "  1. mvn clean compile"