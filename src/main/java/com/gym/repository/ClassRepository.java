package com.gym.repository;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.classess.GymClass;

public class ClassRepository {
    private final List<GymClass> classes = new ArrayList<>();

    public void save(GymClass gymClass) {
        classes.add(gymClass);
    }

    public List<GymClass> findAll() {
        return classes;
    }
}
