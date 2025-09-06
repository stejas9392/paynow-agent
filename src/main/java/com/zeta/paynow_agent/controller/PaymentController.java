package com.zeta.paynow_agent.controller;

import com.zeta.paynow_agent.model.PaymentDecisionRequest;
import com.zeta.paynow_agent.model.PaymentDecisionResponse;
import com.zeta.paynow_agent.model.Result;
import com.zeta.paynow_agent.service.AgentOrchestrationService;
import com.zeta.paynow_agent.service.IdempotencyService;
import com.zeta.paynow_agent.service.MetricsService;
import com.zeta.paynow_agent.service.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@Slf4j
public class PaymentController {
    private final IdempotencyService idempotencyService;
    private final RateLimiterService rateLimiterService;
    private final AgentOrchestrationService agentOrchestrationService;
    private final MetricsService metricsService;

    public PaymentController(IdempotencyService idempotencyService, RateLimiterService rateLimiterService, AgentOrchestrationService agentOrchestrationService, MetricsService metricsService) {
        this.idempotencyService = idempotencyService;
        this.rateLimiterService = rateLimiterService;
        this.agentOrchestrationService = agentOrchestrationService;
        this.metricsService = metricsService;
    }

    @PostMapping("/decide")
    public ResponseEntity<?> decide(@RequestBody PaymentDecisionRequest paymentDecisionRequest){

        long start = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        Long customerId = paymentDecisionRequest.customerId();
        String idempotenceKey=paymentDecisionRequest.idempotencyKey();
        if(!rateLimiterService.allow(customerId)){
            log.error("Rate Limit exceeded");
            return  ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error","Rate Limit exceeded"));
        }
        var existing = idempotencyService.getCompletedResponse(customerId,idempotenceKey);
        if (existing.isPresent()) {
            return ResponseEntity.ok()
                    .header("X-Request-Id", requestId)
                    .body(existing.get());
        }
        if (!idempotencyService.tryInsertInProgress(customerId, idempotenceKey)) {
            log.warn("Duplicate in-progress request");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Duplicate in-progress request"));
        }
        Result  result = agentOrchestrationService.decide(customerId, paymentDecisionRequest.amount());
        long latency = System.currentTimeMillis() - start;
        metricsService.recordMetrics(result.decision(), latency);

        // Build response
        PaymentDecisionResponse response = new PaymentDecisionResponse(
                result.decision(),result.reason(),result.trace(),requestId
        );

        // Mark idempotency complete
        idempotencyService.markCompleted(customerId, idempotenceKey, response);
        log.info("response sent successfully");
        return ResponseEntity.ok()
                .header("X-Request-Id", requestId)
                .body(response);

    }
}
