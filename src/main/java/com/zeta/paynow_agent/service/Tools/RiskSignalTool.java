package com.zeta.paynow_agent.service.Tools;

import com.zeta.paynow_agent.model.ReserveBalanceRequest;
import com.zeta.paynow_agent.model.RiskSignals;
import com.zeta.paynow_agent.service.RiskSignalsService;
import org.springframework.stereotype.Service;

@Service
public class RiskSignalTool implements ToolService<RiskSignals> {

    private final RiskSignalsService riskSignalsService;

    public RiskSignalTool(RiskSignalsService riskSignalsService) {
        this.riskSignalsService = riskSignalsService;
    }

    @Override
    public String name() {
        return "getRiskSignals";
    }

    @Override
    public RiskSignals execute(Long customerId) {
        return riskSignalsService.getSignals(customerId);
    }

    @Override
    public String describe(RiskSignals result) {
        return "recent_disputes=" + result.recentDisputes()
                + ", device_change=" + result.deviceChange();
    }
}
