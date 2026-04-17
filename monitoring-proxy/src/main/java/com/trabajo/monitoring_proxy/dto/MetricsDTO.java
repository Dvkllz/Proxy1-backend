package com.trabajo.monitoring_proxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetricsDTO {
    private String serviceId;
    private long totalCalls;
    private double errorRatePercentage;
    private double avgResponseTimeMs;
}