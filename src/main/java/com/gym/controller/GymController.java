package com.gym.controller;

import com.gym.model.membership.Basic;
import com.gym.model.membership.Family;
import com.gym.model.membership.Premium;
import com.gym.service.MembershipService;

public class GymController {
    private final MembershipService membershipService = new MembershipService();

    public GymController() {
        membershipService.addMembership(new Basic());
        membershipService.addMembership(new Premium());
        membershipService.addMembership(new Family());
    }

    public MembershipService getMembershipService() {
        return membershipService;
    }
}
