package com.zeta.paynow_agent.repository;

import com.zeta.paynow_agent.entites.BalanceEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity,Long> {
    @Modifying
    @Transactional
    @Query("""
        UPDATE BalanceEntity b
           SET b.balanceCents = b.balanceCents - :amount
         WHERE b.customerId = :customerId
           AND b.balanceCents >= :amount
    """)
    int reserveIfSufficient(@Param("customerId") Long customerId, @Param("amount") BigDecimal amount);

    Optional<BalanceEntity> findByCustomerId(Long customerId);

    void deleteByCustomerId(Long testCustomerId);
}
