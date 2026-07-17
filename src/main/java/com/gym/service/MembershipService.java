package com.gym.service;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.membership.Membership;

public class MembershipService {
    private final List<Membership> memberships = new ArrayList<>();

    public void addMembership(Membership membership) {
        memberships.add(membership);
    }

    public Membership findByName(String name) {
        return memberships.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
