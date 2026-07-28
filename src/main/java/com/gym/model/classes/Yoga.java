package com.gym.model.classes;

import java.util.ArrayList;
import java.util.List;

public class Yoga extends GymClass {
    
    private String style;
    private int maxParticipants;
    private List<String> bookedMembers;
    
    public Yoga(String classId, String name, String description, int duration, 
                String category, int maxCapacity, String style, int maxParticipants) {
        super(classId, name, description, duration, category, maxCapacity);
        this.style = style;
        this.maxParticipants = maxParticipants;
        this.bookedMembers = new ArrayList<>();
    }

    public Yoga(int classId, String name, String description, int capacity, String trainer,
                String style, String difficulty) {
        this(String.valueOf(classId), name, description, 60, "YOGA", capacity, style, 20);
        setTrainer(trainer);
        setSchedule(description);
    }
    
    // ============================================================
    // GETTERS & SETTERS
    // ============================================================
    
    public String getStyle() { return style; }
    public int getMaxParticipants() { return maxParticipants; }
    public List<String> getBookedMembers() { return bookedMembers; }
    public String getYogaStyle() { return style; }
    public String getDifficulty() { return maxParticipants > 10 ? "Intermediate" : "Beginner"; }
    
    public void setStyle(String style) { this.style = style; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    public void setBookedMembers(List<String> bookedMembers) { this.bookedMembers = bookedMembers; }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public void addBooking(String memberId) {
        if (bookedMembers == null) {
            bookedMembers = new ArrayList<>();
        }
        if (!bookedMembers.contains(memberId) && bookedMembers.size() < maxParticipants) {
            bookedMembers.add(memberId);
            setCurrentBookings(getCurrentBookings() + 1);
            // ✅ FIXED: Use getName() instead of className
            System.out.println("✅ Member " + memberId + " booked " + getName() + " class.");
        } else if (bookedMembers.contains(memberId)) {
            System.out.println("⚠️ Member " + memberId + " already booked this class.");
        } else {
            // ✅ FIXED: Use getName() instead of className
            System.out.println("❌ " + getName() + " class is full!");
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
        return bookedMembers != null && bookedMembers.size() < maxParticipants;
    }
    
    public int getBookedCount() {
        return bookedMembers != null ? bookedMembers.size() : 0;
    }
    
    public String getClassDetails() {
        // ✅ FIXED: Use getName() instead of className
        return String.format("Yoga Class: %s | Style: %s | Booked: %d/%d",
            getName(), style, getBookedCount(), maxParticipants);
    }
    
    @Override
    public String toString() {
        // ✅ FIXED: Use getName() instead of className
        return String.format("Yoga{id='%s', name='%s', style='%s', booked=%d/%d}",
            getClassId(), getName(), style, getBookedCount(), maxParticipants);
    }
}