package com.zeta.paynow_agent.controller;

import com.zeta.paynow_agent.service.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@Slf4j
public class MetricsController {
    private final MetricsService metrics;

    public MetricsController(MetricsService metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        log.info("request came to metrics");
        return metrics.snapshot();
    }
}
