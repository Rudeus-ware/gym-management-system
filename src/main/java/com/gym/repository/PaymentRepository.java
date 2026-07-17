package com.gym.repository;

import java.util.ArrayList;
import java.util.List;

import com.gym.model.payment.Payment;

public class PaymentRepository {
    private final List<Payment> payments = new ArrayList<>();

    public void save(Payment payment) {
        payments.add(payment);
    }

    public List<Payment> findAll() {
        return payments;
    }
}
