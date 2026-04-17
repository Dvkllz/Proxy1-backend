package com.trabajo.monitoring_proxy.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    public String checkStock(String productId) {
        return "Stock available for product: " + productId;
    }
}