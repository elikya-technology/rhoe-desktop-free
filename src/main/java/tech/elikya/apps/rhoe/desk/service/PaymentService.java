/*
 * Copyright (c) 2020, Elikya Technology.
 */

package tech.elikya.apps.rhoe.desk.service;

import tech.elikya.apps.rhoe.desk.entity.Payment;
import tech.elikya.apps.rhoe.desk.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> getAll() {
        return repository.findAll();
    }

    public Payment save(Payment payment) {
        return repository.save(payment);
    }

    public Payment update(Payment payment) {
        return save(payment);
    }

    public void delete(Payment payment) {
        repository.delete(payment);
    }
}
