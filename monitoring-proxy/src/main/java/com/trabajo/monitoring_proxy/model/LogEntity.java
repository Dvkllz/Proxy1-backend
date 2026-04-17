package com.trabajo.monitoring_proxy.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class LogEntity {

    @Id
    private String requestId = UUID.randomUUID().toString();

    private String serviceId;
    private String operation;
    private long durationMs;
    private String status; // "SUCCESS" o "ERROR"

    @Column(length = 2000)
    private String errorMessage;

    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(length = 1000)
    private String requestParams;

    @Column(length = 2000)
    private String responseData;
}