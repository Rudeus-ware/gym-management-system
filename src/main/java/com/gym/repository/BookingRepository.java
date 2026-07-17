package com.gym.repository;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.booking.Booking;

public class BookingRepository {
    private final List<Booking> bookings = new ArrayList<>();

    public void save(Booking booking) {
        bookings.add(booking);
    }

    public List<Booking> findAll() {
        return bookings;
    }
}
