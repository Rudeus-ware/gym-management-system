package com.gym.model.classess;

public class Strength implements GymClass {
    @Override
    public String getName() {
        return "Strength";
    }

    @Override
    public String getSchedule() {
        return "19:30";
    }

    @Override
    public int getCapacity() {
        return 12;
    }
}
