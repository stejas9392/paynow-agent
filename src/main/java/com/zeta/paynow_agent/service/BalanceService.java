package com.zeta.paynow_agent.service;

import java.math.BigDecimal;
import java.util.Optional;

public interface BalanceService {
    BigDecimal getbalanceCents(Long CustomerId);
    boolean reserveIfSufficient(Long CustomerId, BigDecimal amountCents);
}
