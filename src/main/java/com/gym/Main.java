package com.gym;

import com.gym.controller.GymController;
import com.gym.view.console.GymConsoleView;

public class Main {
    public static void main(String[] args) {
        GymController controller = new GymController();
        GymConsoleView view = new GymConsoleView(controller);
        view.showWelcomeMessage();
    }
}
