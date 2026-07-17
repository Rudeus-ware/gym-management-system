package com.gym.model.attendance;

public class Attendance {
    private final String memberId;
    private final String date;
    private final String status;

    public Attendance(String memberId, String date, String status) {
        this.memberId = memberId;
        this.date = date;
        this.status = status;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
