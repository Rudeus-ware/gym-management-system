package com.gym.repository;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.user.User;

public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public void save(User user) {
        users.add(user);
    }

    public List<User> findAll() {
        return users;
    }
}
