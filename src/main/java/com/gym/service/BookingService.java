package com.gym.service;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.booking.Booking;

public class BookingService {
    private final List<Booking> bookings = new ArrayList<>();

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public List<Booking> getBookings() {
        return bookings;
    }
}
