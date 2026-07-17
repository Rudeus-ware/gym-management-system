package com.gym.model.membership;

public class Premium implements Membership {
    @Override
    public String getName() {
        return "Premium";
    }

    @Override
    public double getPrice() {
        return 59.99;
    }

    @Override
    public String getDescription() {
        return "Includes premium classes and locker access.";
    }
}
