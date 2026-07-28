package com.gym.model.classes;

/**
 * GymClass model class
 */
public class GymClass {
    
    private String classId;
    private String name;           // ← IMPORTANT: This field exists
    private String description;
    private int duration;
    private String category;
    private String status;
    private int maxCapacity;
    private String schedule;
    private String trainer;
    protected int currentBookings;
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    public GymClass() {
        this("CLASS-DEFAULT", "Default Class", "Default description", 60, "GENERAL", 20, "ACTIVE");
    }

    public GymClass(String classId, String name, String description, int duration, String category) {
        this.classId = classId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.status = "ACTIVE";
        this.maxCapacity = 20;
        this.schedule = description;
        this.trainer = "TBD";
        this.currentBookings = 0;
    }
    
    public GymClass(String classId, String name, String description, int duration, 
                    String category, int maxCapacity) {
        this.classId = classId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.status = "ACTIVE";
        this.maxCapacity = maxCapacity;
        this.schedule = description;
        this.trainer = "TBD";
        this.currentBookings = 0;
    }
    
    public GymClass(String classId, String name, String description, int duration, 
                    String category, int maxCapacity, String status) {
        this.classId = classId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.status = status != null ? status : "ACTIVE";
        this.maxCapacity = maxCapacity;
        this.schedule = description;
        this.trainer = "TBD";
        this.currentBookings = 0;
    }
    
    // ============================================================
    // GETTERS - ADD getName() HERE
    // ============================================================
    
    public String getClassId() { 
        return classId; 
    }

    public String getClassName() {
        return name;
    }
    
    /**
     * Get the class name - ADD THIS METHOD
     */
    public String getName() {  // ← ADD THIS
        return name;
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public int getDuration() { 
        return duration; 
    }
    
    public String getCategory() { 
        return category; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public int getMaxCapacity() { 
        return maxCapacity; 
    }

    public int getCapacity() {
        return maxCapacity;
    }

    public int getCurrentBookings() { 
        return currentBookings; 
    }
    
    
    // ============================================================
    // SETTERS
    // ============================================================
    
    public void setClassId(String classId) { 
        this.classId = classId; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public void setDuration(int duration) { 
        this.duration = duration; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
    
    public void setMaxCapacity(int maxCapacity) { 
        this.maxCapacity = maxCapacity; 
    }
    
    public void setCurrentBookings(int currentBookings) { 
        this.currentBookings = currentBookings; 
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
        this.description = schedule;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }
    
    // ============================================================
    // BUSINESS METHODS
    // ============================================================
    
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }
    
    public boolean isFull() {
        return currentBookings >= maxCapacity;
    }
    
    public int getAvailableSpots() {
        return maxCapacity - currentBookings;
    }

    public String getSchedule() {
        return schedule != null ? schedule : description;
    }

    public String getTrainer() {
        return trainer != null ? trainer : "TBD";
    }

    public boolean checkAvailability() {
        return currentBookings < maxCapacity;
    }
    
    public boolean addBooking() {
        if (isFull()) {
            return false;
        }
        currentBookings++;
        return true;
    }
    
    public void addBooking(String memberId) {
    // Default implementation
    System.out.println("Booking added for member: " + memberId);
}
    public boolean cancelBooking() {
        if (currentBookings > 0) {
            currentBookings--;
            return true;
        }
        return false;
    }
    
    public String getStatusDisplay() {
        return switch (status) {
            case "ACTIVE" -> "✅ Active";
            case "CANCELLED" -> "❌ Cancelled";
            case "FULL" -> "🔴 Full";
            case "INACTIVE" -> "⏸️ Inactive";
            default -> "❓ " + status;
        };
    }
    
    public String getCategoryDisplay() {
        return switch (category) {
            case "YOGA" -> "🧘 Yoga";
            case "SPIN" -> "🚴 Spin";
            case "STRENGTH" -> "💪 Strength";
            case "CARDIO" -> "🏃 Cardio";
            case "PILATES" -> "🧘 Pilates";
            case "ZUMBA" -> "💃 Zumba";
            default -> "❓ " + category;
        };
    }
    
    public String getCapacityDisplay() {
        return String.format("%d/%d", currentBookings, maxCapacity);
    }

    public String getClassDetails() {
        return String.format("%s | Schedule: %s | Capacity: %d/%d",
            getName(), getSchedule(), getCurrentBookings(), getCapacity());
    }
    
    @Override
    public String toString() {
        return String.format("GymClass{id='%s', name='%s', category='%s', duration=%d, capacity=%d/%d}",
            classId, name, category, duration, currentBookings, maxCapacity);
    }
}