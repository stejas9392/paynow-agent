package com.zeta.paynow_agent.model;

import java.math.BigDecimal;

public record ReserveBalanceRequest(Long customerId, BigDecimal amount) {
}
