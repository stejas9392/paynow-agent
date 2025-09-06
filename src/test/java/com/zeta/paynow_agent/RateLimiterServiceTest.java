package com.zeta.paynow_agent;

import com.zeta.paynow_agent.service.RateLimiterService;
import com.zeta.paynow_agent.service.serviceImpl.RateLimiterServiceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RateLimiterServiceTest {
    @Test
    void rejectsMoreThan5PerSecond() {
        RateLimiterService limiter = new RateLimiterServiceImpl(5);

        Long customerId = 1002L;

        // First 5 calls should be allowed
        for (int i = 1; i <= 5; i++) {
            assertThat(limiter.allow(customerId)).isTrue();
        }

        // Next calls should be rejected
        for (int i = 6; i <= 10; i++) {
            assertThat(limiter.allow(customerId)).isFalse();
        }
    }

}
