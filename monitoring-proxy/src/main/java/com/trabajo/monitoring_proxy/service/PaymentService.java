package com.trabajo.monitoring_proxy.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class PaymentService {
    private final Random random = new Random();

    public String processPayment(String orderId, String amount) {
        if (random.nextInt(100) < 10) {
            throw new RuntimeException("Network timeout while contacting bank for order " + orderId);
        }
        return "Payment of $" + amount + " processed for order " + orderId;
    }
}