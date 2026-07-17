package com.gym.model.user;

public class Profile {
    private String phoneNumber;
    private String address;

    public Profile(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }
}
