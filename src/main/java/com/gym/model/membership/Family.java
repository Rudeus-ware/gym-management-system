package com.gym.model.membership;

public class Family implements Membership {
    @Override
    public String getName() {
        return "Family";
    }

    @Override
    public double getPrice() {
        return 89.99;
    }

    @Override
    public String getDescription() {
        return "Covers family access for up to four members.";
    }
}
