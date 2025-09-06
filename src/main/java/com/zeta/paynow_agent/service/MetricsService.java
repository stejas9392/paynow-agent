package com.zeta.paynow_agent.service;

import com.zeta.paynow_agent.model.Decision;

import java.util.Map;

public interface MetricsService {
    Map<String, Object> snapshot();
    void recordMetrics(Decision d, long latencyMs);
}
