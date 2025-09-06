package com.zeta.paynow_agent.service.serviceImpl;

import com.zeta.paynow_agent.model.Decision;
import com.zeta.paynow_agent.service.MetricsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsServiceImpl implements MetricsService {
    private final AtomicLong total = new AtomicLong();
    private final Map<Decision, AtomicLong> decisionCounts = new ConcurrentHashMap<>();
    private final Deque<Long> latencies = new ArrayDeque<>();
    private final int window = 500;

    public void recordMetrics(Decision d, long latencyMs) {
        total.incrementAndGet();
        decisionCounts.computeIfAbsent(d, k -> new AtomicLong()).incrementAndGet();
        synchronized (latencies) {
            if (latencies.size() >= window) latencies.removeFirst();
            latencies.addLast(latencyMs);
        }
    }
    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalRequests", total.get());
        Map<String, Long> dc = new LinkedHashMap<>();
        for (Decision d : Decision.values()) {
            dc.put(d.name().toLowerCase(),
                    decisionCounts.getOrDefault(d, new AtomicLong(0)).get());
        }
        m.put("decisionCounts", dc);
        m.put("p95LatencyMs", p95());
        return m;
    }

    private long p95() {
        List<Long> copy;
        synchronized (latencies) { copy = new ArrayList<>(latencies); }
        if (copy.isEmpty()) return 0;
        Collections.sort(copy);
        int idx = (int) Math.ceil(0.95 * copy.size()) - 1;
        return copy.get(Math.max(0, idx));
    }
}
