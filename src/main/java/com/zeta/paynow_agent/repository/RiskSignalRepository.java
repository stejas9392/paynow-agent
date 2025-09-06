package com.zeta.paynow_agent.repository;

import com.zeta.paynow_agent.entites.RiskSignalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskSignalRepository extends JpaRepository<RiskSignalEntity,Long> {

    Optional<RiskSignalEntity> findByCustomerId(Long customerId);

    void deleteByCustomerId(Long testCustomerId);
}
