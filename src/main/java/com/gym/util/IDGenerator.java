package com.gym.util;

public class IDGenerator {
    private static int counter = 1000;

    public static String nextId() {
        return "ID-" + counter++;
    }
}
