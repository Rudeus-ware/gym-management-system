package com.gym.model;

import com.gym.model.membership.Basic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class ProfileTest {
    
    @Test
    public void testProfileCreation() {
        Profile profile = new Profile(1, "John Doe", "john@email.com", "555-1234", "123 Main St");
        assertEquals(1, profile.getProfileId());
        assertEquals("John Doe", profile.getName());
        assertEquals("john@email.com", profile.getEmail());
    }
    
    @Test
    public void testUpdateProfile() {
        Profile profile = new Profile(1, "John Doe", "john@email.com", "555-1234", "123 Main St");
        profile.updateProfile("Jane Doe", "jane@email.com", "555-5678", "456 Oak St");
        assertEquals("Jane Doe", profile.getName());
        assertEquals("jane@email.com", profile.getEmail());
    }
    
    @Test
    public void testMembershipValidation() {
        Basic membership = new Basic(1001, 49.99, "2026-01-01", "2026-12-31", "Active");
        assertTrue(membership.isValid());
        
        membership.setStatus("Inactive");
        assertFalse(membership.isValid());
        
        membership.setStatus("Active");
        membership.setExpiryDate("2025-12-31");
        assertFalse(membership.isValid());
    }
    
    @Test
    public void testMembershipRenewal() {
        Basic membership = new Basic(1001, 49.99, "2026-01-01", "2026-12-31", "Active");
        String oldExpiry = membership.getExpiryDate();
        membership.renew();
        assertNotEquals(oldExpiry, membership.getExpiryDate());
        assertTrue(membership.isValid());
    }
}
