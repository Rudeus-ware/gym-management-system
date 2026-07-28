package com.gym.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Strength extends GymClass {
    
    private String equipment;
    private String intensityLevel;
    private List<String> bookedMembers;
    
    public Strength(String classId, String name, String description, int duration, 
                    String category, int maxCapacity, String equipment, String intensityLevel) {
        super(classId, name, description, duration, category, maxCapacity);
        this.equipment = equipment;
        this.intensityLevel = intensityLevel;
        this.bookedMembers = new ArrayList<>();
    }

    public Strength(int classId, String name, String description, int capacity, String trainer,
                    String focusArea, String intensityLevel) {
        this(String.valueOf(classId), name, description, 45, "STRENGTH", capacity, focusArea, intensityLevel);
        setTrainer(trainer);
        setSchedule(description);
    }
    
    // ============================================================
    // GETTERS & SETTERS
    // ============================================================
    
    public String getEquipment() { return equipment; }
    public String getIntensityLevel() { return intensityLevel; }
    public List<String> getBookedMembers() { return bookedMembers; }
    public String getFocusArea() { return equipment; }
    
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setIntensityLevel(String intensityLevel) { this.intensityLevel = intensityLevel; }
    public void setBookedMembers(List<String> bookedMembers) { this.bookedMembers = bookedMembers; }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public void addBooking(String memberId) {
        if (bookedMembers == null) {
            bookedMembers = new ArrayList<>();
        }
        if (!bookedMembers.contains(memberId)) {
            bookedMembers.add(memberId);
            setCurrentBookings(getCurrentBookings() + 1);
            // ✅ FIXED: Use getName() instead of className
            System.out.println("✅ Member " + memberId + " booked " + getName() + " class.");
        } else {
            System.out.println("⚠️ Member " + memberId + " already booked this class.");
        }
    }
    
    public void removeBooking(String memberId) {
        if (bookedMembers != null && bookedMembers.remove(memberId)) {
            setCurrentBookings(getCurrentBookings() - 1);
            // ✅ FIXED: Use getName() instead of className
            System.out.println("✅ Member " + memberId + " removed from " + getName() + " class.");
        } else {
            System.out.println("❌ Member " + memberId + " not found in this class.");
        }
    }
    
    public boolean checkAvailability() {
        return bookedMembers != null && bookedMembers.size() < getMaxCapacity();
    }
    
    public int getBookedCount() {
        return bookedMembers != null ? bookedMembers.size() : 0;
    }
    
    public String getClassDetails() {
        // ✅ FIXED: Use getName() instead of className
        return String.format("Strength Class: %s | Equipment: %s | Intensity: %s | Booked: %d/%d",
            getName(), equipment, intensityLevel, getBookedCount(), getMaxCapacity());
    }
    
    @Override
    public String toString() {
        // ✅ FIXED: Use getName() instead of className
        return String.format("Strength{id='%s', name='%s', equipment='%s', intensity='%s', booked=%d}",
            getClassId(), getName(), equipment, intensityLevel, getBookedCount());
    }
}