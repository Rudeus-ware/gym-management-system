package com.gym.view.console;

import com.gym.controller.GymController;
import com.gym.model.membership.Membership;

public class GymConsoleView {
    private final GymController controller;

    public GymConsoleView(GymController controller) {
        this.controller = controller;
    }

    public void showWelcomeMessage() {
        System.out.println("Welcome to the Gym Management System");
        System.out.println("Available memberships:");
        for (String name : new String[]{"Basic", "Premium", "Family"}) {
            Membership membership = controller.getMembershipService().findByName(name);
            if (membership != null) {
                System.out.println("- " + membership.getName() + " ($" + membership.getPrice() + ")");
            }
        }
    }
}
