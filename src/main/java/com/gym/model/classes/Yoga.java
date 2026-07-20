package com.gym.model.classes;

public class Yoga extends GymClass {
    // Yoga-specific attributes
    private String yogaStyle;  // Hatha, Vinyasa, Ashtanga, etc.
    private String difficulty;  // Beginner, Intermediate, Advanced
    
    // Constructor
    public Yoga(int classId, String className, String schedule, int capacity, 
                String trainer, String yogaStyle, String difficulty) {
        super(classId, className, schedule, capacity, trainer);
        this.yogaStyle = yogaStyle;
        this.difficulty = difficulty;
    }
    
    // GETTERS & SETTERS
    public String getYogaStyle() { return yogaStyle; }
    public void setYogaStyle(String yogaStyle) { this.yogaStyle = yogaStyle; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    // IMPLEMENT ABSTRACT METHODS
    @Override
    public void addBooking(String memberName) {
        if (checkAvailability()) {
            bookedMembers.add(memberName);
            currentBookings++;
            System.out.println("🧘 " + memberName + " booked Yoga class: " + className);
            System.out.println("   Style: " + yogaStyle + " | Difficulty: " + difficulty);
            System.out.println("   Spots remaining: " + (capacity - currentBookings));
        } else {
            System.out.println("❌ Yoga class is full! Cannot book " + memberName);
        }
    }
    
    @Override
    public void removeBooking(String memberName) {
        if (bookedMembers.remove(memberName)) {
            currentBookings--;
            System.out.println("🗑️ " + memberName + " removed from Yoga class: " + className);
            System.out.println("   Spots now available: " + (capacity - currentBookings));
        } else {
            System.out.println("⚠️ " + memberName + " not found in this Yoga class");
        }
    }
    
    @Override
    public boolean checkAvailability() {
        boolean available = currentBookings < capacity;
        if (available) {
            System.out.println("✅ Yoga class has " + (capacity - currentBookings) + " spots available");
        } else {
            System.out.println("❌ Yoga class is fully booked");
        }
        return available;
    }
    
    // YOGA-SPECIFIC METHODS
    public void setClassToBeginner() {
        this.difficulty = "Beginner";
        System.out.println("🧘 Yoga class set to Beginner difficulty");
    }
    
    public void setClassToAdvanced() {
        this.difficulty = "Advanced";
        System.out.println("🧘 Yoga class set to Advanced difficulty");
    }
    
    @Override
    public String getClassDetails() {
        return super.getClassDetails() +
               "\nYoga Style: " + yogaStyle +
               "\nDifficulty Level: " + difficulty +
               "\nClass Type: Yoga";
    }
}