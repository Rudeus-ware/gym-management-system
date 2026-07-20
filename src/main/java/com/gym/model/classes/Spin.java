package com.gym.model.classes;

public class Spin extends GymClass {
    // Spin-specific attributes
    private String intensity;  // Low, Medium, High
    private int durationMinutes;
    private String musicType;
    
    // Constructor
    public Spin(int classId, String className, String schedule, int capacity, 
                String trainer, String intensity, int durationMinutes, String musicType) {
        super(classId, className, schedule, capacity, trainer);
        this.intensity = intensity;
        this.durationMinutes = durationMinutes;
        this.musicType = musicType;
    }
    
    // GETTERS & SETTERS
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getMusicType() { return musicType; }
    public void setMusicType(String musicType) { this.musicType = musicType; }
    
    // IMPLEMENT ABSTRACT METHODS
    @Override
    public void addBooking(String memberName) {
        if (checkAvailability()) {
            bookedMembers.add(memberName);
            currentBookings++;
            System.out.println("🚴 " + memberName + " booked Spin class: " + className);
            System.out.println("   Intensity: " + intensity + " | Duration: " + durationMinutes + " min");
            System.out.println("   Music: " + musicType);
            System.out.println("   Spots remaining: " + (capacity - currentBookings));
        } else {
            System.out.println("❌ Spin class is full! Cannot book " + memberName);
        }
    }
    
    @Override
    public void removeBooking(String memberName) {
        if (bookedMembers.remove(memberName)) {
            currentBookings--;
            System.out.println("🗑️ " + memberName + " removed from Spin class: " + className);
            System.out.println("   Spots now available: " + (capacity - currentBookings));
        } else {
            System.out.println("⚠️ " + memberName + " not found in this Spin class");
        }
    }
    
    @Override
    public boolean checkAvailability() {
        boolean available = currentBookings < capacity;
        if (available) {
            System.out.println("✅ Spin class has " + (capacity - currentBookings) + " spots available");
        } else {
            System.out.println("❌ Spin class is fully booked");
        }
        return available;
    }
    
    // SPIN-SPECIFIC METHODS
    public void increaseIntensity() {
        if (intensity.equals("Low")) {
            intensity = "Medium";
        } else if (intensity.equals("Medium")) {
            intensity = "High";
        } else {
            System.out.println("Already at maximum intensity!");
            return;
        }
        System.out.println("⚡ Spin class intensity increased to: " + intensity);
    }
    
    public void decreaseIntensity() {
        if (intensity.equals("High")) {
            intensity = "Medium";
        } else if (intensity.equals("Medium")) {
            intensity = "Low";
        } else {
            System.out.println("Already at minimum intensity!");
            return;
        }
        System.out.println("⚡ Spin class intensity decreased to: " + intensity);
    }
    
    @Override
    public String getClassDetails() {
        return super.getClassDetails() +
               "\nIntensity: " + intensity +
               "\nDuration: " + durationMinutes + " minutes" +
               "\nMusic Type: " + musicType +
               "\nClass Type: Spin";
    }
}