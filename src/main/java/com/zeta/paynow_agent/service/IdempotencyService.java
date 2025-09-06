package com.zeta.paynow_agent.service;

import java.util.Optional;

public interface IdempotencyService {
    Optional<String> getCompletedResponse(Long customerId, String key);
    boolean tryInsertInProgress(Long customerId,String key);
    void markCompleted(Long customerId,String key,Object responseObj);
}
