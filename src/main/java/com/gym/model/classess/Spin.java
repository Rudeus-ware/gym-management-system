package com.gym.model.classess;

public class Spin implements GymClass {
    @Override
    public String getName() {
        return "Spin";
    }

    @Override
    public String getSchedule() {
        return "18:00";
    }

    @Override
    public int getCapacity() {
        return 15;
    }
}
