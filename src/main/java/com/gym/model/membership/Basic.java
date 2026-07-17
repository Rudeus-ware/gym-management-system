package com.gym.model.membership;

public class Basic implements Membership {
    @Override
    public String getName() {
        return "Basic";
    }

    @Override
    public double getPrice() {
        return 29.99;
    }

    @Override
    public String getDescription() {
        return "Access to gym floor and basic classes.";
    }
}
