package com.zeta.paynow_agent.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.paynow_agent.entites.IdempotencyEntity;
import com.zeta.paynow_agent.repository.IdempotencyRepository;
import com.zeta.paynow_agent.service.IdempotencyService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper  objectMapper;

    public IdempotencyServiceImpl(IdempotencyRepository idempotencyRepository, ObjectMapper objectMapper) {
        this.idempotencyRepository = idempotencyRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Optional<String> getCompletedResponse(Long customerId, String key) {
        return idempotencyRepository.findByCustomerIdAndIdempotencyKeyAndStatus(customerId, key, "COMPLETED")
                .map(IdempotencyEntity::getResponseJson);
    }

    @Override
    @Transactional
    public boolean tryInsertInProgress(Long customerId, String key) {
        try {
            IdempotencyEntity entity = new IdempotencyEntity();
            entity.setCustomerId(customerId);
            entity.setIdempotencyKey(key);
            entity.setStatus("IN_PROGRESS");
            idempotencyRepository.save(entity);
            return true;
        } catch (Exception e) {
            // unique constraint violation
            return false;
        }
    }

    @Transactional
    public void markCompleted(Long customerId, String key, Object responseObj) {
        idempotencyRepository.findByCustomerIdAndIdempotencyKeyAndStatus(customerId, key, "IN_PROGRESS")
                .ifPresent(entity -> {
                    try {
                        String json = objectMapper.writeValueAsString(responseObj);
                        entity.setStatus("COMPLETED");
                        entity.setResponseJson(json);
                        idempotencyRepository.save(entity);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to serialize response", e);
                    }
                });
        log.info("Transaction Completed");
    }
}
