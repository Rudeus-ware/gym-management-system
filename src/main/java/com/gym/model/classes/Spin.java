package com.gym.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Spin extends GymClass {
    
    private int bikesAvailable;
    private String instructor;
    private int resistanceLevel;
    private List<String> bookedMembers;
    
    public Spin(String classId, String name, String description, int duration, 
                String category, int maxCapacity, String instructor, int resistanceLevel) {
        super(classId, name, description, duration, category, maxCapacity);
        this.instructor = instructor;
        this.resistanceLevel = resistanceLevel;
        this.bikesAvailable = maxCapacity;
        this.bookedMembers = new ArrayList<>();
    }
    
    // ============================================================
    // GETTERS & SETTERS
    // ============================================================
    
    public int getBikesAvailable() { return bikesAvailable; }
    public String getInstructor() { return instructor; }
    public int getResistanceLevel() { return resistanceLevel; }
    public List<String> getBookedMembers() { return bookedMembers; }
    
    public void setBikesAvailable(int bikesAvailable) { this.bikesAvailable = bikesAvailable; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public void setResistanceLevel(int resistanceLevel) { this.resistanceLevel = resistanceLevel; }
    public void setBookedMembers(List<String> bookedMembers) { this.bookedMembers = bookedMembers; }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public void addBooking(String memberId) {
        if (bikesAvailable > 0 && !bookedMembers.contains(memberId)) {
            bookedMembers.add(memberId);
            bikesAvailable--;
            setCurrentBookings(getCurrentBookings() + 1);
            System.out.println("✅ Member " + memberId + " booked " + getName() + " class. Bikes remaining: " + bikesAvailable);
        } else if (bookedMembers.contains(memberId)) {
            System.out.println("⚠️ Member " + memberId + " already booked this class.");
        } else {
            System.out.println("❌ No bikes available for " + getName() + " class!");
        }
    }
    
    public void removeBooking(String memberId) {
        if (bookedMembers.remove(memberId)) {
            bikesAvailable++;
            setCurrentBookings(getCurrentBookings() - 1);
            System.out.println("✅ Member " + memberId + " removed from " + getName() + " class. Bikes available: " + bikesAvailable);
        } else {
            System.out.println("❌ Member " + memberId + " not found in this class.");
        }
    }
    
    public boolean checkAvailability() {
        return bikesAvailable > 0;
    }
    
    public int getBookedCount() {
        return bookedMembers.size();
    }
    
    public void adjustResistance(int level) {
        if (level >= 1 && level <= 10) {
            this.resistanceLevel = level;
            System.out.println("✅ Spin resistance adjusted to level: " + level);
        } else {
            System.out.println("❌ Invalid resistance level. Must be between 1-10.");
        }
    }
    
    public String getClassDetails() {
        // ✅ FIXED: Use getName() instead of className
        return String.format("Spin Class: %s | Instructor: %s | Bikes Available: %d/%d | Resistance: %d | Booked: %d",
            getName(), instructor, bikesAvailable, getMaxCapacity(), resistanceLevel, getBookedCount());
    }
    
    @Override
    public String toString() {
        // ✅ FIXED: Use getName() instead of className
        return String.format("Spin{id='%s', name='%s', instructor='%s', bikes=%d/%d, resistance=%d, booked=%d}",
            getClassId(), getName(), instructor, bikesAvailable, getMaxCapacity(), resistanceLevel, getBookedCount());
    }
}