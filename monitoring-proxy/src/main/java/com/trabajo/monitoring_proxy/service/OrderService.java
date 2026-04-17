package com.trabajo.monitoring_proxy.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    public String createOrder(String customer, String item) {
        return "Order created for " + customer + " buying " + item;
    }
}