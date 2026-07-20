package com.gym.model.classes;

import java.util.ArrayList;
import java.util.List;

public abstract class GymClass {
    // Common attributes for ALL class types
    protected int classId;
    protected String className;
    protected String schedule;
    protected int capacity;
    protected String trainer;
    protected List<String> bookedMembers;
    protected int currentBookings;
    
    // Constructor
    public GymClass(int classId, String className, String schedule, int capacity, String trainer) {
        this.classId = classId;
        this.className = className;
        this.schedule = schedule;
        this.capacity = capacity;
        this.trainer = trainer;
        this.bookedMembers = new ArrayList<>();
        this.currentBookings = 0;
    }
    
    // GETTERS
    public int getClassId() { return classId; }
    public String getClassName() { return className; }
    public String getSchedule() { return schedule; }
    public int getCapacity() { return capacity; }
    public String getTrainer() { return trainer; }
    public int getCurrentBookings() { return currentBookings; }
    public List<String> getBookedMembers() { 
        return new ArrayList<>(bookedMembers); 
    }
    
    // SETTERS
    public void setClassId(int classId) { this.classId = classId; }
    public void setClassName(String className) { this.className = className; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setTrainer(String trainer) { this.trainer = trainer; }
    
    // ABSTRACT METHODS (must be implemented by subclasses)
    public abstract void addBooking(String memberName);
    public abstract void removeBooking(String memberName);
    public abstract boolean checkAvailability();
    
    // CONCRETE METHODS (shared by all classes)
    public String getClassDetails() {
        return "Class ID: " + classId +
               "\nClass Name: " + className +
               "\nType: " + this.getClass().getSimpleName() +
               "\nSchedule: " + schedule +
               "\nTrainer: " + trainer +
               "\nCapacity: " + capacity +
               "\nCurrent Bookings: " + currentBookings +
               "\nAvailable Spots: " + (capacity - currentBookings);
    }
    
    public boolean isFull() {
        return currentBookings >= capacity;
    }
    
    public void displayBookedMembers() {
        if (bookedMembers.isEmpty()) {
            System.out.println("No members booked for this class.");
        } else {
            System.out.println("📋 Booked Members:");
            for (String member : bookedMembers) {
                System.out.println("   • " + member);
            }
        }
    }
}