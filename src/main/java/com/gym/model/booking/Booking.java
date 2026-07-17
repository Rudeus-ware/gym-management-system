package com.gym.model.booking;

public class Booking {
    private final String bookingId;
    private final String memberId;
    private final String className;

    public Booking(String bookingId, String memberId, String className) {
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.className = className;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getClassName() {
        return className;
    }
}
