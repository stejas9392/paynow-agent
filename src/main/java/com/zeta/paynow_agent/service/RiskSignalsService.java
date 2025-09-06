package com.zeta.paynow_agent.service;


import com.zeta.paynow_agent.model.RiskSignals;

public interface RiskSignalsService {
    RiskSignals getSignals(Long customerId);
}
