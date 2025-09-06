package com.zeta.paynow_agent.service.Tools;

import com.zeta.paynow_agent.model.RiskSignals;
import com.zeta.paynow_agent.service.BalanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceTool implements ToolService<BigDecimal> {

    private final BalanceService balanceService;

    public BalanceTool(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public String name() {
        return "getBalance";
    }

    @Override
    public BigDecimal execute(Long customerId) {
        return balanceService.getbalanceCents(customerId);
    }

    @Override
    public String describe(BigDecimal result) {
        return "balance=" + result;
    }
}

