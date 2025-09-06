package com.zeta.paynow_agent.service;

import com.zeta.paynow_agent.model.Result;

import java.math.BigDecimal;

public interface AgentOrchestrationService {

    Result decide(Long customerId, BigDecimal amount);

}
