package com.zeta.paynow_agent.service;

public interface RateLimiterService {
    boolean allow(Long customerId);
}
