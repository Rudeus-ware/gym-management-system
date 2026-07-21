package com.gym.persistence;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.gym.model.classes.GymClass;
import com.gym.model.classes.Spin;
import com.gym.model.classes.Strength;
import com.gym.model.classes.Yoga;
import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Membership;
import com.gym.model.membership.Premium;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private Gson gson;
    
    public FileManager() {
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Configure GSON for pretty printing and polymorphic membership deserialization
        this.gson = new GsonBuilder()
            .registerTypeAdapter(Membership.class, new JsonDeserializer<Membership>() {
                @Override
                public Membership deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    JsonObject object = json.getAsJsonObject();
                    if (object.has("numberOfMembers") || object.has("familyMembers")) {
                        return context.deserialize(json, Family.class);
                    }
                    if (object.has("benefits") || object.has("extraBenefits")) {
                        return context.deserialize(json, Premium.class);
                    }
                    return context.deserialize(json, Basic.class);
                }
            })
            .registerTypeAdapter(GymClass.class, new JsonDeserializer<GymClass>() {
                @Override
                public GymClass deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    JsonObject object = json.getAsJsonObject();
                    if (object.has("yogaStyle") || object.has("difficulty")) {
                        return context.deserialize(json, Yoga.class);
                    }
                    if (object.has("intensity") || object.has("durationMinutes") || object.has("musicType")) {
                        return context.deserialize(json, Spin.class);
                    }
                    if (object.has("focusArea") || object.has("equipment") || object.has("intensityLevel")) {
                        return context.deserialize(json, Strength.class);
                    }
                    return context.deserialize(json, Yoga.class);
                }
            })
            .setPrettyPrinting()
            .create();
    }
    
    // ===== SAVE METHODS =====
    
    public <T> void saveData(List<T> data, String filename) {
        try (Writer writer = new FileWriter(DATA_DIR + filename)) {
            gson.toJson(data, writer);
            System.out.println("✅ Data saved to: " + filename);
        } catch (IOException e) {
            System.out.println("❌ Error saving data to " + filename);
            e.printStackTrace();
        }
    }
    
    // ===== LOAD METHODS =====
    
    public <T> List<T> loadData(String filename, Type type) {
        File file = new File(DATA_DIR + filename);
        
        // If file doesn't exist, return empty list
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
    
    // ===== UTILITY METHODS =====
    
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