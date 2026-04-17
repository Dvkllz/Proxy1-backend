package com.trabajo.monitoring_proxy.controller;

import com.trabajo.monitoring_proxy.dto.MetricsDTO;
import com.trabajo.monitoring_proxy.model.LogEntity;
import com.trabajo.monitoring_proxy.proxy.LoggingProxy;
import com.trabajo.monitoring_proxy.repository.LogRepository;
import com.trabajo.monitoring_proxy.service.InventoryService;
import com.trabajo.monitoring_proxy.service.OrderService;
import com.trabajo.monitoring_proxy.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite llamadas desde cualquier frontend
@RequiredArgsConstructor
public class MonitoringController {

    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final LogRepository logRepository;

    // --- ENDPOINTS DE LOS SERVICIOS (USANDO PROXY) ---

    @PostMapping("/services/inventory/{operation}")
    public ResponseEntity<?> callInventory(@PathVariable String operation, @RequestBody(required = false) Object[] params) {
        return executeProxy(new LoggingProxy<>(inventoryService, "InventoryService", logRepository), operation, params);
    }

    @PostMapping("/services/orders/{operation}")
    public ResponseEntity<?> callOrders(@PathVariable String operation, @RequestBody(required = false) Object[] params) {
        return executeProxy(new LoggingProxy<>(orderService, "OrderService", logRepository), operation, params);
    }

    @PostMapping("/services/payments/{operation}")
    public ResponseEntity<?> callPayments(@PathVariable String operation, @RequestBody(required = false) Object[] params) {
        return executeProxy(new LoggingProxy<>(paymentService, "PaymentService", logRepository), operation, params);
    }

    private ResponseEntity<?> executeProxy(LoggingProxy<?> proxy, String operation, Object[] params) {
        try {
            Object result = proxy.execute(operation, params != null ? params : new Object[]{});
            return ResponseEntity.ok(Collections.singletonMap("result", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // --- ENDPOINTS DEL DASHBOARD Y MÉTRICAS ---

    @GetMapping("/metrics/summary")
    public ResponseEntity<List<MetricsDTO>> getSummary() {
        List<LogEntity> allLogs = logRepository.findAll();
        Map<String, List<LogEntity>> grouped = allLogs.stream().collect(Collectors.groupingBy(LogEntity::getServiceId));

        List<MetricsDTO> summary = new ArrayList<>();
        grouped.forEach((serviceId, logs) -> {
            long total = logs.size();
            long errors = logs.stream().filter(l -> "ERROR".equals(l.getStatus())).count();
            double avgTime = logs.stream().mapToLong(LogEntity::getDurationMs).average().orElse(0.0);
            double errorRate = (total == 0) ? 0 : ((double) errors / total) * 100;
            summary.add(new MetricsDTO(serviceId, total, errorRate, avgTime));
        });

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/metrics/logs")
    public ResponseEntity<Page<LogEntity>> getLogsFiltered(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable) {
        return ResponseEntity.ok(logRepository.findFilteredLogs(service, status, from, to, pageable));
    }

    @PostMapping("/metrics/simulate-load")
    public ResponseEntity<?> simulateLoad() {
        Random rand = new Random();
        String[] services = {"Inventory", "Order", "Payment"};

        for (int i = 0; i < 50; i++) {
            String svc = services[rand.nextInt(services.length)];
            try {
                if (svc.equals("Inventory")) {
                    new LoggingProxy<>(inventoryService, "InventoryService", logRepository)
                            .execute("checkStock", "ITEM-" + rand.nextInt(100));
                } else if (svc.equals("Order")) {
                    new LoggingProxy<>(orderService, "OrderService", logRepository)
                            .execute("createOrder", "User" + rand.nextInt(10), "ITEM");
                } else {
                    new LoggingProxy<>(paymentService, "PaymentService", logRepository)
                            .execute("processPayment", "ORD-" + rand.nextInt(1000), String.valueOf(rand.nextInt(500)));
                }
            } catch (Exception ignored) {} // Se ignora porque el Proxy ya guardó el log en H2
        }
        return ResponseEntity.ok(Collections.singletonMap("message", "50 llamadas simuladas exitosamente"));
    }
}