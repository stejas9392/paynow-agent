package com.zeta.paynow_agent.service.serviceImpl;

import com.zeta.paynow_agent.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {
    private static class Bucket { double tokens; long lastRefillNs; }

    private final ConcurrentMap<Long, Bucket> buckets = new ConcurrentHashMap<>();
    private final double ratePerSec;

    public RateLimiterServiceImpl(@Value("${app.rate-limit.per-second:5}") int perSecond) {
        this.ratePerSec = perSecond;
    }

    @Override
    public boolean allow(Long customerId) {
        Bucket b = buckets.computeIfAbsent(customerId, id -> {
            Bucket nb = new Bucket();
            nb.tokens = ratePerSec;
            nb.lastRefillNs = System.nanoTime();
            return nb;
        });
        synchronized (b) {
            long now = System.nanoTime();
            double elapsedSec = (now - b.lastRefillNs) / 1_000_000_000.0;
            double refill = elapsedSec * ratePerSec;
            b.tokens = Math.min(ratePerSec, b.tokens + refill);
            b.lastRefillNs = now;
            if (b.tokens >= 1.0) { b.tokens -= 1.0; return true; }
            return false;
        }
    }
}
