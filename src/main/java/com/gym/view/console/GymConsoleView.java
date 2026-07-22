package com.gym.view.console;

import com.gym.controller.GymController;
import com.gym.controller.MembershipController;

public class GymConsoleView {
    
    private GymController controller;
    private MembershipController membershipController;
    
    public GymConsoleView() {
        this.controller = new GymController();
        
        // ✅ FIX: Use getMembershipController() instead
        this.membershipController = controller.getMembershipController();
        // OR if you want to use getMembershipService():
        // this.membershipController = controller.getMembershipService();
    }
    
    public void displayMenu() {
        // Your console menu code
    }
}