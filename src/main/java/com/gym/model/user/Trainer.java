package com.gym.model.user;

import java.util.ArrayList;
import java.util.List;

public class Trainer extends User {
    private String specialization;
    private List<String> assignedClasses;
    
    // Constructor
    public Trainer(int profileId, String name, String email, String phone, String address,
                   String userId, String password, String specialization) {
        super(profileId, name, email, phone, address, userId, password);
        this.specialization = specialization;
        this.assignedClasses = new ArrayList<>();
    }
    
    // GETTERS & SETTERS
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public List<String> getAssignedClasses() { 
        return new ArrayList<>(assignedClasses); 
    }
    
    // TRAINER-SPECIFIC METHODS
    public void assignClass(String className) {
        if (!assignedClasses.contains(className)) {
            assignedClasses.add(className);
        }
        System.out.println("Class " + className + " assigned to trainer " + getName());
    }
    
    public void removeClass(String className) {
        assignedClasses.remove(className);
        System.out.println("Class " + className + " removed from trainer " + getName());
    }
    
    public void updateSchedule(String className, String newSchedule) {
        System.out.println("Schedule for " + className + " updated to: " + newSchedule);
    }
    
    // Override resetPassword if needed
    @Override
    public void resetPassword(String newPassword) {
        super.resetPassword(newPassword);
        System.out.println("Trainer password updated!");
    }
    
    // Override viewProfile
    @Override
    public String viewProfile() {
        return super.viewProfile() + 
               "\nRole: Trainer" +
               "\nSpecialization: " + specialization +
               "\nAssigned Classes: " + assignedClasses;
    }
}