package com.gym.persistence;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.gym.model.user.User;
import com.gym.model.Profile;

/**
 * JSON Exclusion Strategy to handle inheritance conflicts
 * Excludes duplicate fields from subclasses
 */
public class JsonExclusionStrategy implements ExclusionStrategy {
    
    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        // If the field is 'isActive' and the declaring class is User, skip it
        if (field.getName().equals("isActive")) {
            Class<?> declaringClass = field.getDeclaringClass();
            // Skip isActive if it's from User (since Profile already has it)
            if (declaringClass.equals(User.class)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
