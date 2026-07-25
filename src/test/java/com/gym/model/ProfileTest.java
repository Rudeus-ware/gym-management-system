package com.gym.model;

import com.gym.model.membership.Basic;
import com.gym.model.membership.Premium;
import com.gym.model.membership.Family;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProfileTest {
    
    @Test
    public void testProfileCreation() {
        Profile profile = new Profile("000072422", "John Doe", "john@email.com", "555-1234", "123 Main St");
        assertEquals("000072422", profile.getProfileId());
        assertEquals("John Doe", profile.getName());
        assertEquals("john@email.com", profile.getEmail());
    }
    
    @Test
    public void testProfileCreationWithDifferentIds() {
        Profile admin = new Profile("000072400", "Admin User", "admin@gym.com", "555-0000", "Admin Office");
        Profile trainer = new Profile("001072411", "Trainer User", "trainer@gym.com", "555-1111", "Trainer Office");
        Profile member = new Profile("002072422", "Member User", "member@gym.com", "555-2222", "Member Home");
        
        assertEquals("000072400", admin.getProfileId());
        assertEquals("001072411", trainer.getProfileId());
        assertEquals("002072422", member.getProfileId());
    }
    
    @Test
    public void testUpdateProfile() {
        Profile profile = new Profile("000072422", "John Doe", "john@email.com", "555-1234", "123 Main St");
        profile.updateProfile("Jane Doe", "jane@email.com", "555-5678", "456 Oak St");
        
        assertEquals("Jane Doe", profile.getName());
        assertEquals("jane@email.com", profile.getEmail());
        assertEquals("555-5678", profile.getPhone());
        assertEquals("456 Oak St", profile.getAddress());
        assertEquals("000072422", profile.getProfileId());
    }
    
    @Test
    public void testProfileStatus() {
        Profile profile = new Profile("000072422", "John Doe", "john@email.com", "555-1234", "123 Main St");
        assertTrue(profile.isActive());
        
        profile.setActive(false);
        assertFalse(profile.isActive());
        
        profile.setActive(true);
        assertTrue(profile.isActive());
    }
    
    @Test
    public void testProfileWithMembership() {
        Profile profile = new Profile("000072422", "John Doe", "john@email.com", "555-1234", "123 Main St");
        Basic membership = new Basic("MEM072400", 49.99, "2026-01-01", "2026-12-31", "Active");
        profile.setMembership(membership);
        
        assertNotNull(profile.getMembership());
        assertEquals("MEM072400", profile.getMembership().getMembershipId());
        assertEquals(49.99, profile.getMembership().calculateFee());
    }
    
    @Test
    public void testMembershipValidation() {
        Basic membership = new Basic("MEM072401", 49.99, "2026-01-01", "2026-12-31", "Active");
        assertTrue(membership.isValid());
        
        membership.setStatus("Inactive");
        assertFalse(membership.isValid());
        
        membership.setStatus("Active");
        membership.setExpiryDate("2025-12-31");
        assertFalse(membership.isValid());
    }
    
    @Test
    public void testMembershipRenewal() {
        Basic membership = new Basic("MEM072402", 49.99, "2026-01-01", "2026-12-31", "Active");
        String oldExpiry = membership.getExpiryDate();
        membership.renew();
        
        assertNotEquals(oldExpiry, membership.getExpiryDate());
        assertTrue(membership.isValid());
        assertEquals("MEM072402", membership.getMembershipId());
    }
    
    @Test
    public void testMembershipWithDifferentTypes() {
        Profile profile = new Profile("000072422", "John Doe", "john@email.com", "555-1234", "123 Main St");
        
        Basic basic = new Basic("MEM072400", 49.99, "2026-01-01", "2026-12-31", "Active");
        profile.setMembership(basic);
        assertEquals("Basic", profile.getMembership().getClass().getSimpleName());
        assertEquals(49.99, profile.getMembership().calculateFee());
        
        Premium premium = new Premium("MEM072401", 99.99, "2026-01-01", "2026-12-31", "Active", "VIP Access");
        profile.setMembership(premium);
        assertEquals("Premium", profile.getMembership().getClass().getSimpleName());
        assertEquals(99.99, profile.getMembership().calculateFee());
        
        Family family = new Family("MEM072402", 69.99, "2026-01-01", "2026-12-31", "Active", 4);
        profile.setMembership(family);
        assertEquals("Family", profile.getMembership().getClass().getSimpleName());
        assertEquals(69.99, profile.getMembership().calculateFee());
    }
    
    @Test
    public void testProfileIdFormatValidation() {
        String[] validIds = {
            "000072400",  // Admin
            "001072411",  // Trainer
            "002072422",  // Member
            "999072400"   // Max counter
        };
        
        for (String id : validIds) {
            Profile profile = new Profile(id, "Test User", "test@email.com", "555-0000", "Test Address");
            assertEquals(id, profile.getProfileId());
        }
    }
    
    @Test
    public void testViewProfileWithMembership() {
        Profile profile = new Profile("000072422", "John Doe", "john@email.com", "555-1234", "123 Main St");
        Basic membership = new Basic("MEM072400", 49.99, "2026-01-01", "2026-12-31", "Active");
        profile.setMembership(membership);
        
        String view = profile.viewProfile();
        
        assertTrue(view.contains("Profile ID: 000072422"));
        assertTrue(view.contains("Name: John Doe"));
        assertTrue(view.contains("Email: john@email.com"));
        assertTrue(view.contains("MEMBERSHIP INFO"));
        assertTrue(view.contains("MEM072400"));
        assertTrue(view.contains("Basic"));
        assertTrue(view.contains("$49.99"));
    }
}