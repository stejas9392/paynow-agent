package com.zeta.paynow_agent.service.Tools;

import com.zeta.paynow_agent.model.ReserveBalanceRequest;
import com.zeta.paynow_agent.model.RiskSignals;

public interface ToolService<T>{
    String name();
    T execute(Long customerId);
    String describe(T result);

}
