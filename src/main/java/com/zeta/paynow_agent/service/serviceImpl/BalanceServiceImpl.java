package com.zeta.paynow_agent.service.serviceImpl;

import com.zeta.paynow_agent.entites.BalanceEntity;
import com.zeta.paynow_agent.repository.BalanceRepository;
import com.zeta.paynow_agent.service.BalanceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceServiceImpl(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    @Transactional
    public BigDecimal getbalanceCents(Long customerId) {
        BigDecimal balance= balanceRepository.findByCustomerId(customerId).map(BalanceEntity::getBalanceCents).get();
        log.info("balance fetched successfully");
        return balance;

    }

    @Override
    @Transactional
    public boolean reserveIfSufficient(Long customerId, BigDecimal amountCents) {
        return balanceRepository.reserveIfSufficient(customerId,amountCents)==1;
    }
}
