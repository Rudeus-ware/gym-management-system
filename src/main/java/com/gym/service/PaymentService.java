package com.gym.service;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.payment.Payment;

public class PaymentService {
    private final List<Payment> payments = new ArrayList<>();

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    public List<Payment> getPayments() {
        return payments;
    }
}
