package com.gym.repository;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.membership.Membership;

public class MembershipRepository {
    private final List<Membership> memberships = new ArrayList<>();

    public void save(Membership membership) {
        memberships.add(membership);
    }

    public List<Membership> findAll() {
        return memberships;
    }
}
