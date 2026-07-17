package com.gym.service;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.user.User;

public class UserService {
    private final List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }
}
