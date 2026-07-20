package com.gym.service;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.classes.GymClass;

public class ClassService {
    private final List<GymClass> classes = new ArrayList<>();

    public void addClass(GymClass gymClass) {
        classes.add(gymClass);
    }

    public List<GymClass> getClasses() {
        return classes;
    }
}
