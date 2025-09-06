package com.zeta.paynow_agent.service.Tools;

import com.zeta.paynow_agent.model.ReserveBalanceRequest;
import com.zeta.paynow_agent.service.BalanceService;
import org.springframework.stereotype.Service;

@Service
public class ReserveBalanceTool implements ToolService<Boolean>{

    private final BalanceService balanceService;

    public ReserveBalanceTool(BalanceService balanceService) {
        this.balanceService = balanceService;

    }

    @Override
    public String name() {
        return "reserveBalance";
    }

    @Override
    public Boolean execute(Long customerId) {
        return null;
    }

    public Boolean execute(ReserveBalanceRequest reserveBalanceRequest) {
        return balanceService.reserveIfSufficient(reserveBalanceRequest.customerId(),reserveBalanceRequest.amount());
    }
    @Override
    public String describe(Boolean result) {
        return "";
    }

}
