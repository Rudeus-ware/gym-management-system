package com.gym.model.classess;

public class Yoga implements GymClass {
    @Override
    public String getName() {
        return "Yoga";
    }

    @Override
    public String getSchedule() {
        return "06:00";
    }

    @Override
    public int getCapacity() {
        return 20;
    }
}
