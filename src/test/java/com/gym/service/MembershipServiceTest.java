package com.gym.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.gym.model.membership.Basic;
import com.gym.model.membership.Membership;

class MembershipServiceTest {
    @Test
    void shouldCreateAndReturnMemberships() {
        MembershipService service = new MembershipService();
        service.addMembership(new Basic());

        Membership membership = service.findByName("Basic");
        assertNotNull(membership);
        assertEquals("Basic", membership.getName());
    }
}
