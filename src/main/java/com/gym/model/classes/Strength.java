package com.gym.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Strength extends GymClass {
    // Strength-specific attributes
    private String focusArea;  // Upper body, Lower body, Full body, Core
    private List<String> equipment;
    private String intensityLevel;
    
    // Constructor
    public Strength(int classId, String className, String schedule, int capacity, 
                    String trainer, String focusArea, String intensityLevel) {
        super(classId, className, schedule, capacity, trainer);
        this.focusArea = focusArea;
        this.intensityLevel = intensityLevel;
        this.equipment = new ArrayList<>();
        // Add default equipment
        this.equipment.add("Dumbbells");
        this.equipment.add("Resistance Bands");
        this.equipment.add("Weight Machines");
    }
    
    // GETTERS & SETTERS
    public String getFocusArea() { return focusArea; }
    public void setFocusArea(String focusArea) { this.focusArea = focusArea; }
    public String getIntensityLevel() { return intensityLevel; }
    public void setIntensityLevel(String intensityLevel) { this.intensityLevel = intensityLevel; }
    public List<String> getEquipment() { return new ArrayList<>(equipment); }
    
    // IMPLEMENT ABSTRACT METHODS
    @Override
    public void addBooking(String memberName) {
        if (checkAvailability()) {
            bookedMembers.add(memberName);
            currentBookings++;
            System.out.println("💪 " + memberName + " booked Strength class: " + className);
            System.out.println("   Focus: " + focusArea + " | Intensity: " + intensityLevel);
            System.out.println("   Equipment: " + equipment);
            System.out.println("   Spots remaining: " + (capacity - currentBookings));
        } else {
            System.out.println("❌ Strength class is full! Cannot book " + memberName);
        }
    }
    
    @Override
    public void removeBooking(String memberName) {
        if (bookedMembers.remove(memberName)) {
            currentBookings--;
            System.out.println("🗑️ " + memberName + " removed from Strength class: " + className);
            System.out.println("   Spots now available: " + (capacity - currentBookings));
        } else {
            System.out.println("⚠️ " + memberName + " not found in this Strength class");
        }
    }
    
    @Override
    public boolean checkAvailability() {
        boolean available = currentBookings < capacity;
        if (available) {
            System.out.println("✅ Strength class has " + (capacity - currentBookings) + " spots available");
        } else {
            System.out.println("❌ Strength class is fully booked");
        }
        return available;
    }
    
    // STRENGTH-SPECIFIC METHODS
    public void addEquipment(String equipmentItem) {
        equipment.add(equipmentItem);
        System.out.println("🏋️ Equipment added: " + equipmentItem);
        System.out.println("   Current equipment: " + equipment);
    }
    
    public void removeEquipment(String equipmentItem) {
        if (equipment.remove(equipmentItem)) {
            System.out.println("🗑️ Equipment removed: " + equipmentItem);
            System.out.println("   Current equipment: " + equipment);
        } else {
            System.out.println("⚠️ Equipment not found: " + equipmentItem);
        }
    }
    
    public void displayEquipment() {
        System.out.println("🏋️ Equipment for " + className + ":");
        for (String item : equipment) {
            System.out.println("   • " + item);
        }
    }
    
    @Override
    public String getClassDetails() {
        return super.getClassDetails() +
               "\nFocus Area: " + focusArea +
               "\nIntensity Level: " + intensityLevel +
               "\nEquipment: " + equipment +
               "\nClass Type: Strength";
    }
}