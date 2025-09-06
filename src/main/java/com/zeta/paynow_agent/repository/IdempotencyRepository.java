package com.zeta.paynow_agent.repository;

import com.zeta.paynow_agent.entites.IdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRepository extends JpaRepository<IdempotencyEntity,Long> {
    Optional<IdempotencyEntity> findByCustomerIdAndIdempotencyKeyAndStatus(
            Long customerId, String idempotencyKey, String status);
}
